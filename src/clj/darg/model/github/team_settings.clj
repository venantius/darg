(ns darg.model.github.team-settings
  "Team settings for the GitHub integration."
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
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
