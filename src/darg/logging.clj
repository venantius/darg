(ns darg.logging
  (:use clj-logging-config.log4j))

(defn set-logging-defaults
  "Set the logging level for the application root"
  []
  (set-loggers! :root {:level :info
                       :out :console
                       :pattern "[%p] %c %d{MM-dd-yyyy HH:mm:ss} | %m%n"}

                "com.mchange.v2"
                {:level :warn}))
