(ns darg.core
  (:gen-class)
  (:use darg.injections)
  (:require [clojure.tools.logging :as logging]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [darg.api.v1 :as api]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [org.httpkit.server :as server]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as resp]))

;; Pay attention to trailing slashes - right now the only thing that should end in a
;; slash is the root.
(defroutes routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
  (POST "/api/v1/email" x (api/parse-forwarded-email x))
  (route/resources "/"))

(def app (-> routes
             (handler/site
               {:session {:store (cookie/cookie-store {:key "california--bear"})
                          :cookie-attrs {:max-age 259200 ;; 3 days
                                         }}})
             middleware/ignore-trailing-slash))

(defn -main []
  (init/configure)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))] ;; TODO replace with env
    (logging/info "Starting Darg server on port" port)
    (server/run-server #'app {:port port :join? false})))
