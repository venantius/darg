(ns darg.model.role
  (:require [clojure.tools.logging :as log]
            [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.model.team.invitation :as invitation]
            [korma.core :refer [select where with]]))

(defmodel db/role {})

(defn create-role-from-token!
  [user token]
  (when-let [invitation (invitation/fetch-one-team-invitation {:token token})]
    (create-role! {:user_id (:id user)
                   :team_id (:team_id invitation)})))

(defn fetch-one-role-with-user
  "Fetch one role, with the associated user information as well."
  [role]
  (first (select db/role
                 (with db/user)
                 (where role))))

(defn role-belongs-to-user?
  [role user]
  (let [user_id (:user_id role)]
    (= (:id user) user_id)))
