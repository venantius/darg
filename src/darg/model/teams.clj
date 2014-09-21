(ns darg.model.teams
  (:require [darg.model :as db :only teams]
                [korma.core :refer :all]))

; Create

(defn create-team
  "Insert a team into the database
  Takes a map of fields to insert into the db.
  Requires:
    :email - the unique email address for the team (string)
    :name - the name of the team (string)"
  [params]
  (insert db/teams (values params)))

; Getters

(defn get-team-by-fields
  "Returns a vector containing matching teams from the db
  Takes a map of fields to use in db lookup"
  [params]
  (select db/teams (where params)))

(defn get-team-id
  "Returns the id of the user based on submitted fields
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/teams (fields :id) (where params)))))

(defn get-team-by-id
  "Returns a team map from the db
  Takes a team id as an integer"
  [id]
  (first (select db/teams (where {:id id}))))

; Update

(defn update-team
  "Updates the fields for a team. 
  Takes a team-id as an integer and a map of fields + values to update."
  [id params]
  (update db/teams (where {:id id}) (set-fields params)))

; Destroy

(defn delete-team
  "Delete a team from the database
  Takes a team-id as an integer"
  [id]
  (delete db/teams (where {:id id})))

; Team Membership
(defn get-team-users
  "Gets the list of users for a given team"
  [id]
  (:users (first (select db/teams 
                   (where {:id id})
                   (with db/users)))))

