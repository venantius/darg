(ns darg.model.tasks
  (:require [darg.model :as db]
            [korma.core :refer :all]))

;; Create
(defn create-task
  [params]
  (println params)
  (println "Inserting")
  (insert db/tasks (values params)))

(defn create-task-list
  [tasks-list metadata]
  (println "Create Task List")
  (dorun (map (fn 
         [task] 
           (create-task 
           (assoc metadata :task task))) 
       tasks-list)))

;; Update

(defn update-task
  [id params]
  (update db/tasks (where {:id id}) (set-fields params)))

;; Destroy

(defn delete-task
  [params]
  (delete db/tasks (where params)))

;; Retrieve

(defn get-task-by-params
  [params]
  (select db/tasks (where params)))

(defn get-task-by-id
  [id]
  (first (select db/tasks (where {:id id}))))

(defn get-taskid
  [params]
  (:id (first (select db/tasks (where params)))))

;; User Tasks

(defn get-all-tasks-for-user
  [id]
  (select db/tasks
    (with db/users
      (where {:id id}))))

(defn get-tasks-for-user-daterange
  [id minDate maxDate]
  (select db/tasks
    (with db/users
      (where {:id id
        ;Date is within bounds
        }))))

;(defn get-tasks-for-user-in-team
 ; [userid teamid ])

;; Team Tasks

(defn get-all-tasks-for-team
  [id]
  (select db/tasks
    (with db/teams
      (where {:id id}))))

(defn get-tasks-for-team-daterange
  [id minDate maxDate]
  (select db/tasks
    (with db/users
      (where (and {:id id}
                          (between :date [minDate maxDate]))))))

