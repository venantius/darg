(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [ring.adapter.jetty :as ring]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello, world!"})

(defn -main []
  (ring/run-jetty handler {:port 8080}))
