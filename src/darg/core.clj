(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [compojure.core :refer [defroutes GET POST]]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]
            [ring.adapter.jetty :as ring]

            [darg.api.v1 :as api]))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (POST "/api/v1/email/" x api/parse-forwarded-email)
  )

(defn -main []
  (lconfig/init)
  (lobos/migrate)
  (ring/run-jetty #'routes {:port (Integer. (or (System/getenv "PORT") "8080"))
                            :join? false}))
