(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [lobos.core :as lobos]
            [lobos.config :as lconfig])

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (POST "/api/v1/email/" {body :body} api/parse-forwarded-email)
  (route/resources "/")
  (route/not-found "Not Found!")))

(def app (-> routes handler/site))

(defn -main []
  (lconfig/init)
  (lobos/migrate)
  (ring/run-jetty #'app {:port (Integer. (or (System/getenv "PORT") "8080"))
                         :join? false}))
