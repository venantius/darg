(ns darg.process.email
  "A namespace for batch email-sending activity"
  (:require [clj-time.core :as t]
            [darg.db :as db]
            [darg.model.email :as email]
            [darg.model.users :as users]
            [darg.util.datetime :as dt]))

(defn within-the-hour
  "Is the provided datetime within an hour of the user's e-mail time?"
  [dt {:keys [timezone email_hour] :as user}]
  (let [email-hour (get dt/hour-map (clojure.string/lower-case email_hour))
        current-local-hour (t/hour (dt/local-time dt timezone))]
    (if (= email-hour current-local-hour) true false)))

(defn send-emails
  "Check for any emails that might need to be sent, and send them."
  [& args]
  (db/set-korma-db)
  (let [start-time (t/now)]
    (println "Current JVM time" (t/now))
    (println "Current JVM hour" (dt/nearest-hour))
    (dorun (map println (map #(within-the-hour start-time %) (users/fetch-user {}))))
    (println "Sent email!")))
