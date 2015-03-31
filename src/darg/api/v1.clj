(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :refer [split trim]]
            [darg.api.responses :as responses]
            [darg.controller.task :as task-api]
            [darg.controller.user :as user-api]
            [darg.db-util :as dbutil]
            [darg.model.darg :as darg]
            [darg.model.email :as email]
            [darg.model.task :as task]
            [darg.model.user :as user]
            [darg.services.mailgun :as mailgun]
            [korma.core :refer :all]
            [korma.sql.fns :as ksql]
            [pandect.algo.md5 :refer :all]))

(defn login
  "/api/v1/login

  Method: POST

  Authentication endpoint. if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [{:keys [params] :as request}]
  (let [{:keys [email password]} params]
    (cond
      (not (user/authenticate email password))
      {:body "Failed to authenticate"
       :session {:authenticated false}
       :status 401}
      :else
      (let [id (:id (user/fetch-one-user {:email email}))]
        {:body "Successfully authenticated"
         :cookies {"logged-in" {:value true :path "/"}
                   "id" {:value id :path "/"}}
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
      (user/send-password-reset-email email)
      (responses/ok "Success!")
      (catch Exception e
        (responses/bad-request "Password reset failed.")))))

(def signup user-api/create!)

(def update-user user-api/update!)

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

(def post-task task-api/create!)

;; dargs

(defn get-darg
  "/api/v1/darg/:team-id

  Method: GET

  Retrieve all dargs for the current user for the target team"
  [{:keys [params user] :as request}]
  (let [team-id (-> params :team_id read-string)]
    (responses/ok
     {:darg (darg/personal-timeline (:id user) team-id)})))

(defn get-team-darg
  "/api/v1/darg/team/:team-id

  Method: GET

  Retrieve all dargs for a given team"
  [{:keys [params user]}]
  (let [team-id (-> params :team_id read-string)]
    (responses/ok
     {:darg (darg/team-timeline team-id)})))

(defn email
  "/api/v1/email

  E-mail parsing endpoint; only for use with Mailgun. Authenticates the e-mail
  from Mailgun, and adds a task for each newline in the :stripped-text field."
  [{:keys [params]}]
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

(def get-user user-api/get)
