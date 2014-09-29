(ns darg.model.tasks
  (:require [darg.model :as db]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [korma.core :refer :all]))

;; Create
(defn create-task
  "Creates a task in the database
  Takes a map of fields to insert into the db
  Required Fields:
  :task - string that describes the actual completed task
  :users_id - integer that identifies the user who completed the task
  :teams_id - integer that identifies the team associated with the task
  :date - date the task was completed"
  [params]
  (insert db/tasks (values params)))

(defn create-task-list
  "Used to insert multiple tasks into the db with matching metadata
  Takes a vector of tasks and a map of metadata {:users_id :teams_id :date} to apply to the tasklist"
  [tasks-list metadata]
  (dorun (map (fn
                [task]
                (create-task
                (assoc metadata :task task)))
               tasks-list)))

;; Update

(defn update-task
  "Updates the fields for a task.
  Takes an id as an integer and a map of fields + values to update."
  [id fields]
  (update db/tasks (where {:id id}) (set-fields fields)))

;; Destroy

(defn delete-task
  "Deletes a task from the database
  Takes an id as an integer"
  [id]
  (delete db/tasks (where {:id id})))

;; Retrieve

(defn get-task
  "Returns tasks that match a set of fields, may return multiple tasks depending on the fields passed.
  Takes a map, where the key represents the field and the value is a vector of values for that field"
  [params]
  (loop [base (select* db/tasks)
         keylist (keys params)]
      (if (seq keylist)
        (recur (-> base (where {(first keylist) [in ((first keylist) params)]}))
          (rest keylist))
        (-> base (select)))))

(defn get-task-by-id
  "Returns a task based on a unique id
  Takes an id as an integer or a vector of integer ids"
  [id]
  (first (select db/tasks (where {:id id}))))

(defn get-task-id
  "Returns id for a task based on a set of fields
  Will return the first id for a task that matches"
  [params]
  (:id (first (select db/users (fields :id) (where params)))))

;; User Tasks

(defn get-tasks-by-user-id
  "Returns a map of tasks which are associated with a specific user-id.
  Takes a user-id as an integer"
  [user-id]
  (select db/tasks
    (where {:users_id user-id})
    (order :date :desc)))

;; Team Tasks

(defn get-tasks-by-team-id
  "Returns a map of tasks which are associated with a specific team-id.
  Takes a team-id as an integer"
  [team-id]
  (select db/tasks
    (where {:teams_id team-id})))
