(ns darg.api.v1)

(defn parse-forwarded-email
  "Parse an e-mail that has been forwarded by Mailgun"
  [body]
  (let [params (:params body)
        {:keys [recipient sender from subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map]} params]
    (println "BODY" body)
    (println "PARAMS" params)))
