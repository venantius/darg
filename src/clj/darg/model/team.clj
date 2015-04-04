(ns darg.model.team
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]))

(defmodel db/team)

(defn fetch-roles
  "Gets the list of users for a given team and their respective roles."
  [id]
  (:user (first (select db/team
                       (where {:id id})
                       (with db/user)))))

