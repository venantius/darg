(ns darg.model.team
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]))

(defmodel db/team)

(defn fetch-team-by-id
  "Returns a team map from the db
  Takes a team id as an integer"
  [id]
  (first (select db/team (where {:id id}))))

; Team Membership
(defn fetch-team-users
  "Gets the list of users for a given team"
  [id]
  (:user (first (select db/team
                       (where {:id id})
                       (with db/user)))))

