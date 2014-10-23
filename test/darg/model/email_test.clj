(ns darg.model.email-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.email :as email-fixtures]
            [darg.model.email :as email]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]))

(with-db-fixtures)

(deftest parsed-email-is-written-to-db
  (email/parse-email email-fixtures/test-email-2)
  (is (not (empty? (tasks/get-task {:task "Dancing tiem!!"}))))
  (is (not (empty? (tasks/get-task {:task "Aint it a thing?"})))))

(deftest we-can-get-a-users-task-list
  (email/parse-email email-fixtures/test-email-2)
  (let [test-user-id (users/get-user-id {:email "domo@darg.io"})]
  (is (= (count (tasks/get-tasks-by-user-id test-user-id)) 5))))

(deftest we-can-get-a-teams-task-list
  (email/parse-email email-fixtures/test-email-2)
  (let [test-team-id (teams/get-team-id {:email "test.api@darg.io"})]
    (is (= (count (tasks/get-tasks-by-team-id test-team-id)) 6))))
