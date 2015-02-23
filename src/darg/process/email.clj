(ns darg.process.email
  "A namespace for batch email-sending activity"
  (:require [clj-time.core :as t]
            [darg.init :as init]
            [darg.model.email :as email]
            [darg.model.users :as users]
            [darg.util.datetime :as dt]))

(defn within-the-hour
  [user]
  (println (dt/current-local-time (:timezone user)))
  )

(defn send-emails
  "Check for any emails that might need to be sent, and send them."
  [& args]
  (init/set-db-atoms)
  (let [start-time (t/now)]
    (println (dt/nearest-hour))
    (doall (map within-the-hour (users/fetch-user {})))
    (println "Sent email!")))
