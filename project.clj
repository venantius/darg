(defproject darg "0.1.0-SNAPSHOT"
  :description "Simple Accomplishment Tracking for Teams"
  :min-lein-version "2.0.0"
  :url "http://darg.io"

  :injections  [(require 'darg.injections)]
  :dependencies [[org.clojure/clojure "1.6.0"]

                 ;; repl
                 [org.clojure/tools.nrepl "0.2.5"]

                 ;; util
                 [environ "1.0.0"]

                 ;; logging
                 [org.clojure/tools.logging "0.3.0"]
                 [clj-logging-config "1.9.12"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]

                 ;; HTTP client helpers
                 [cheshire "5.3.1"]
                 [clj-http "1.0.0"]

                 ;; services / integrations
                 [abengoa/clj-stripe "1.0.4"]

                 ;; db
                 [korma "0.4.0"]
                 [lobos "1.0.0-beta3"]
                 [org.postgresql/postgresql "9.2-1004-jdbc4"]

                 ;; webserver
                 [ring "1.3.1"]
                 [compojure "1.1.8"]
                 [http-kit "2.0.0"]

                 ;; testing
                 [ring-mock "0.1.5"]]

  :plugins [[lein-lobos "1.0.0-beta1"]
            [lein-environ "1.0.0"]]
  :profiles {:dev {:env {:database-url "postgres://localhost:5432/darg"
                         :darg-environment :development}}
             :test {:env {:database-url "postgres://localhost:5432/darg_test"
                          :darg-environment :test}}
             :staging {:env {:darg-environment :staging}}
             :production {:env {:darg-environment :production}}}

  :main darg.core
  :aot [darg.core
        clojure.tools.logging.impl]
  )

