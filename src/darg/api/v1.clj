(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
            [darg.db-util :as dbutil]
            [darg.services.stormpath :as stormpath]
            [korma.core :refer :all]
            [slingshot.slingshot :refer [try+]]
            ))

(defn login
  "/v1/api/login

  Authentication endpoint. Routes input parameters to Stormpath for
  authentication; if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [request-map]
  (let [email (-> request-map :params :email)
        password (-> request-map :params :password)]
    (try+
      (stormpath/authenticate email password)
      (logging/info "Successfully authenticated with email" email)
      {:body "Successfully authenticated"
       :cookies {"logged-in" {:value true :path "/"}}
       :session {:authenticated true}
       :status 200}
      (catch [:status 400] response
        (logging/info "Failed to authenticate with email " email)
        {:body "Failed to authenticate"
         :session {:authenticated false}
         :status 401}))))

;; our logging problem is very similar to https://github.com/iphoting/heroku-buildpack-php-tyler/issues/17
(defn parse-forwarded-email
  "Parse an e-mail that has been forwarded by Mailgun"
  [body]
  (let [params (:params body)
        {:keys [recipient sender From subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map]} params]
    (logging/info "Mailgun Params: " params)
    (logging/info "Full Mailgun POST: " body)
    (str params)))

(defn parse-email
  [email]
  (let [tasks (map str/trim (str/split (:body-plain email) #"\n"))
     email-metadata {:user-id (dbutil/get-userid "email" (:from email))
                     :team-id (dbutil/get-teamid "email" (:recipient email))
                     :date (dbutil/sql-date-from-subject (:subject email))}
     build-task-map-and-insert (fn [task] (dbutil/insert-task (assoc email-metadata (:task task))))]
    "Insert each task into the tasks db"
    (map build-task-map-and-insert [tasks])))

