(ns darg.api.v1-test
  (:require [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.core :as core]
            [darg.db :as db]
            [darg.fixtures :refer :all]
            [darg.fixtures.email :as f-email]
            [darg.model :as table]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]
            [darg.services.stormpath :as stormpath]
            [darg.services.stormpath-test :as stormpath-test] ;; TODO: refactor this
            [korma.core :refer :all]
            [ring.mock.request :as mock-request]))

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
    (is (= (:status auth-response) 401))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

;; /api/v1/logout

; TODO - TEST HERE WITH A HEADLESS BROWSER

;; /api/v1/gravatar

(deftest gravar-image-is-what-it-should-be
  (is (= (api/gravatar {:session {:email "venantius@gmail.com"}})
         {:body "http://www.gravatar.com/avatar/6b653616a592b8bdc296b0abf6207a71?s=40"
          :status 200})
      (= (api/gravatar {})
         {:body "http://www.gravatar.com/avatar"
          :status 200})))

;; /api/v1/signup

(deftest i-can-register-and-it-wrote-to-the-database-and-cookies
  (let [auth-response (core/app (mock-request/request
    :post "/api/v1/signup"
    stormpath-test/user-1))]
  (is (= (:body auth-response) "Account successfully created"))
  (is (= (:status auth-response) 200))
  (is (not (empty? (users/get-user-by-field {:email "test-user@darg.io"}))))
  (is (some #{"logged-in=true;Path=/"}
    (get (:headers auth-response) "Set-Cookie")))
  (stormpath/delete-account-by-email (:email stormpath-test/user-1))))

(deftest i-cant-write-the-same-thing-twice
  (let [auth-response (core/app (mock-request/request
    :post "/api/v1/signup"
    stormpath-test/user-2))]
  (is (= (:body auth-response) "Account already exists"))
  (is (= (:status auth-response) 409))))

(deftest signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (core/app (mock-request/request
    :post "/api/v1/signup"
    stormpath-test/quasi-user))]
  (is (= (:body auth-response) "Failed to create account"))
  (is (= (:status auth-response) 400))
  (is (empty? (users/get-user-by-field {:email "quasi-user@darg.io"})))
  (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))


;; api/v1/email

(deftest parsed-email-is-written-to-db
  (api/parse-email f-email/test-email-2)
  (is (not (empty? (tasks/get-task-by-params {:task "Dancing tiem!!"})))))

(deftest we-can-get-a-users-task-list
  (api/parse-email f-email/test-email-2)
  (let [test-user-id (users/get-userid {:email "domo@darg.io"})]
    (is (= (count (tasks/get-all-tasks-for-user test-user-id)) 5))))

(deftest we-can-get-a-teams-task-list
  (api/parse-email f-email/test-email-2)
  (let [test-team-id (teams/get-teamid {:email "test.api@darg.io"})]
    (is (= (count (tasks/get-all-tasks-for-team test-team-id)) 5))))
