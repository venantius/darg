(ns darg.model.users
  (:require [darg.model :as db]
            [korma.core :refer :all]
            [darg.services.stormpath :as stormpath]))

; Create

(defn create-user
  "Insert a user into the database
  Takes a map of fields to insert into db
  Required fields:
  :email - user's unique email (string)
  :name - user's name, for display and for stormpath authentication
  :admin (optional) - identifies the user as a darg.io admin"
  [params]
  (insert db/users (values params)))

(defn create-user-from-signup-form
  "Convert a stormpath account to a user and write it to database
  Takes an account-map of a stormpath user."
  [account-map]
  (-> account-map
    (select-keys [:givenName :email])
      stormpath/account->user
      create-user))

(defn add-user-to-team
  "Adds a user-team relationship
  Takes a user-id (integer) and team-id (integer)"
  [user-id team-id]
  (insert db/team-users (values {:teams_id team-id :users_id user-id})))

; Update

(defn update-user
  "Updates the fields for a user. 
  Takes a user-id as an integer and a map of fields + values to update."
  [id params]
  (update db/users (where {:id id}) (set-fields params)))

; Lookups

(defn get-user-by-fields
  "returns a user map from the db 
  Takes a map of fields for use in db lookup"
  [fields]
  (select db/users (where fields)))

(defn get-user-id
  "Returns a user-id (integer)
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/users (fields :id) (where params)))))

(defn get-user-by-id
"Returns a user map from the db
  Takes a user-id as an integer"
  [id]
  (first (select db/users (where {:id id}))))

; Destroy

(defn delete-user
  "Deletes a user from the database
  Takes a user-id as an integer"
  [id]
  (delete db/users (where {:id id})))

; User Team Membership

(defn is-user-in-team
  "Returns boolean true/false based on whether the use is a member of a given team
  Takes a user-id (integer) and team-id (integer)"
  [userid teamid]
  (if (not (empty? (select db/team-users (where {:users_id userid :teams_id teamid})))) true false))

(defn get-user-teams
  "Returns the map of teams that a user belongs to
  Takes a user-id (integer)"
  [user-id]
  (first (select db/teams
    (where {:users_id user-id}))))
