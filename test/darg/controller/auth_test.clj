(ns darg.controller.auth-test
  (:require [clojure.test :refer :all]
            [darg.controller.auth :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as model-fixtures]
            [darg.process.server :as server]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

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

