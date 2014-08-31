(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [lobos.core :as lobos]
            ))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>"))

(defn -main []
  ;; (lobos/migrate) ; This makes Heroku very sad
  (ring/run-jetty #'routes {:port (Integer. (or (System/getenv "PORT") "8080"))
                            :join? false}))
