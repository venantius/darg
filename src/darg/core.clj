(ns darg.core
  (:gen-class)
  (:require [clojure.tools.logging :as logging]
            [compojure.core :refer [defroutes context GET POST ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [darg.api.v1 :as api]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [darg.middleware.authentication :as authn]
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

(def darg-spa (resp/resource-response "index.html" {:root "public"}))

(defroutes routes
  ;; www
  (GET    "/"               [] darg-spa)
  (GET    "/about"          [] darg-spa)
  (GET    "/api"            [] darg-spa)
  (GET    "/faq"            [] darg-spa)
  (GET    "/integrations"   [] darg-spa)
  (GET    "/password_reset" [] darg-spa)
  (GET    "/settings"       [] darg-spa)

  ;; debug
  (GET    "/debug" request (debug request))

  ;; api - auth
  (POST   "/api/v1/login" request (api/login request))
  (GET    "/api/v1/logout" request (api/logout request))
  (POST   "/api/v1/password_reset" request (api/password-reset request))
  (POST   "/api/v1/signup" request (api/signup request))

  ;; api - email
  (POST   "/api/v1/email" request (api/email request))

  ;; api - util
  (POST   "/api/v1/gravatar" request (api/gravatar request))

  ;; api - core
  (GET    "/api/v1/darg" request (api/get-darg request))
  (POST   "/api/v1/darg" request (api/post-darg request))

  (POST   "/api/v1/task" request (api/post-task request))

  (GET    "/api/v1/user" request (api/get-user request))
  (GET    "/api/v1/user/:user-id/:resource" request (api/get-user-stuff request))
  (route/resources "/"))

(def app (-> routes
             (authn/wrap-authentication
               authn/darg-auth-fn
               :whitelist authn/darg-whitelist-fn)
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
