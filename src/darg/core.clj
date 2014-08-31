(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]
            ))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>"))

(defn -main []
  (lconfig/init)
  (lobos/migrate)
  (ring/run-jetty #'routes {:port (Integer. (or (System/getenv "PORT") "8080"))
                            :join? false}))
