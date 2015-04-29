(ns darg.model.team-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.team :as team]
            [darg.model.task :as task]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest capitalized-emails-are-normalized
  (team/create-team! {:name "WeeabooCorp" :email "WeEaBoo@darg.io"})
  (is (some? (team/fetch-one-team {:email "weeaboo@darg.io"}))))

(deftest we-can-insert-team-into-db
  (team/create-team! {:name "krogancorp" :email "kcorp@darg.io"})
  (is (team/fetch-team {:name "krogancorp"})))

(deftest we-can-update-team-in-db
  (team/update-team! 1 {:name "Drake v. Weezy"})
  (is (= "Drake v. Weezy" (:name (team/fetch-one-team {:id 1})))))

(deftest we-can-fetch-a-teamid
  (is (= 1 (:id (team/fetch-one-team {:id 1})))))

(deftest we-can-fetch-team-tasks
  (is (not (empty? (task/fetch-tasks-by-team-id 1)))))

(deftest we-can-fetch-roles
  (is (not (empty? (team/fetch-roles 1)))))
