(ns darg.model.tasks
  (:require [darg.model :as db]
            [korma.core :refer :all]))



;; User Tasks

(defn get-all-tasks-for-user
  [id]
  (select db/tasks
    (with db/users
      (where {:user-id id}))))

(defn get-user-tasks-for-daterange
  [id minDate maxDate]
  (select db/tasks
    (with db/users
      (where {:user-id id
      	"Date is within bounds"}))))

;; Team Tasks

(defn get-all-tasks-for-user
  [id]
  (select db/tasks
    (with db/teams
      (where {:team-id id}))))

(defn get-user-tasks-for-daterange
  [id minDate maxDate]
  (select db/tasks
    (with db/users
      (where {:team-id id
      	"Date is within bounds"}))))
