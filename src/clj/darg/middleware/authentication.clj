(ns darg.middleware.authentication
  (:require
   [cemerick.url :refer [map->query url url-encode]]
   [clojure.tools.logging :as log]
   [darg.api.responses :as responses]
   [darg.routes :as routes]
   [ring.util.response :as response]
   [ring.util.request :as req]))

(defn is-site-route?
  "Is this a blacklisted site route?"
  [request]
  (routes/matches-any-path? routes/site-paths request))

(defn route-whitelist-fn
  "A function for whitelisting particular routes.

  Whitelists our major resource routes first, then some API routes."
  [path]
  (cond
    (or 
     (= path "/")
     (.startsWith path "/css")
     (.startsWith path "/fonts")
     (.startsWith path "/images")
     (.startsWith path "/js")
     (.startsWith path "/templates")

     (= path "/about")
     (= path "/api")
     (= path "/faq")
     (= path "/integrations")
     (= path "/password_reset")
     (.startsWith path "/login")
     (.startsWith path "/new_password")
     (.startsWith path "/signup")

     (= path "/debug")
     (= path "/api/v1/email")
     (= path "/api/v1/email/send")
     (= path "/api/v1/login")
     (= path "/api/v1/user")
     (= path "/api/v1/gravatar")
     (= path "/api/v1/password_reset")) true
    :else false))

(defn darg-auth-fn
  "The Darg authentication function."
  [{:keys [session] :as request}]
  (let [{:keys [email authenticated id]} session]
    (if (and id email authenticated)
      {:id id
       :email email})))

(defn redirect-to-signin
  [request]
  (let [root-target-url (req/path-info request)
        query-str (map->query (:query-params request))
        redirect-url (str root-target-url
                        (when query-str
                          (str "?" query-str)))]
    (response/redirect 
      (str "/login?redirect=" 
          (url-encode redirect-url)))))

(defn wrap-authentication
  "Wraps authentication for Darg. If a user is successfully authenticated,
  then a map of their id and email is assoc'd onto the :user key of the request
  map."
  [handler auth-fn & {:keys [whitelist]
                      :or {whitelist #{}}}]
  (fn [request]
    (if (whitelist (req/path-info request))
      (handler request)
      (let [user (auth-fn request)]
        (if user
          (handler (assoc request :user user))
          (if (is-site-route? request)
            (redirect-to-signin request)
            (responses/unauthorized "User not authenticated.")))))))
