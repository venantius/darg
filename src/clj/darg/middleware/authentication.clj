(ns darg.middleware.authentication
  (:require
   [clojure.tools.logging :as logging]
   [darg.api.responses :as responses]
   [ring.util.request :as req]))

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
      (.startsWith path "/new_password")

     (= path "/debug")
     (= path "/api/v1/email")
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

(defn wrap-authentication
  "Wraps authentication for Darg. If a user is successfually authenticated,
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
          (responses/unauthorized "User not authenticated."))))))
