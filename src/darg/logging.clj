(ns darg.logging
  (:use clojure.tools.logging
        clj-logging-config.log4j))

;; http://www.codejava.net/coding/common-conversion-patterns-for-log4js-patternlayout
(defn set-logging-defaults
  "Set the logging level for the application root"
  []
  (set-loggers! :root {:level :info
                       :out :console
                       :pattern "[%p] %d{MM-dd-yyyy HH:mm:ss} | %m%n"}))

(set-logging-defaults)
