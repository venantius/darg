(ns darg.model.email-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.email :as email-fixtures]
            [darg.fixtures.model :as model-fixtures]
            [darg.model.email :as email]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]))

(with-db-fixtures)

(deftest parsed-email-is-written-to-db
  (email/parse-email email-fixtures/test-email-2)
  (is (not (empty? (tasks/fetch-task {:task "Dancing tiem!!"}))))
  (is (not (empty? (tasks/fetch-task {:task "Aint it a thing?"})))))

(deftest we-can-get-a-users-task-list
  (email/parse-email email-fixtures/test-email-2)
  (let [test-user-id (users/fetch-user-id {:email "savelago@gmail.com"})]
    (is (= (count (tasks/fetch-tasks-by-user-id test-user-id)) 3))))

(deftest we-can-get-a-teams-task-list
  (email/parse-email email-fixtures/test-email-2)
  (let [test-team-id (:id (teams/fetch-one-team {:email "test.api@darg.io"}))]
    (is (= (count (tasks/fetch-tasks-by-team-id test-team-id)) 6))))

(deftest we-can-validate-email-posting-rights
  (testing "a user on the team returns true"
    (let [user model-fixtures/test-user-1
          team model-fixtures/test-team-1]
      (is (true? (email/user-can-email-this-team? (:email user) (:email team))))))
  (testing "a user not on the team returns false"
    (let [user model-fixtures/test-user-1
          team model-fixtures/test-team-3]
      (is (false? (email/user-can-email-this-team? (:email user) (:email team)))))))
