(ns darg.model.github.repo
  "Team settings for the GitHub integration."
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [schema.core :as s]))

(defmodel db/github-repo
  {(s/optional-key :id) s/Int
   (s/optional-key :name) s/Str
   (s/optional-key :full_name) s/Str})

(defn create-or-update-github-repo!
  [repo]
  (if-let [maybe-repo (fetch-one-github-repo repo)]
    (update-github-repo!
      (:id maybe-repo)
      repo)
    (create-github-repo! repo)))
