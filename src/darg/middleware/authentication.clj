(ns darg.middleware.authentication
  (:require
    [clojure.tools.logging :as logging]
    [darg.api.responses :as responses]
    [ring.util.request :as req]))

(defn darg-whitelist-fn
  "A function for whitelisting particular routes.

  Whitelists our major resource routes first, then some API routes."
  [path]
  (cond
    (= path "/") true
    (.startsWith path "/css") true
    (.startsWith path "/fonts") true
    (.startsWith path "/images") true
    (.startsWith path "/js") true
    (.startsWith path "/templates") true

    (= path "/debug") true
    (= path "/api/v1/email") true
    (= path "/api/v1/login") true
    (= path "/api/v1/signup") true
    (= path "/api/v1/gravatar") true
    (= path "/api/v1/password_reset") true
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
