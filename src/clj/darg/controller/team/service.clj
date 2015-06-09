(ns darg.controller.team.service
  "Handlers for service-related team endpoints."
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [bad-request ok unauthorized]]
            [darg.model.github :as github]
            [darg.model.github.team-settings :as gh-team-settings]
            [darg.model.team :as team]
            [darg.model.user :as user]))

(defn create!
  "Add an integration to this team."
  [{:keys [user params] :as request}]
  (log/info params)
  (let [user-id (:id user)
        {:keys [team_id type]} params
        team (team/fetch-one-team 
              (team/map->team 
               {:id team_id}))]
    (cond
      (not team)
      (bad-request "That team does not exist")
      (not (user/user-in-team? user-id (:id team)))
      (unauthorized "You are not a member of this team.")
      (= type "github")
      (ok (gh-team-settings/create-github-team-settings!
           {:darg_team_id (:id team)}))
      :else
      (bad-request "Invalid integration type."))))
