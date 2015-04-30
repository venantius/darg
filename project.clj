(defproject darg "0.1.6"
  :description "Simple Accomplishment Tracking for Teams"
  :min-lein-version "2.0.0"
  :url "http://darg.io"

  :dependencies [[org.clojure/clojure "1.6.0"]

                 ;; repl
                 [org.clojure/tools.nrepl "0.2.5"]
                 [org.clojure/tools.cli "0.3.1"]

                 ;; util
                 [environ "1.0.0"]
                 [clj-time "0.8.0"]
                 [slingshot "0.10.3"]

                 ;; crypto
                 [pandect "0.4.0" :exclusions [potemkin]]
                 [org.mindrot/jbcrypt "0.3m"]

                 ;; logging
                 [org.clojure/tools.logging "0.3.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]

                 ;; HTTP client helpers
                 [com.cemerick/url "0.1.1"]
                 [cheshire "5.3.1"]
                 [clj-http "1.0.0"]

                 ;; services / integrations
                 [abengoa/clj-stripe "1.0.4"]
                 [tentacles "0.2.7"]

                 ;; db
                 [korma "0.4.0"]
                 [ursacorp/ragtime.core "0.4.0"]
                 [ursacorp/ragtime.sql "0.4.0"]
                 [ursacorp/ragtime.sql.files "0.4.0"]
                 [ursacorp/ragtime.sql.resources "0.4.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.postgresql/postgresql "9.2-1004-jdbc4"]

                 ;; webserver
                 [ring "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring-basic-authentication "1.0.5"]
                 [compojure "1.1.8"]

                 ;; templating
                 [enlive "1.1.5"]
                 [selmer "0.8.2"]

                 ;; testing
                 [ring-mock "0.1.5"]
                 [bond "0.2.5"]]

  :plugins [[com.jakemccrary/lein-test-refresh "0.5.1"]
            [lein-environ "1.0.0"]]

  :test-selectors {:default (complement :integration)
                   :all (constantly true)
                   :integration :integration
                   :unit (complement :integration)}

  :jvm-opts  ["-Duser.timezone=UTC"]

  :source-paths ["src/clj"]

  :profiles {:dev 
             {:env {:darg-environment "dev"
                    :database-url "postgres://localhost:5432/darg"
                    :reload-db-on-run true
                    :port "8080"
                    :session-key "california--bear"
                    :email-password "huxtables"}
              :plugins [[jonase/eastwood "0.1.4"]]}

             :test 
             {:env {:darg-environment "test"
                    :database-url "postgres://localhost:5432/darg_test"
                    :port "8080"
                    :session-key "antarctica--bear"
                    :email-password "huxtables"}
              :plugins [[jonase/eastwood "0.1.4"]]
              :jvm-opts ["-Dlog4j.configuration=log4j-test.properties"]}

             :staging 
             {:env {:darg-environment "staging"}}

             :production 
             {:env {:darg-environment "production"}}

             :uberjar
             {:aot [darg.core]}}

  :uberjar-name "darg.jar"
  :repl-options {:port 6001}
  :main darg.core)
