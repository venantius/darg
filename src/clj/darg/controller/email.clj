(ns darg.controller.email
  (:require [clj-time.core :as t]
            [clojure.tools.logging :as log]
            [darg.api.responses :refer [bad-request ok unauthorized]] 
            [darg.model.email :as email]
            [darg.model.user :as user]
            [darg.services.mailgun :as mailgun]
            [darg.util.datetime :as dt]
            [darg.util.stacktrace]
            [environ.core :as env]
            [ring.middleware.basic-authentication :refer 
             [basic-authentication-request]]))

(defn email
  "/api/v1/email

  E-mail parsing endpoint; only for use with Mailgun. Authenticates the e-mail
  from Mailgun, and adds a task for each newline in the :stripped-text field."
  [{:keys [params]}]
  (log/info params)
  (let [{:keys [recipient sender from subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map] :as email} params]
    (try
      (cond
        (not (mailgun/authenticate email))
        (unauthorized "Failed to authenticate email.")
        (not (email/user-can-email-this-team? sender recipient))
        (unauthorized (format "E-mails from this address <%s> are not authorized to post to this team address <%s>." sender recipient))
        :else
        (do
          (email/parse-email email)
          (ok {:message "E-mail successfully parsed."})))
      (catch Exception e
        (log/errorf "Failed to parse email with exception: %s" e)
        (darg.util.stacktrace/print-stacktrace e)
        (bad-request "Failed to parse e-mail.")))))

(defn send-email-fn
  "Sends personal and digest emails"
  [{:keys [basic-authentication] :as request}]
  (if (not basic-authentication)
    (unauthorized "Incorrect email task password")
    (let [start-time (t/now)]
      (log/info "Starting to send personal and digest emails at JVM time:" (t/now))
      (log/info "Current JVM hour" (dt/nearest-hour))
      (dorun (map (partial email/send-emails start-time) (user/fetch-user {})))
      (ok "Sent email!"))))

(defn email-auth-fn
  "Authentication function used by the basic authentication middleware
   to make sure this was triggered by our scheduler."
  [user pass]
  (= pass (env/env :email-password)))

(defn send-email
  "/api/v1/email/send
   
  Method: POST
   
  Triggers the sending of daily personal and digest e-mails."
  [request]
  (send-email-fn
    (basic-authentication-request request email-auth-fn)))
