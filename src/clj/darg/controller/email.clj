(ns darg.controller.email
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [bad-request ok unauthorized]] 
            [darg.model.email :as email]
            [darg.services.mailgun :as mailgun]
            [darg.util.stacktrace]))

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

