(defproject darg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :main darg.core
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]

                 [cheshire "5.3.1"]
                 [clj-http "1.0.0"]

                 ;; services / integrations
                 [abengoa/clj-stripe "1.0.4"]

                 ;; db
                 [korma "0.3.0"]
                 [lobos "1.0.0-beta3"]
                 [clj-bonecp-url "0.1.1"]
                 [org.slf4j/slf4j-nop "1.7.2"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]


                 ;; webserver
                 [ring/ring-core "1.3.0"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [compojure "1.1.6"]
                 ])
