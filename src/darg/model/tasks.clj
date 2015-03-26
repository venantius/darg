(ns darg.model.tasks
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [korma.core :refer :all]))

(defmodel db/task)

(defn create-task-list
  "Used to insert multiple tasks into the db with matching metadata
  Takes a vector of tasks and a map of metadata {:user_id :team_id :date} to apply to the tasklist"
  [tasks-list metadata]
  (dorun (map (fn
                [task]
                (create-task!
                (assoc metadata :task task)))
               tasks-list)))

(defn update-task
  "Update a task."
  [id fields]
  (update db/task (where {:id id}) (set-fields fields)))

(defn delete-task
  "Delete a task."
  [id]
  (delete db/task (where {:id id})))

(defn fetch-task-by-id
  "Returns a task based on a unique id
  Takes an id as an integer or a vector of integer ids"
  [id]
  (first (select db/task (where {:id id}))))

(defn fetch-task-id
  "Returns id for a task based on a set of fields
  Will return the first id for a task that matches"
  [params]
  (:id (first (select db/user (fields :id) (where params)))))

;; User Tasks

(defn fetch-tasks-by-user-id
  "Returns a map of tasks which are associated with a specific user-id.
  Takes a user-id as an integer"
  [user-id]
  (select db/task
    (where {:user_id user-id})
    (order :date :desc)))

;; Team Tasks

(defn fetch-tasks-by-team-id
  "Returns a map of tasks which are associated with a specific team-id.
  Takes a team-id as an integer"
  [team-id]
  (select db/task
    (where {:team_id team-id})))
