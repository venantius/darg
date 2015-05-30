(ns darg.routes
  "Routing for the Darg app.
   
   Pay attention to trailing slashes - right now the only thing that should
   end in a slash is the site root."
  (:require [clojure.tools.logging :as log]
            [clout.core :as clout]
            [compojure.core :refer [defroutes context rfn GET POST ANY DELETE]]
            [compojure.route :as route]
            [darg.controller.auth :as auth]
            [darg.controller.darg :as darg]
            [darg.controller.email :as email]
            [darg.controller.task :as task]
            [darg.controller.team :as team]
            [darg.controller.team.role :as role]
            [darg.controller.user :as user]
            [darg.controller.user.email-confirmation :as conf]
            [darg.controller.oauth.github :as gh-oauth]
            [ring.middleware.basic-authentication :refer
             [wrap-basic-authentication]]
            [ring.util.response :refer [resource-response]]))

(defn matches-any-path?
  "Does this request match any of the paths?
   
   Expects paths to be a coll."
  [paths request]
  (some #(clout/route-matches % request) paths))

(defn darg-spa
 "Our single page app." 
  []
  (resource-response "index.html" {:root "public"}))

(defn debug [request]
  (log/info (str request))
  {:body (str request)})

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
   "/team/:team_id/timeline"
   "/team/:team_id/timeline/:date"
   "/settings"
   "/settings/:settings_page"])

(defroutes site-routes
  (rfn request 
    (when (matches-any-path? site-paths request)
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

  (POST   "/api/v1/user"                    request (user/create! request))
  (GET    "/api/v1/user/:id"                request (user/get request))
  (POST   "/api/v1/user/:id"                request (user/update! request))

  (POST   "/api/v1/user/:id/email"          request (conf/create! request))
  (POST   "/api/v1/user/:id/email/:token"   request (conf/confirm! request)))

(defroutes routes
  site-routes
  api-routes
  (ANY    "/debug"                          request (debug request))
  (GET    "/oauth/github"                   request (gh-oauth/callback request)) 
  (route/resources "/"))
