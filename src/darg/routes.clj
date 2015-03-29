(ns darg.routes
  "Routing for the Darg app.
   
   Pay attention to trailing slashes - right now the only thing that should
   end in a slash is the site root."
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes context GET POST ANY]]
            [compojure.route :as route]
            [darg.api.v1 :as api]
            [darg.oauth.github :as gh-oauth]
            [ring.util.response :refer [resource-response]]))

(def darg-spa (resource-response "index.html" {:root "public"}))

(defn debug [request]
  (log/info (str request))
  {:body (str request)})

(defroutes routes
  ;; site
  (GET    "/"                         [] darg-spa)
  (GET    "/timeline/:team-id"        [] darg-spa)
  (GET    "/about"                    [] darg-spa)
  (GET    "/api"                      [] darg-spa)
  (GET    "/faq"                      [] darg-spa)
  (GET    "/integrations"             [] darg-spa)
  (GET    "/password_reset"           [] darg-spa)
  (GET    "/settings"                 [] darg-spa)
  (GET    "/settings/:settings-page"  [] darg-spa)

  ;; debug
  (ANY    "/debug"                      request (debug request))

  ;; oauth
  (GET    "/oauth/github"               request (gh-oauth/callback request))

  ;; api
  (POST   "/api/v1/login"               request (api/login request))
  (GET    "/api/v1/logout"              request (api/logout request))
  (POST   "/api/v1/password_reset"      request (api/password-reset request))
  (POST   "/api/v1/signup"              request (api/signup request))
  (POST   "/api/v1/email"               request (api/email request))
  (POST   "/api/v1/gravatar"            request (api/gravatar request))
  (GET    "/api/v1/darg/:team-id"       request (api/get-darg request))
  (POST   "/api/v1/darg"                request (api/post-darg request))
  (GET    "/api/v1/darg/user/:user-id"  request (api/get-user-darg request))
  (GET    "/api/v1/darg/team/:team-id"  request (api/get-team-darg request))
  (POST   "/api/v1/task"                request (api/post-task request))
  (GET    "/api/v1/user"                request (api/get-user request))
  (POST   "/api/v1/user"                request (api/update-user request))
  (GET    "/api/v1/user/:user-id"       request (api/get-user-profile request))
  (route/resources "/"))
