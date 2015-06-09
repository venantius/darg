(ns darg.routes
  "Routing for the Darg app.
   
   Pay attention to trailing slashes - right now the only thing that should
   end in a slash is the site root."
  (:require [clojure.tools.logging :as log]
            [clout.core :as clout]
            [compojure.core :refer [defroutes context rfn GET PATCH POST ANY DELETE]]
            [compojure.route :as route]
            [darg.controller.auth :as auth]
            [darg.controller.darg :as darg]
            [darg.controller.email :as email]
            [darg.controller.task :as task]
            [darg.controller.team :as team]
            [darg.controller.team.role :as role]
            [darg.controller.team.service :as service]
            [darg.controller.team.service.github :as github]
            [darg.controller.user :as user]
            [darg.controller.user.email-confirmation :as conf]
            [darg.controller.user.service.github :as user-gh]
            [darg.controller.oauth.github :as gh-oauth]
            [darg.middleware.authentication :as auth-middleware]
            [ring.middleware.basic-authentication :refer
             [wrap-basic-authentication]]
            [ring.util.response :refer [resource-response]]))

(defn darg-spa
 "The Angular.js single page app." 
  []
  (resource-response "index.html" {:root "public"}))

(defn debug [request]
  (log/info (str request))
  {:status 200
   :body (str request)})

(def site-paths
  ["/"
   "/about"
   "/api"
   "/faq"
   "/integrations"
   "/login"
   "/signup"
   "/password_reset"
   "/pricing"
   "/new_password"
   "/team"
   "/team/:team_id/settings"
   "/team/:team_id/members"
   "/team/:team_id/services"
   "/team/:team_id/services/github"
   "/team/:team_id/timeline"
   "/team/:team_id/timeline/:date"
   "/settings"
   "/settings/:settings_page"])

(defroutes site-routes
  (rfn request 
    (when (auth-middleware/matches-any-path? site-paths request)
      (darg-spa))))

(defroutes api-routes
  (POST   "/api/v1/login"                   request (auth/login request))
  (GET    "/api/v1/logout"                  request (auth/logout request))
  (POST   "/api/v1/password_reset"          request (auth/password-reset request))
  (POST   "/api/v1/new_password"            request (auth/set-new-password request))
  
  (POST   "/api/v1/email"                        request (email/email request))
  (POST   "/api/v1/email/send"                   request (email/send-email request))

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

  (POST   "/api/v1/team/:team_id/services" 
       request (service/create! request)) ;; TODO: test this

  (GET    "/api/v1/team/:team_id/services/github" 
       request (github/fetch request)) ;; TODO: test this
  (PATCH  "/api/v1/team/:team_id/services/github" 
       request (github/update! request))  ;; TODO: test this

  (POST   "/api/v1/user"                    request (user/create! request))
  (GET    "/api/v1/user/:id"                request (user/get request))
  (POST   "/api/v1/user/:id"                request (user/update! request))

  (POST   "/api/v1/user/:id/email"          request (conf/create! request))
  (POST   "/api/v1/user/:id/email/:token"   request (conf/confirm! request))
  
  (GET    "/api/v1/user/services/github/repos"
       request (user-gh/fetch-repos request))) ;; TODO: test this

(defroutes routes
  site-routes
  api-routes
  (ANY    "/debug"                          request (debug request))
  (GET    "/oauth/github/login/:team_id"    request (gh-oauth/redirect request)) 
  (GET    "/oauth/github"                   request (gh-oauth/callback request)) 
  (route/resources "/"))
