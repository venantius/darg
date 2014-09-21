(ns darg.model.tasks
  (:require [darg.model :as db]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [korma.core :refer :all]))

;; Create
(defn create-task
  [params]
  (insert db/tasks (values params)))

(defn create-task-list
  [tasks-list metadata]
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
  [id]
  (delete db/tasks (where {:id id})))

;; Retrieve

(defn get-task-by-fields
  [params]
  (select db/tasks (where params)))

(defn get-task-by-id
  [id]
  (first (select db/tasks (where {:id id}))))

(defn get-task-id
  [params]
  (:id (first (select db/tasks (where params)))))

;; User Tasks

(defn get-tasks-by-user-id
  [user-id]
  (select db/tasks
    (where {:users_id user-id})))

(defn get-tasks-by-user-email
  [email]
  (let [uid (users/get-user-id {:email email})]
    (get-tasks-by-user-id uid)))

;; Team Tasks

(defn get-tasks-by-team-id
  [team-id]
  (select db/tasks
    (where {:teams_id team-id})))

(defn get-tasks-by-team-email
  [email]
  (let [tid (teams/get-team-id {:email email})]
    (get-tasks-by-team-id tid)))
