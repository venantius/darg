(ns darg.model.team
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]))

(defmodel db/team)

(defn email-from-name
  "Figure out an email address from the name"
  [n]
  (str (clojure.string/replace n #"\W" "-") "@mail.darg.io"))

(defn fetch-roles
  "Gets the list of users for a given team and their respective roles."
  [id]
  (:user (first (select db/team
                        (where {:id id})
                        (with db/user
                              (fields [:email]))))))

(defn fetch-team-roles
  [team]
  (:role (first (select db/team
                        (where team)
                        (with db/role
                              (with db/user
                                    (fields [:email])))))))
