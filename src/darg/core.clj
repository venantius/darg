(ns darg.core
  (:gen-class)
  (:require [clojure.tools.logging :as logging]
            [compojure.core :refer [defroutes context GET POST ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [darg.api.v1 :as api]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [environ.core :as env]
            [org.httpkit.server :as server]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as resp]))

(defn debug [request]
  (logging/info (str request))
  {:body (str request)})

;; Pay attention to trailing slashes - right now the only thing that should end in a
;; slash is the root.

(defroutes routes
  ;; www
  (GET "/" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/about" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/api" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/faq" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/integrations" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/password_reset" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/settings" request (resp/resource-response "index.html" {:root "public"}))
  (GET "/debug" request (debug request))

  ;; api
  (POST "/api/v1/email" request (api/email request))
  (POST "/api/v1/gravatar" request (api/gravatar request))
  (POST "/api/v1/login" request (api/login request))
  (GET "/api/v1/logout" request (api/logout request))
  (POST "/api/v1/password_reset" request (api/password-reset request))
  (POST "/api/v1/signup" request (api/signup request))
  (ANY "/api/v1/darg" request (api/darg request))
  (GET  "/api/v1/user/:user-id/:resource" request (api/get-user request))
  (route/resources "/"))

(def app (-> routes
             (handler/site
               {:session {:store (cookie/cookie-store {:key (env/env :session-key)})
                          :cookie-attrs {;; :secure true -- requires https somewhere
                                         }}})
             middleware/ignore-trailing-slash
             wrap-json-response))

(defn -main []
  (init/configure)
  (let [port (Integer. (env/env :port))]
    (logging/info "Starting Darg server on port" port)
    (server/run-server #'app {:port port :join? false})))
