(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
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

;; Reponses

(def no-auth-response
  {:body "User not authenticated"
   :cookies {"logged-in" {:value false :max-age 0 :path"/"}}
   :status 403})

(def access-denied-user
  {:body "You do not have access to this user"
   :status 403})

;; Utils

(defn authenticated?
  "Returns true if the user is not authenticated, and false if the user is authenticated"
  [request-map]
  (let [email (-> request-map :session :email)
        authenticated (-> request-map :session :authenticated)
        id (-> request-map :session :id)]
     (if (and id email authenticated)
       true
       false)))

;; Authentication

(defn login
  "/v1/api/login

  Authentication endpoint. if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [request-map]
  (let [email (-> request-map :params :email)
        password (-> request-map :params :password)]
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
      {:body "Success!"
       :status 200}
      (catch Exception e
        {:body "Password reset failed."
         :status 400}))))

(defn signup
  "/api/v1/signup

  Signs a user up. This creates an account in our db and authenticates
  the user."
  [request-map]
  (let [params (:params request-map)
        email (:email params)]
    (cond
      (some? (users/fetch-one-user {:email email}))
        {:status 409
         :body {:message "A user with that e-mail already exists."}}
      (not (every? params [:email :name :password]))
        {:status 400
         :body {:message "The signup form needs an e-mail, a name, and a password."}}
      :else
        (let [user (users/create-user-from-signup-form params)]
          {:body {:message "Account successfully created"}
           :cookies {"logged-in" {:value true :path "/"}}
           :session {:authenticated true :email (:email params) :id (:id user)}
           :status 200}))))

;; utils

(defn gravatar
  "Get a given user's gravatar image URL"
  [request-map]
  (let [email (-> request-map :session :email)
        size (-> request-map :params :size)]
    (if email
      {:body (clojure.string/join "" ["http://www.gravatar.com/avatar/"
                                      (md5 email)
                                      "?s="
                                      size])
       :status 200}
      {:body (format "http://www.gravatar.com/avatar/?s=%s" size)
       :status 200})))

;; tasks

(defn post-task
  [request-map]
  (let [task (-> request-map :params :task)
        user-id (-> request-map :session :id)
        team-id (-> request-map :params :team-id)
        date (-> request-map :params :date dbutil/sql-date-from-subject)]
    (if (not (users/user-in-team? user-id team-id))
      {:body "User is not a registered member of this team."
       :status 403}
      (tasks/create-task {:task task
                          :user_id user-id
                          :team_id team-id
                          :date date}))))

;; dargs

(defn get-darg
  [request-map]
  (let [id (-> request-map :session :id)]
    {:body {:dargs (dargs/timeline id)}
     :status 200}))

(defn post-darg
  [request-map]
  (let [task-list (-> request-map :params :darg)
        user-id (-> request-map :session :id)
        team-id (-> request-map :params :team-id)
        date (-> request-map
               :params
               :date
               dbutil/sql-date-from-subject)
        metadata {:users_id user-id
                  :teams_id team-id
                  :date date}]
    (if (users/user-in-team? user-id team-id)
      (do (tasks/create-task-list task-list metadata)
        {:body "Tasks Created Successfully"
         :status 200})
      {:body "User is not a registered member of this team"
       :status 403})))

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
  [request-map]
  (let [request-method (-> request-map :request-method)]
    (if (not (authenticated? request-map))
      no-auth-response
      (cond
        (= request-method :get) (get-darg request-map)
        (= request-method :post) (post-darg request-map)
        :else {:body "Method not allowed"
               :status 405}))))

;; v1/users

(defn get-user-profile
  [user-ids]
  {:body (users/fetch-user {:id user-ids})
   :status 200})

(defn get-user-darg
  [team-ids user-ids]
  {:body (tasks/fetch-task {:teams_id [ksql/pred-in team-ids] :users_id user-ids})
   :status 200})

(defn get-user-teams
  [team-ids]
  {:body (teams/fetch-team {:id [ksql/pred-in team-ids]})
   :status 200})

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

  [request-map]
  (let [requestor-id (-> request-map :session :id)
        target-id (-> request-map :params :user-id read-string)
        function (-> request-map :params :resource)]
    (if (not (authenticated? request-map))
        no-auth-response
        (let [team-ids (mapv :id (users/team-overlap requestor-id target-id))
              user-id target-id]
          (if (empty? team-ids)
              access-denied-user
              (cond
                (= function "profile") (get-user-profile user-id)
                (= function "darg") (get-user-darg team-ids user-id)
                (= function "teams") (get-user-teams team-ids)
                :else {:body "Resource does not exist"
                       :status 404}))))))

;; Email Parsing
;; our logging problem is very similar to https://github.com/iphoting/heroku-buildpack-php-tyler/issues/17

(defn email
  "/api/v1/email

  E-mail parsing endpoint; only for use with Mailgun. Authenticates the e-mail
  from Mailgun, and adds a task for each newline in the :stripped-text field."
  [request-map]
  (let [params (:params request-map)
        {:keys [recipient sender From subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map] :as email} params]
    (try
      (if (mailgun/authenticate email)
        (do
          (email/parse-email email)
          {:status 200
           :body {:message "E-mail successfully parsed."}})
        (do
          (logging/warn "Failed to authenticate e-mail")
          {:status 401
           :body {:message "Failed to authenticate email"}}))
      (catch Exception e
       (logging/errorf "Failed to parse email with exception: %s" e)
       {:status 400
        :body {:message "Failed to parse e-mail"}}))))
