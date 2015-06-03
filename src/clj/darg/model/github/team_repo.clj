(ns darg.model.github.team-repo
  "Which repositories are associated with which teams?"
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [schema.core :as s]))

(defmodel db/github-team-repo
  {(s/optional-key :id) s/Int
   (s/optional-key :darg_team_id) s/Int
   (s/optional-key :repo_id) s/Int})
