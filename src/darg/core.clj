(ns darg.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.tools.logging :as logging]
            [darg.api.v1 :as api]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [org.httpkit.server :as server]
            ))

;; Pay attention to trailing slashes - right now the only thing that should end in a
;; slash is the root.
(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (POST "/api/v1/email" x (api/parse-forwarded-email x)))

(def app (-> routes
             handler/site
             middleware/ignore-trailing-slash))

(defn -main []
  (init/configure)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))] ;; TODO replace with env
    (logging/info "Starting Darg server on port" port)
    (server/run-server #'app {:port port :join? false})))
