(ns darg.model.users
  (:require [darg.model :as db]
            [korma.core :refer :all]
            [darg.services.stormpath :as stormpath]))

; Create

(defn create-user
  "Insert a user into the database"
  [params]
  (insert db/users (values params)))

(defn create-user-from-signup-form
  "Convert a stormpath account to a user and write it to database"
  [account-map]
  (-> account-map
    (select-keys [:givenName :email])
      stormpath/account->user
      create-user))

(defn add-user-to-team
  [userid teamid]
  (insert db/team-users (values {:teams_id teamid :users_id userid})))

; Update

(defn update-user
  [id params]
  (update db/users (where {:id id}) (set-fields params)))

; Lookups

(defn get-user-by-field
  "Find a user in the db based on a field + value"
  [params]
  (select db/users (where params)))

(defn get-userid
  "Find just the user's id based on other information"
  [params]
  (:id (first (select db/users (fields :id) (where params)))))

(defn get-user-by-id
  "Find a user in the db based on their unique id"
  [id]
  (first (select db/users (where {:id id}))))

; Destroy

(defn delete-user
  "Delete a user from the database"
  [params]
  (delete db/users (where params)))

; User Team Membership

(defn is-user-in-team
  [userid teamid]
  (if (not (empty? (select db/team-users (where {:users_id userid :teams_id teamid})))) true false))

(defn get-user-teams
  "Gets the list of teams a user belongs to"
  [id]
  (select db/users
    (where {:id id})
    (with db/teams)))
