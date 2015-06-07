(ns darg.controller.team.service.github
  "Handlers for GitHub-related team functions"
  (:require [darg.api.responses :refer [bad-request ok unauthorized]] 
            [darg.middleware.authentication :refer [github-auth-fn]]
            [darg.model.github.team-settings :as team-settings]
            [darg.model.team :as team]
            [darg.model.user :as user]))

(defn create!
  "Add a GitHub integration to this team."
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
      (ok (team-settings/create-github-team-settings!
           {:darg_team_id (:id team)})))))

(defn update!
  "Update the settings for this team's GitHub integration."
  [{:keys [user params]}]
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
      (ok (team-settings/update-github-team-settings!'
            

;; TODO
            ))
      )
    )
  )
