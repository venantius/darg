(defproject darg "0.1.0-SNAPSHOT"
  :description "Simple Accomplishment Tracking for Teams"
  :min-lein-version "2.0.0"
  :url "http://darg.io"

  :license {:name "Eclipse Public License" ;; should change this
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]

                 [com.cemerick/drawbridge "0.0.6"] ;; not currently implemented

                 ;; logging
                 [org.clojure/tools.logging "0.3.0"]
                 [clj-logging-config "1.9.12"]
                 [log4j/log4j "1.2.17"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]

                 ;; HTTP client helpers
                 [cheshire "5.3.1"]
                 [clj-http "1.0.0"]

                 ;; services / integrations
                 [abengoa/clj-stripe "1.0.4"]

                 ;; db
                 [korma "0.4.0"]
                 [org.clojure/java.jdbc "0.3.5"] ;; specific for korma
                 [lobos "1.0.0-beta3"]
                 [uri "1.1.0"]
                 [org.postgresql/postgresql "9.2-1004-jdbc4"]

                 ;; webserver
                 [ring/ring-core "1.3.0"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [compojure "1.1.6"]]
  :profiles {:dev {}}

  :main darg.core
  :aot [darg.core]
  :uberjar-name "darg-0.1.jar"
  )

