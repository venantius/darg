(ns darg.model.teams-test
  (:require [clojure.test :refer :all]
            [darg.db-util :as dbutil]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.teams :as teams]
            [darg.model.tasks :as tasks]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest we-can-insert-team-into-db
  (teams/create-team {:name "krogancorp" :email "kcorp@darg.io"})
  (is (teams/fetch-team {:name "krogancorp"})))

(deftest we-can-update-team-in-db
  (teams/update-team 1 {:name "Drake v. Weezy"})
  (is (= "Drake v. Weezy" (:name (teams/fetch-team-by-id 1)))))

(deftest we-can-delete-team-from-db
  (teams/delete-team 1)
  (is (= nil (teams/fetch-team-by-id 1))))

(deftest we-can-fetch-a-teamid
  (is (= 1 (teams/fetch-team-id {:id 1}))))

(deftest we-can-fetch-team-tasks
  (is (not (empty? (tasks/fetch-tasks-by-team-id 1)))))

(deftest we-can-fetch-team-users
  (is (not (empty? (teams/fetch-team-users 1)))))
