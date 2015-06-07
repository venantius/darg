(ns darg.model.github.team-settings
  "Team settings for the GitHub integration."
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.model.team :as team]
            [schema.core :as s]))

(defmodel db/github-team-settings
  {(s/optional-key :id) s/Int
   (s/optional-key :darg_team_id) s/Int
   (s/optional-key :access_token_id) s/Int
   (s/optional-key :commits) s/Bool
   (s/optional-key :commit_comments) s/Bool
   (s/optional-key :pull_requests) s/Bool
   (s/optional-key :issues) s/Bool
   (s/optional-key :pr_issue_comments) s/Bool})

(defn update-with-new-access-token!
  "Update this team's GitHub settings with the associated access token."
  [{:keys [darg_team_id] :as state} at]
  {:pre [(some? (:id at))]}
  (let [{:keys [id] :as gts} (fetch-one-github-team-settings 
                               {:darg_team_id darg_team_id})]
    (update-github-team-settings!
      id
      {:access_token_id (:id at)})))
