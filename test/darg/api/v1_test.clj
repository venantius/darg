(ns darg.api.v1-test
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.process.server :as server]
            [darg.db :as db]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.email :as email-fixtures]
            [darg.fixtures.model :as model-fixtures]
            [darg.model.task :as task]
            [darg.model.user :as user]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

;; /api/v1/login

(deftest i-can-login-and-it-set-my-cookies
  (let [auth-response (server/app (mock-request/request
                                   :post "/api/v1/login"
                                   {:email (:email model-fixtures/test-user-4)
                                    :password "samurai"}))]
    (is (= (:body auth-response) "Successfully authenticated"))
    (is (= (:status auth-response) 200))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest i-can't-login-and-it-don't-set-no-cookies
  (let [auth-response (server/app (mock-request/request
                                   :post "/api/v1/login"
                                   {:email (:email model-fixtures/test-user-5)
                                    :password (:password model-fixtures/test-user-5)}))]
    (is (= (:body auth-response) "Failed to authenticate"))
    (is (= (:status auth-response) 401))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

;; /api/v1/logout

; TODO - TEST HERE WITH A HEADLESS BROWSER
; https://github.com/ursacorp/darg/issues/161

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:team_id "1"}}
        response (api/get-darg sample-request)]
    (is (= (:status response) 200))))
