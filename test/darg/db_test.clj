(ns darg.db-test
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.model.tasks :as tasks]
            [lobos.core :as lobos]
            [darg.db-util :as dbutil]
            [lobos.config :as lconfig]))

(with-db-fixtures)

(deftest darg-db-is-assigned
  (is korma/_default))

; User Tests

(deftest we-can-insert-user-into-the-db
  (users/create-user {:email "haruko@test.com" :name "haruko" :active true})
  (is (users/get-user {:email ["haruko@test.com"]})))

(deftest we-can-update-user-in-the-db
  (users/update-user 3 {:name "irrashaimase"})
  (is (= "irrashaimase" (:name (users/get-user-by-id 3)))))

(deftest we-can-delete-user-from-the-db
  (users/delete-user 3)
  (is (= nil (users/get-user-by-id 3))))

(deftest we-can-get-userid
  (is (= 1 (users/get-user-id {:id 1}))))

(deftest we-can-get-user-tasks
  (is (not (empty? (tasks/get-tasks-by-user-id 2)))))

(deftest we-can-check-a-user-is-in-a-team
  (is (users/user-in-team? 3 1))
  (is (not (users/user-in-team? 3 2))))

(deftest we-can-add-user-to-team
  (users/add-user-to-team 3 2)
  (is (users/user-in-team? 3 2)))

;Team Tests

(deftest we-can-insert-team-into-db
  (teams/create-team {:name "krogancorp" :email "kcorp@darg.io"})
  (is (teams/get-team {:name ["krogancorp"]})))

(deftest we-can-update-team-in-db
  (teams/update-team 1 {:name "Drake v. Weezy"})
  (is (= "Drake v. Weezy" (:name (teams/get-team-by-id 1)))))

(deftest we-can-delete-team-from-db
  (teams/delete-team 1)
  (is (= nil (teams/get-team-by-id 1))))

(deftest we-can-get-a-teamid
  (is (= 1 (teams/get-team-id {:id 1}))))

(deftest we-can-get-team-tasks
  (is (not (empty? (tasks/get-tasks-by-team-id 1)))))

(deftest we-can-get-team-users
  (is (not (empty? (teams/get-team-users 1)))))

;Task Test

(deftest we-can-insert-task-into-db
  (tasks/create-task {:date (dbutil/sql-date-from-subject "Sep 22 2014")
                      :users_id 2
                      :teams_id 3
                      :task "Interrupt the Cellular Mitosis"})
  (is (tasks/get-task {:task ["Interrupt the Cellular Mitosis"]})))

(deftest we-can-delete-task-from-db
  (tasks/delete-task 1)
  (is (not (tasks/get-task-by-id 1))))

(deftest we-can-update-task-in-db
  (tasks/update-task 1 {:task "Understand the concept of love"})
  (is (= "Understand the concept of love" (:task (tasks/get-task-by-id 1)))))

(deftest we-can-get-a-taskid
  (is (= 1 (tasks/get-task-id {:id 1}))))
