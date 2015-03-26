(ns darg.model.teams
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]))

(defmodel db/team)

(defn fetch-team-by-id
  "Returns a team map from the db
  Takes a team id as an integer"
  [id]
  (first (select db/team (where {:id id}))))

; Update

(defn update-team
  "Updates the fields for a team.
  Takes a team-id as an integer and a map of fields + values to update."
  [id params]
  (update db/team (where {:id id}) (set-fields params)))

; Team Membership
(defn fetch-team-users
  "Gets the list of users for a given team"
  [id]
  (:darg.user (first (select db/team
                       (where {:id id})
                       (with db/user)))))

