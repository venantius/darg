(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
    [clojure.string :only [split] ]
    [darg.db-util :refer :all]))

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
    (let [tasks (clojure.string/split (:body-plain email) #"\n   ")]
      (into (empty [])
        (for [task tasks] 
          { :user-email (:from email) 
            :team-email (:recipient email) 
            :date (sql-date-from-subject (:subject email)) 
            :task task}))))