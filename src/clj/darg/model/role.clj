(ns darg.model.role
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer [select where with]]))

(defmodel db/role)

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
