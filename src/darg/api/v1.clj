(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
            [darg.api.responses :as responses]
            [darg.db-util :as dbutil]
            [darg.model.dargs :as dargs]
            [darg.model.email :as email]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]
            [darg.services.mailgun :as mailgun]
            [korma.core :refer :all]
            [korma.sql.fns :as ksql]
            [pandect.algo.md5 :refer :all]
            [ring.middleware.session.cookie :as cookie-store]
            [ring.middleware.session.store :as session-store]
            [slingshot.slingshot :refer [try+]]))

;; Utils

(defn authenticated?
  "Returns true if the user is not authenticated, and false if the user is authenticated"
  [{:keys [session] :as request-map}]
  (let [{:keys [email authenticated id]} session]
     (if (and id email authenticated)
       true
       false)))

;; Authentication

(defn login
  "/v1/api/login

  Authentication endpoint. if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [{:keys [params] :as request}]
  (let [{:keys [email password]} params]
    (cond
      (not (users/authenticate email password))
        {:body "Failed to authenticate"
         :session {:authenticated false}
         :status 401}
      :else
        (let [id (:id (first (users/fetch-user {:email email})))]
          {:body "Successfully authenticated"
           :cookies {"logged-in" {:value true :path "/"}}
           :session {:authenticated true :id id :email email}
           :status 200}))))

(defn logout
  "/api/v1/logout

  The other half of the authentication endpoint pair. This one clears
  your session cookie and your plaintext cookie, logging you out both
  in practice and appearance"
  [request-map]
  {:body ""
   :status 200
   :session nil
   :cookies {"logged-in" {:value false :max-age 0 :path "/"}}})

(defn password-reset
  "/api/v1/password_reset

  Methods: POST
  Initiates the password reset workflow. This will send an e-mail to the user
  with a special link that they can use to reset their password. The link will
  only remain valid for "
  [request-map]
  (let [email (-> request-map :params :email)]
    (try
      (users/send-password-reset-email email)
      (responses/ok "Success!")
      (catch Exception e
        (responses/bad-request "Password reset failed.")))))

(defn signup
  "/api/v1/signup

  Signs a user up. This creates an account in our db and authenticates
  the user."
  [request-map]
  (let [params (:params request-map)
        email (:email params)]
    (cond
      (some? (users/fetch-one-user {:email email}))
        (responses/conflict "A user with that e-mail already exists.")
      (not (every? params [:email :name :password]))
        (responses/bad-request
          "The signup form needs an e-mail, a name, and a password.")
      :else
        (let [user (users/create-user-from-signup-form params)]
          {:body {:message "Account successfully created"}
           :cookies {"logged-in" {:value true :path "/"}}
           :session {:authenticated true :email (:email params) :id (:id user)}
           :status 200}))))

;; utils

(defn gravatar
  "Get a given user's gravatar image URL"
  [request]
  (let [email (-> request :session :email)
        size (-> request :params :size)]
    (if email
      (responses/ok
        (clojure.string/join "" ["http://www.gravatar.com/avatar/"
                                 (md5 email)
                                 "?s="
                                 size]))
      (responses/ok
        (format "http://www.gravatar.com/avatar/?s=%s" size)))))

;; tasks

(defn post-task
  [request]
  (let [task (-> request :params :task)
        user-id (-> request :session :id)
        team-id (-> request :params :team-id)
        date (-> request :params :date dbutil/sql-date-from-subject)]
    (if (not (users/user-in-team? user-id team-id))
      (responses/unauthorized "Not authorized.")
      (tasks/create-task {:task task
                          :user_id user-id
                          :team_id team-id
                          :date date}))))

;; dargs

(defn get-darg
  [request]
  (let [id (-> request :session :id)]
    (responses/ok
      {:dargs (dargs/timeline id)})))

(defn post-darg
  [{:keys [params session] :as request}]
  (let [task-list (:darg params)
        user-id (:id session)
        team-id (:team-id params)
        date (-> params
                 :date
                 dbutil/sql-date-from-subject)
        metadata {:users_id user-id
                  :teams_id team-id
                  :date date}]
    (if (users/user-in-team? user-id team-id)
      (do
        (tasks/create-task-list task-list metadata)
        (responses/ok "Tasks created successfully."))
      (responses/unauthorized "User is not a registered member of this team"))))

(defn darg
  "Takes a request, identifies the request method, and routes to the appropriate function.

  GET /api/v1/darg/
  Returns a user's darg. Expects the following
  :email - taken from session cookie

  POST /api/v1/darg/
  Adds dargs for the user. Expects the following:
  :email - taken from session cookie
  :team-id - specified by user in the body of the request, takes only one team and applies to the full darg
  :date - specified by user in the body of the request, takes only one date and applies to the full darg
  :darg-list - specified by user in the body of the request, expects an array of task strings

  DELETE /api/v1/darg/
  Deletes items from a user's darg. Will only delete tasks related to the user set in the session cookie.
  :email - taken from session cookie
  :task-ids - passed as an array in the body of the request."
  [{:keys [request-method] :as request}]
  (if (not (authenticated? request))
    (responses/unauthorized "User not authenticated.")
    (cond
      (= request-method :get) (get-darg request)
      (= request-method :post) (post-darg request)
      :else (responses/method-not-allowed "Method not allowed."))))

;; v1/users

(defn get-user-profile
  [user-ids]
  (responses/ok (users/fetch-user {:id user-ids})))

(defn get-user-darg
  [team-ids user-ids]
  (responses/ok
    (tasks/fetch-task
      {:teams_id [ksql/pred-in team-ids]
       :users_id user-ids})))

(defn get-user-teams
  [team-ids]
  (responses/ok
    (teams/fetch-team
      {:id [ksql/pred-in team-ids]})))

(defn get-user
  "Verifies that a user is authenticated and has permission to view the user resource, then routes to the appropriate function
  The requesting user must share a team with the target user to see any information

  Requires in URL
  :user-id - the id of the target user for the request
  :resource - the target user resource being requested (profile, darg, teams)

  Functions are mapped as follows:

  :profile - Allows a user to view the user profile of someone else on their team.
  Profile returns the user's name, email address, and admin status

  :darg - Allows a user to view the darg list of a user on their team. They can only see dargs for teams that both users share

  :teams - Allows a user to view the list of teams they share with another user"

  [{:keys [session params] :as request}]
  (let [requestor-id (:id session)
        target-id (-> params :user-id read-string)
        function (:resource params)]
    (if (not (authenticated? request))
      (responses/unauthorized "User not authenticated.")
      (let [team-ids (mapv :id (users/team-overlap requestor-id target-id))
            user-id target-id]
        (if (empty? team-ids)
          (responses/unauthorized "Not authorized.")
          (cond
            (= function "profile") (get-user-profile user-id)
            (= function "darg") (get-user-darg team-ids user-id)
            (= function "teams") (get-user-teams team-ids)
            :else
              (responses/not-found "Resource does not exist.")))))))

(defn email
  "/api/v1/email

  E-mail parsing endpoint; only for use with Mailgun. Authenticates the e-mail
  from Mailgun, and adds a task for each newline in the :stripped-text field."
  [{:keys [params] :as request}]
  (let [{:keys [recipient sender from subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map] :as email} params]
    (try
      (cond
        (not (mailgun/authenticate email))
          (responses/unauthorized "Failed to authenticate email.")
        (not (email/user-can-email-this-team? from recipient))
          (responses/unauthorized (format "E-mails from this address <%s> are not authorized to post to this team address <%s>." from recipient))
        :else
          (do
            (email/parse-email email)
            (responses/ok {:message "E-mail successfully parsed."})))
      (catch Exception e
        (logging/errorf "Failed to parse email with exception: %s" e)
        (responses/bad-request "Failed to parse e-mail.")))))
