(ns darg.api.v1-test
  (:use [darg.fixtures]
        [korma.core])
  (:require [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.db :as db]
            [darg.fixtures.email :as f-email]
            [darg.model.tasks :as tasks]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.model :as table]))

(with-db-fixtures)

(deftest parsed-email-is-written-to-db
  (api/parse-email f-email/test-email-2)
  (is (not (empty? (tasks/get-task-by-params {:task "Dancing tiem!!"})))))

(deftest we-can-get-a-users-task-list
  (api/parse-email f-email/test-email-2)
  (def test-user-id (users/get-userid {:email "domo@darg.io"}))
  (is (= (count (tasks/get-all-tasks-for-user test-user-id)) 5)))

(deftest we-can-get-a-teams-task-list
  (api/parse-email f-email/test-email-2)
  (def test-team-id (teams/get-teamid {:email "test.api@darg.io"}))
  (is (= (count (tasks/get-all-tasks-for-team test-team-id)) 5)))
  

