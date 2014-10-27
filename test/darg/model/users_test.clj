(ns darg.model.users-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.users :as users]
            [darg.model.tasks :as tasks]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest we-can-insert-user-into-the-db
  (users/create-user {:email "haruko@test.com" :name "haruko" :active true})
  (is (users/fetch-user {:email "haruko@test.com"})))

(deftest we-can-update-user-in-the-db
  (users/update-user 3 {:name "irrashaimase"})
  (is (= "irrashaimase" (:name (users/fetch-user-by-id 3)))))

(deftest we-can-delete-user-from-the-db
  (users/delete-user 3)
  (is (= nil (users/fetch-user-by-id 3))))

(deftest we-can-fetch-userid
  (is (= 1 (users/fetch-user-id {:id 1}))))

(deftest we-can-fetch-user-tasks
  (is (not (empty? (tasks/fetch-tasks-by-user-id 2)))))

(deftest we-can-get-a-users-teams
  (is (= (first (users/fetch-user-teams 1)) {:email "test.api@darg.io", :name "darg", :id 1})))

(deftest we-can-check-a-user-is-in-a-team
  (is (users/user-in-team? 3 1))
  (is (not (users/user-in-team? 3 2))))

(deftest we-can-get-overlapping-teams-for-users
  (is (= 1 (:id (first (users/team-overlap 3 1)))))
  (is (empty? (users/team-overlap 2 1))))

(deftest we-can-test-users-are-on-the-same-team
  (is (users/users-on-same-team? 3 1))
  (is (not (users/users-on-same-team? 2 1))))

(deftest we-can-add-user-to-team
  (users/add-user-to-team 3 2)
  (is (users/user-in-team? 3 2)))

(deftest fetch-dates-works
 (is (= (users/fetch-task-dates 4)
         (list
           (c/to-sql-time (t/local-date 2012 05 17))
           (c/to-sql-time (t/local-date 2012 02 16))))))

(deftest fetch-tasks-by-date-works
  (let [user (users/fetch-user-by-id 4)
        date (t/local-date 2012 02 16)
        date_2 (t/local-date 2012 02 17)]
    (is (= (dissoc (first (users/fetch-tasks-by-date user date)) :id)
           {:task "Do a good deed everyday"
            :teams_id 1
            :users_id 4
            :date (c/to-sql-time (t/local-date 2012 02 16))}))
    (is (= (users/fetch-tasks-by-date user date_2)
           (list)))))
