(ns darg.core
  (:gen-class)
  (:use darg.config)
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as ring]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]
            [darg.api.v1 :as api]))

(defn test-response
  []
  (println "HAHA!")
  "Meh")

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (GET "/butts" [] (test-response))
  (POST "/api/v1/email/" {body :body} (api/parse-forwarded-email body)))

(def app (-> routes handler/site))

(defn -main []
  (lconfig/init)
  (lobos/migrate)
  (ring/run-jetty #'app {:port (Integer. (or (System/getenv "PORT") "8080"))
                         :join? false}))
