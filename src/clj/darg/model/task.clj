(ns darg.model.task
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.tools.logging :as log]
            [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.util.datetime :as dt]
            [korma.core :refer :all]))

(defmodel db/task {})

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

(defn parse-task-datetime
  "Given a date and a timestamp, return a datetime object with timezone
   where the date is that of the date and the time is of the timestamp."
  [date timestamp timezone]
  (let [date (c/from-string date)
        timestamp (-> (c/from-string timestamp)
                      (dt/local-time timezone))
        dt (t/date-time 
            (t/year date)
            (t/month date) 
            (t/day date)
            (t/hour timestamp)
            (t/minute timestamp)
            (t/second timestamp))]
    (c/to-sql-time (dt/as-local-date dt timezone))))
