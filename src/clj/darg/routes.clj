(ns darg.routes
  "Routing for the Darg app.
   
   Pay attention to trailing slashes - right now the only thing that should
   end in a slash is the site root."
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes context GET POST ANY DELETE]]
            [compojure.route :as route]
            [darg.controller.auth :as auth]
            [darg.controller.darg :as darg]
            [darg.controller.email :as email]
            [darg.controller.task :as task]
            [darg.controller.team :as team]
            [darg.controller.team.role :as role]
            [darg.controller.user :as user]
            [darg.oauth.github :as gh-oauth]
            [ring.util.response :refer [resource-response]]))

(defn darg-spa 
  []
  (resource-response "index.html" {:root "public"}))

(defn debug [request]
  (log/info (str request))
  {:body (str request)})

(defroutes routes
  ;; site
  (GET    "/"                               [] (darg-spa))

  (GET    "/about"                          [] (darg-spa))
  (GET    "/api"                            [] (darg-spa))
  (GET    "/faq"                            [] (darg-spa))
  (GET    "/integrations"                   [] (darg-spa))

  (GET    "/signup"                         [] (darg-spa))

  (GET    "/password_reset"                 [] (darg-spa))
  (GET    "/new_password"                   [] (darg-spa))

  (GET    "/team"                           [] (darg-spa))
  (GET    "/team/:team_id"                  [] (darg-spa))
  (GET    "/team/:team_id/timeline"         [] (darg-spa))
  (GET    "/team/:team_id/timeline/:date"   [] (darg-spa))

  (GET    "/settings"                       [] (darg-spa))
  (GET    "/settings/:settings_page"        [] (darg-spa))

  ;; debug
  (ANY    "/debug"                          request (debug request))

  ;; oauth
  (GET    "/oauth/github"                   request (gh-oauth/callback request))

  ;; api
  (POST   "/api/v1/login"                   request (auth/login request))
  (GET    "/api/v1/logout"                  request (auth/logout request))
  (POST   "/api/v1/password_reset"          request (auth/password-reset request))
  
  (POST   "/api/v1/email"                        request (email/email request))

  (GET    "/api/v1/darg/:team_id"                request 
       (darg/get-darg request))
  (GET    "/api/v1/darg/team/:team_id"           request 
       (darg/get-team-darg request))
  (GET    "/api/v1/darg/team/:team_id/:date"     request 
       (darg/get-team-darg-by-date request))

  (POST   "/api/v1/task"                         request (task/create! request))

  (POST   "/api/v1/team"                         request (team/create! request))
  (GET    "/api/v1/team/:id"                     request (team/fetch request))
  (POST   "/api/v1/team/:id"                     request (team/update! request))

  (GET    "/api/v1/team/:team_id/user"           request (role/fetch-all request))
  (POST   "/api/v1/team/:team_id/user"           request (role/create! request))
  (GET    "/api/v1/team/:team_id/user/:user_id"  request (role/fetch-one request))
  (POST   "/api/v1/team/:team_id/user/:user_id"  request (role/update! request))
  (DELETE "/api/v1/team/:team_id/user/:user_id"  request (role/delete! request))

  (POST   "/api/v1/user"                    request (user/create! request))
  (GET    "/api/v1/user/:id"                request (user/get request))
  (POST   "/api/v1/user/:id"                request (user/update! request))
  
  (route/resources "/"))
