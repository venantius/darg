(ns darg.model.teams
  (:require [darg.model :as db :only teams]
                [korma.core :refer :all]))

; Create

(defn create-team
  "Insert a team into the database"
  [params]
  (insert db/teams (values params)))

; Getters

(defn get-team-by-field
  "Find a team in the db based on a field + value. Returns a vector containing matching teams as maps"
  [params]
  (select db/teams (where params)))

(defn get-teamid
  "Find just the team's id based on other information. Returns just the value"
  [params]
  (:id (first (select db/teams (fields :id) (where params)))))

(defn get-team-by-id
  "Find a team in the db based on their unique id"
  [id]
  (first (select db/teams (where {:id id}))))

; Update

(defn update-team
  [id params]
  (update db/teams (where {:id id}) (set-fields params)))

; Destroy

(defn delete-team
  "Delete a team from the database"
  [params]
  (delete db/teams (where params)))

; Team Membership
(defn get-team-users
  "Gets the list of users for a given team"
  [id]
  (select db/teams 
    (where {:id id})
    (with db/users)))

(defn get-team-user-ids
  [teamid]
  (map (get-team-users [teamid])))

(defn get-team-user-emails)
