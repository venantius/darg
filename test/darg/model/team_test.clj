(ns darg.model.team-test
  (:require [clojure.test :refer :all]
            [darg.db-util :as dbutil]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.team :as team]
            [darg.model.task :as task]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest we-can-insert-team-into-db
  (team/create-team! {:name "krogancorp" :email "kcorp@darg.io"})
  (is (team/fetch-team {:name "krogancorp"})))

(deftest we-can-update-team-in-db
  (team/update-team! 1 {:name "Drake v. Weezy"})
  (is (= "Drake v. Weezy" (:name (team/fetch-team-by-id 1)))))

(deftest we-can-fetch-a-teamid
  (is (= 1 (:id (team/fetch-one-team {:id 1})))))

(deftest we-can-fetch-team-tasks
  (is (not (empty? (task/fetch-tasks-by-team-id 1)))))

(deftest we-can-fetch-team-users
  (is (not (empty? (team/fetch-team-users 1)))))
