(ns darg.controller.team.service.github
  "Handlers for GitHub-related team functions"
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [bad-request ok unauthorized]] 
            [darg.middleware.authentication :refer [github-auth-fn]]
            [darg.model.github :as github]
            [darg.model.github.team-settings :as team-settings]
            [darg.model.team :as team]
            [darg.model.user :as user]))

(defn fetch
  "Get the GitHub integration settings."
  [{:keys [user params] :as request}]
  (let [user-id (:id user)
        team (team/fetch-one-team
              (team/map->team
               {:id (:team_id params)}))]
    (cond
      (not team)
      (bad-request "That team does not exist")
      (not (user/user-in-team? user-id (:id team)))
      (unauthorized "You are not a member of this team.")
      :else
      (ok (github/fetch-teams-github-integration
           team)))))

(defn update!
  "Update the settings for this team's GitHub integration."
  [{:keys [user params]}]
  (log/info params)
  (let [user-id (:id user)
        team (team/fetch-one-team
              (team/map->team
               {:id (:team_id params)}))]
    (log/info (empty? (:access_token_id params)))
    (cond
      (not team)
      (bad-request "That team does not exist")
      (not (user/user-in-team? user-id (:id team)))
      (unauthorized "You are not a member of this team.")
      :else
      (if
       (= (:access_token_id params) "")
        (ok
         (github/remove-access-token team)) 
        (ok "stuff. tbd") ;; TODO
        ))))
