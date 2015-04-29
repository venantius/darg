(ns darg.model.task
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
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

(defn fetch-tasks-by-user-id
  "Returns a seq of tasks which are associated with a specific user-id.
  Takes a user-id as an integer"
  [user-id]
  (select db/task
    (where {:user_id user-id})
    (order :timestamp :desc)))

(defn fetch-tasks-by-team-id
  "Returns a seq of tasks which are associated with a specific team-id.
  Takes a team-id as an integer"
  [team-id]
  (select db/task
    (where {:team_id team-id})))
