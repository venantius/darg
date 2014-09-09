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

;; /api/v1/login

(deftest i-can-login-and-it-set-my-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email stormpath-test/user-2)
                                   :password (:password stormpath-test/user-2)}))]
    (is (= (:body auth-response) "Successfully authenticated"))
    (is (= (:status auth-response) 200))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest i-can't-login-and-it-don't-set-no-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email stormpath-test/user-1)
                                   :password (:password stormpath-test/user-1)}))]
    (is (= (:body auth-response) "Failed to authenticate"))
    (is (= (:status auth-response 401)))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

;; /api/v1/logout

; TODO - TEST HERE WITH A HEADLESS BROWSER

;; api/v1/email

(deftest email-sent-to-us-is-parseable
  (is (api/parse-email test-received-params-2)))

(deftest parsed-email-is-written-to-db
  (api/parse-email f-email/test-email-2)
  ; (println (select table/tasks))
  ; (println (select table/tasks (where {:task "Dancing tiem!!"})))
  (is (not (empty? (tasks/get-task-by-params {:task "Dancing tiem!!"})))))

(deftest we-can-get-a-user's-task-list
  (api/parse-email f-email/test-email-2)
  (def test-user-id (users/get-userid {:email "domo@darg.io"}))
  (is (= (count (tasks/get-all-tasks-for-user test-user-id)) 4)))

(deftest we-can-get-a-team's-task-list
  (api/parse-email f-email/test-email-2)
  (def test-team-id (teams/get-teamid {:email "test.api@darg.io"}))
  (is (= (count (tasks/get-all-tasks-for-team test-team-id)) 4)))
  

