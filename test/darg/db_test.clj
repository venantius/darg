(ns darg.db-test
  (:use darg.fixtures)
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.model.tasks :as tasks]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]))

(with-db-fixtures)

(deftest darg-db-is-assigned
  (is korma/_default))

; User Tests

(deftest we-can-insert-user-into-the-db
  (users/create-user {:email "haruko@test.com" :first_name "haruko"})
  (is (users/get-user-by-field {:email "haruko@test.com"})))

(deftest we-can-update-user-in-the-db
  (users/update-user 3 {:first_name "irrashaimase"})
  (is (= "irrashaimase" (:first_name (users/get-user-by-id 3)))))

(deftest we-can-delete-user-from-the-db
  (println (users/get-user-by-id 3))
  (users/delete-user {:id 3})
  (is (= nil (users/get-user-by-id 3))))

(deftest we-can-get-user-tasks
  (println (tasks/get-all-tasks-for-user 1))
  (is (not (empty? (tasks/get-all-tasks-for-user 1)))))

(deftest we-can-check-a-user-is-in-a-team
  (is (users/is-user-in-team 3 1))
  (is (not (users/is-user-in-team 3 2))))

(deftest we-can-add-user-to-team
  (users/add-user-to-team 3 2)
  (is (users/is-user-in-team 3 2)))

;Team Tests

(deftest we-can-insert-team-into-db
  (teams/create-team {:name "krogancorp" :email "kcorp@darg.io"})
  (is (teams/get-team-by-field {:name "krogancorp"})))

(deftest we-can-update-team-in-db
  (teams/update-team 1 {:name "Drake v. Weezy"})
  (is (= "Drake v. Weezy" (:name (teams/get-team-by-id 1)))))

(deftest we-can-delete-team-from-db
  (teams/delete-team {:id 1})
  (is (= nil (teams/get-team-by-id 1))))

(deftest we-can-get-team-tasks
  (println (tasks/get-all-tasks-for-team 1))
  (is (not (empty? (tasks/get-all-tasks-for-team 1)))))


