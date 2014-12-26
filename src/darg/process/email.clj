(ns darg.process.email
  "A namespace for batch email-sending activity"
  (:require [darg.init :as init]
            [darg.model.email :as email]))

(defn send-emails
  "Check for any emails that might need to be sent, and send them."
  [& args]
  (init/set-db-atoms)
  (println "Sent email!"))
