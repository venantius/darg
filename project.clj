(defproject darg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :main darg.core
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]

                 [cheshire "5.3.1"]
                 [clj-http "1.0.0"]

                 [abengoa/clj-stripe "1.0.4"]

                 [ring/ring-core "1.3.0"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [compojure "1.1.6"]
                 ])
