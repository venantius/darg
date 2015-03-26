(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :refer [split trim]]
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

(defn login
  "/v1/api/login

  Method: POST

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

  Method: GET

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

  Method: POST

  Initiates the password reset workflow. This will send an e-mail to the user
  with a special link that they can use to reset their password. The link will
  only remain valid for a finite amount of time."
  [request-map]
  (let [email (-> request-map :params :email)]
    (try
      (users/send-password-reset-email email)
      (responses/ok "Success!")
      (catch Exception e
        (responses/bad-request "Password reset failed.")))))

(defn signup
  "/api/v1/signup

  Method: POST

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

(defn update-user
  "/api/v1/user

  Method: POST

  Update a user's profile."
  [{:keys [user params session] :as request}]
  (let [email (:email user)
        current-user (users/fetch-one-user {:email email})]
    (if (or (= email (:email params))
            (nil? (users/fetch-one-user {:email (:email params)})))
      (do
        (users/update-user! (:id current-user) params)
        {:status 200
         :session (assoc session :email (:email params))
         :body current-user})
      (responses/conflict "User with that e-mail already exists"))))

(defn gravatar
  "/api/v1/gravatar

  Supports: POST

  Return a given user's gravatar image URL."
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
  "/api/v1/task

  Method: POST

  Create a task."
  [{:keys [params user] :as request}]
  (let [task (:task params)
        user-id (:id user)
        team-id (read-string (:team_id params))
        date (-> params :date dbutil/sql-date-from-task)]
    (cond
      (not (and task user-id team-id date))
      (responses/bad-request "Request needs to include 'task', 'team-id' and 'date'.")
      (not (users/user-in-team? user-id team-id))
      (responses/unauthorized "Not authorized.")
      :else
      (tasks/create-task! {:task task
                          :user_id user-id
                          :team_id team-id
                          :date date}))))

;; dargs

(defn get-darg
  "/api/v1/darg/:team-id

  Method: GET

  Retrieve all dargs for the current user for the target team"
  [{:keys [params user] :as request}]
  (let [team-id (-> params :team-id read-string)]
    (responses/ok
     {:dargs (dargs/personal-timeline (:id user) team-id)})))

(defn post-darg
  "/api/v1/darg

  Method: POST

  Creates a darg for the user. Expects the following:

    :email - taken from user authentication map
    :team-id - specified by user in the body of the request, takes only one team and applies to the full darg
    :date - specified by user in the body of the request, takes only one date and applies to the full darg
    :darg - specified by user in the body of the request, expects an array of task strings"
  [{:keys [params user] :as request}]
  (let [task-list (:darg params)
        user-id (:id user)
        team-id (:team-id params)
        date (-> params
                 :date
                 dbutil/sql-date-from-subject)
        metadata {:user_id user-id
                  :team_id team-id
                  :date date}]
    (if (users/user-in-team? user-id team-id)
      (do
        (tasks/create-task-list task-list metadata)
        (responses/ok "Tasks created successfully."))
      (responses/unauthorized "User not authorized."))))

;; TODO: Re-write this to include authorization re: teams
;; https://github.com/ursacorp/darg/issues/177
(defn get-user-darg
  "/api/v1/darg/user/:user-id

  Method: GET

  Returns a darg for a given user"
  [{:keys [params user] :as request}]
  (let [current-user-id (:id user)
        target-user-id (-> params :user-id read-string)
        team-ids (mapv :id (users/team-overlap current-user-id target-user-id))]
    (if (empty? team-ids)
      (responses/unauthorized "Not authorized.")
      (responses/ok
       (tasks/fetch-task
        {:team_id [ksql/pred-in team-ids]
         :user_id target-user-id})))))

(defn get-team-darg
  "/api/v1/darg/team/:team-id

  Method: GET

  Retrieve all dargs for a given team"
  [{:keys [params user] :as request}]
  (let [team-id (-> params :team-id read-string)]
    (responses/ok
     {:dargs (dargs/team-timeline team-id)})))

;; v1/users
;; TODO: for both get-user and get-user-profile (which should probably be
;; renamed to get-current-user and get-any-user) we should add in a list of
;; teams that they're on (as well as some thinking around which of those teams
;; they can actually see.
;; https://github.com/ursacorp/darg/issues/178

(defn get-user
  "/api/v1/user

  Method: GET

  Retrieve info on the current user."
  [{:keys [user] :as request}]
  (responses/ok
   (users/profile {:id (:id user)})))

(defn get-user-profile
  "/api/v1/user/:user-id

  Method: GET

  Retrieve info on the targeted user."
  [{:keys [params user] :as request}]
  (let [current-user-id (:id user)
        target-user-id (-> params :user-id read-string)
        team-ids (mapv :id (users/team-overlap current-user-id target-user-id))]
    (if (empty? team-ids)
      (responses/unauthorized "Not authorized.")
      (responses/ok
       (users/profile
        {:id target-user-id}
        team-ids)))))

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
