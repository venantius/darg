(ns darg.controller.user.service.github
  "Handlers for GitHub-related user functions"
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :as responses]
            [darg.model.user :as user]
            [darg.service.github :as github]))

(defn fetch-repos
  [{:keys [params user] :as request}]
  (let [user (user/fetch-one-with-github-access-token user)
        repos (github/repos user)]
    (responses/ok
     (map :full_name
          (github/repos user)))))
