(ns darg.controller.team.services.github
  "Handlers for GitHub-related team functions"
  (:require [darg.middleware.authentication :refer [github-auth-fn]])
  )

(defn fetch
  "Retrive the settings for this team's GitHub integration."
  [{:keys [user params] :as request}]
  (github-auth-fn
    request)
  {:status 200
   :body "Fine."})

(defn update!
  "Update the settings for our"
  [{:keys [user params]}]

  )
