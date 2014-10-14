(ns darg.model.users
  (:require [clj-time.coerce :as c]
            [darg.model :as db]
            [korma.core :refer :all]
            [darg.services.stormpath :as stormpath]
            [clojure.data :as data :only [diff]]))

; Create

(defn create-user
  "Insert a user into the database
  Takes a map of fields to insert into db
  Required fields:
  :email - user's unique email (string)
  :name - user's name, for display and for stormpath authentication
  :active - boolean value that determines if a user is active or inactive
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
      (assoc :active true)
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

(defn get-user
  "returns a user map from the db
  Takes a map of fields for use in db lookup"
  [params]
  (select db/users (where params)))

(defn get-one-user
  "Returns the first user from get-user"
  [params]
  (first (get-user params)))

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

(defn user-in-team?
  "Returns boolean true/false based on whether the use is a member of a given team
  Takes a user-id (integer) and team-id (integer)"
  [userid teamid]
  (if (empty? (select db/team-users (where {:users_id userid :teams_id teamid}))) false true))

(defn get-user-teams
  "Returns the map of teams that a user belongs to
  Takes a user-id (integer)"
  [user-id]
  (:teams (first (select db/users
    (where {:id user-id})
    (with db/teams)))))

(defn team-overlap
  "Returns a seq of team-maps that two users have in common
  Will return an empty seq if the users do not share any teams.
  Takes 2 user-id's (integer)"
  [userid1 userid2]
  (select db/teams 
    (fields :id :name) 
    (where (and {:id [in (subselect db/team-users 
                           (fields :teams_id) 
                           (where {:users_id userid1}))]}
                {:id [in (subselect db/team-users 
                           (fields :teams_id) 
                           (where {:users_id userid2}))]}))))

(defn users-on-same-team?
  "Returns boolean true/false based on whether user's are on the same team
  Takes 2 user-ids (integers)"
  [userid1 userid2]
  (if (= userid1 userid2)
    true
    (let [teamlist (team-overlap userid1 userid2)]
      (if (empty? teamlist)
        false
        true))))

;; tasks

(defn get-tasks-by-date
  "Find tasks for this user by date"
  [user date]
  (select db/tasks
          (fields :date :users_id :teams_id :task :id)
          (where {:users_id (:id user)
                  :date (c/to-sql-time date)})))

(defn fetch-task-dates
  "Returns a list of dates that a user posted tasks. Useful for timeline
  generation."
  [user-id]
  (let [db-results (select db/tasks
                           (fields :date)
                           (where {:users_id user-id})
                           (order :date :desc)
                           (group :date))]
    (map :date db-results)))

;; tasks

(defn get-tasks-by-date
  "Find tasks for this user by date"
  [user date]
  (select db/tasks
          (fields :id :date :users_id :teams_id :task)
          (where {:users_id (:id user)
                  :date (c/to-sql-time date)})))

(defn fetch-task-dates
  "Returns a list of dates that a user posted tasks. Useful for timeline
  generation."
  [user-id]
  (let [db-results (select db/tasks
                           (fields :date)
                           (where {:users_id user-id})
                           (order :date :desc)
                           (group :date))]
    (map :date db-results)))

