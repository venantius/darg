(ns darg.core
  (:gen-class)
  (:require [clojure.tools.logging :as logging]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [darg.api.v1 :as api]
            [darg.init :as init] ;; needs to be imported before the jetty adapter
            [lobos.core :as lobos]
            [lobos.config :as lconfig]
            [ring.adapter.jetty :as ring]))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (POST "/api/v1/email/" x (api/parse-forwarded-email x)))

(def app (-> routes handler/site))

(defn -main []
  (init/configure)
  (ring/run-jetty #'app {:port (Integer. (or (System/getenv "PORT") "8080"))
                         :join? false}))
