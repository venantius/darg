(ns darg.model.github
  "A meta-model namespace with logic for the GitHub API controllers."
  (:require [darg.db.entities :as db]
            [darg.model.github.team-settings :as team-settings]
            [korma.core :refer :all]))

(defn fetch-teams-github-integration
  [team]
  (let [github (first 
                (select db/github-team-settings
                        (with db/team
                              (fields)
                              (with db/github-team-repo))
                        (where {:darg_team_id (:id team)})
                        (with db/github-access-token
                              (fields)
                              (with db/github-user
                                    (fields :login)))))]
    (-> github
        (assoc :type "github"))))

(defn remove-access-token
  [team]
  (let [{:keys [id] :as settings} 
        (team-settings/fetch-one-github-team-settings
         {:darg_team_id (:id team)})]
    (team-settings/update-github-team-settings!
      id
      {:access_token_id nil})))
