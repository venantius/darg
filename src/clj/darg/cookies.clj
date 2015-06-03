(ns darg.cookies
  "Namespace for cookie-related functions"
  (:require [darg.model.user :as user]))

(defn authed-cookies
  [{:keys [id email] :as user}]
  (let [user (user/fetch-one-with-github-access-token user)]
    (merge-with merge
                {:cookies {"logged-in" {:value true :path "/"}
                           "id" {:value id :path "/"}}
                 :session {:authenticated true :id id :email email}}
                (when (:github_access_token user)
                  {:cookies {"github" {:value true :path "/"}}}))))
