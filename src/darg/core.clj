(ns darg.core
  (:gen-class)
  (:require [darg.injections :refer :all])
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

(defn debug [request-map]
  (logging/info (str request-map))
  {:body (str request-map)})

;; Pay attention to trailing slashes - right now the only thing that should end in a
;; slash is the root.

(defroutes routes
  ;; www
  (GET "/" request-map (resp/resource-response "index.html" {:root "public"}))
  (GET "/debug" request-map (debug request-map))

  ;; api
  (POST "/api/v1/email" request-map (api/parse-forwarded-email request-map))
  (GET "/api/v1/gravatar" request-map (api/gravatar request-map))
  (POST "/api/v1/login" request-map (api/login request-map))
  (GET "/api/v1/logout" request-map (api/logout request-map))
  (POST "/api/v1/signup" request-map (api/signup request-map))
  (GET "/api/v1/darg" request-map (api/get-user-dargs request-map))
  ; (PUT "/api/v1/darg" request-map (api/add-dargs-for-user request-map))
  (POST "/api/v1/darg" request-map (api/add-dargs-for-user request-map))
  ; (DELETE "/api/v1/darg" request-map (api/delete-dargs-for-user request-map))
  (route/resources "/"))

(def app (-> routes
             (handler/site
               {:session {:store (cookie/cookie-store {:key "california--bear"}) ;; TODO -- store in env
                          :cookie-attrs {:max-age 259200}}})
             middleware/ignore-trailing-slash))

(defn -main []
  (init/configure)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))] ;; TODO replace with env
    (logging/info "Starting Darg server on port" port)
    (server/run-server #'app {:port port :join? false})))
