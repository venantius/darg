(ns darg.model.team
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]
            [schema.core :as s]))

(defmodel db/team
  {(s/optional-key :id) s/Int
   (s/optional-key :name) s/Str
   (s/optional-key :email) s/Str})

(defn email-from-name
  "Figure out an email address from the name"
  [n]
  (clojure.string/lower-case
   (str (clojure.string/replace n #"\W" "-") "@mail.darg.io")))

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

(defn fetch-one-with-services
  "Fetch a single team, with its associated services."
  [team]
  (let [team (first
               (select db/team
                       (where team)
                       (with db/github-team-settings)))]
    (assoc
      team
      :github_team_settings
      (first (:github_team_settings team)))))
