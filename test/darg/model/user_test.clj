(ns darg.model.user-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixtures]
            [darg.model.password-reset-token :as password-reset-token]
            [darg.model.user :as user]
            [darg.model.task :as task]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest we-can-insert-user-into-the-db
  (user/create-user! {:email "haruko@test.com" :name "haruko" :active true})
  (is (user/fetch-user {:email "haruko@test.com"})))

(deftest we-can-update-user-in-the-db
  (user/update-user! 3 {:name "irrashaimase"})
  (is (= "irrashaimase" (:name (user/fetch-user-by-id 3)))))

(deftest we-can-fetch-user-tasks
  (is (not (empty? (task/fetch-tasks-by-user-id 2)))))

(deftest we-can-get-a-users-teams
  (is (= (first (user/fetch-user-teams {:id 1})) 
         {:email "test.api@mail.darg.io", :name "Darg", :id 1})))

(deftest we-can-check-a-user-is-in-a-team
  (is (user/user-in-team? 3 1))
  (is (not (user/user-in-team? 3 2))))

(deftest we-can-get-overlapping-teams-for-users
  (is (= 1 (:id (first (user/team-overlap 3 1)))))
  (is (empty? (user/team-overlap 2 1))))

(deftest we-can-test-users-are-on-the-same-team
  (is (user/users-on-same-team? 3 1))
  (is (not (user/users-on-same-team? 2 1))))

(deftest fetch-tasks-by-team-and-date-works
  (let [user (user/fetch-user-by-id 4)
        date (c/from-sql-date (:date fixtures/test-task-1))
        date_2 (t/local-date 2012 02 17)]
    (is (= (dissoc (first (user/fetch-tasks-by-team-and-date user 1 date)) :id)
           fixtures/test-task-1))
    (is (= (user/fetch-tasks-by-team-and-date user 1 date_2)
           (list)))))

(deftest we-can-authenticate-a-user
  (let [user (user/fetch-user-by-id 4)]
    (is (true? (user/authenticate (:email user) "samurai")))))

(deftest build-password-reset-link-works
  (let [user (user/fetch-user-by-id 4)
        link (user/build-password-reset-link user)
        token (:token (password-reset-token/fetch-one-valid 
                        {:user_id (:id user)}))]
    (is (= link
           (clojure.string/join ["http://darg.herokuapp.com/new_password?token="
                                 token])))))

(deftest ^:integration send-password-reset-email-works
  ;; This is the standard "successfully sent" response from Mailgun.
  (is (= "Queued. Thank you."
         (:message (user/send-password-reset-email "test@darg.io")))))
