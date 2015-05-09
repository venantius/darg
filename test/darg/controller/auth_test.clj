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

(deftest we-can-set-a-new-password
  (let [request {:request-method :post
                 :params {:password "new_password"
                          :confirm_password "new_password"
                          :token "XBT6XI7WAHPX4NQDHBWGXPP2YCJSXS7Q"}}
        {:keys [status body] :as response} (api/set-new-password request)]
    (is (= 200 status))
    (is (= body "Okay!"))))

(deftest we-cant-set-a-new-password-when-the-token-doesnt-exist
  (let [request {:request-method :post
                 :params {:password "new_password"
                          :confirm_password "new_password"
                          :token "XBT6XI7WAHPX4NQDHBW7Q"}}
        {:keys [status body] :as response} (api/set-new-password request)]
    (is (= 400 status))
    (is (= body {:message "Invalid token."}))))

(deftest we-cant-set-a-new-password-when-the-token-has-expired
  (let [request {:request-method :post
                 :params {:password "new_password"
                          :confirm_password "new_password"
                          :token "T3HLQG5QEPDF6K26Y2OQTFJGNOD2WYI7"}}
        {:keys [status body] :as response} (api/set-new-password request)]
    (is (= 400 status))
    (is (= body {:message "Invalid token."}))))

(deftest we-cant-set-a-new-password-when-the-passwords-dont-match
  (let [request {:request-method :post
                 :params {:password "new_password"
                          :confirm_password "new_pass"
                          :token "XBT6XI7WAHPX4NQDHBWGXPP2YCJSXS7Q"}}
        {:keys [status body] :as response} (api/set-new-password request)]
    (is (= 400 status))
    (is (= body {:message "Password fields do not match."}))))
