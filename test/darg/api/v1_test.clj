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

;; /api/v1/user (POST)

(deftest update-user-works
  (let [params {:email "test-user5@darg.io"
                :name "Fiona the Human"}
        sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params params}
        response (api/update-user sample-request)]
    (is (= (:status response) 200))
    (is (some? (user/fetch-one-user {:email "test-user5@darg.io"})))))

(deftest we-cant-update-a-user-to-have-an-email-of-an-existing-user
  (let [params {:email "david@ursacorp.io"
                :name "David Jarvis"}
        sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params params}
        response (api/update-user sample-request)]
    (is (= (:status response) 409))
    (is (= 1 (count (user/fetch-user {:email "david@ursacorp.io"}))))))

;; /api/v1/gravatar

(deftest gravar-image-is-what-it-should-be
  (is (= (api/gravatar {:session {:email "venantius@gmail.com"}
                        :params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/6b653616a592b8bdc296b0abf6207a71?s=40"
          :status 200}))
  (is (= (api/gravatar {:params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/?s=40"
          :status 200})))

;; /api/v1/password_reset

;; TODO - I've validated this manually for right now and will add tests later.
;; https://github.com/ursacorp/darg/issues/162
(deftest the-password-reset-api-sends-a-reset-email)

;; /api/v1/signup

(deftest i-can-register-and-it-wrote-to-the-database-and-cookies
  (let [auth-response (server/app (mock-request/request
                                   :post "/api/v1/signup"
                                   {:email "dummy@darg.io"
                                    :password "test"
                                    :name "Crash dummy"}))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "Account successfully created"}))
    (is (= (:status auth-response) 200))
    (is (not (empty? (user/fetch-user {:email "dummy@darg.io"}))))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest i-cant-write-the-same-thing-twice
  (let [user (select-keys model-fixtures/test-user-4
                          [:email :name :password])
        auth-response (server/app (mock-request/request
                                   :post "/api/v1/signup"
                                   user))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "A user with that e-mail already exists."}))
    (is (= (:status auth-response) 409))))

(deftest signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (server/app (mock-request/request
                                   :post "/api/v1/signup"
                                   {:email "quasi-user@darg.io"}))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "The signup form needs an e-mail, a name, and a password."}))
    (is (= (:status auth-response) 400))
    (is (empty? (user/fetch-user {:email "quasi-user@darg.io"})))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

;GET v1/darg

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:team_id "1"}}
        response (api/get-darg sample-request)]
    (is (= (:status response) 200))))

;; GET api/v1/user/:userid/profile

(deftest user-can-get-teammates-profile
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user_id "1"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response)
           (user/profile {:id 1})))))

(deftest user-cant-see-profile-for-non-teammate
  (let [sample-request {:user {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user_id "2" :resource "profile"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "Not authorized."}))))

;; api/v1/email

(deftest we-can-successfully-parse-a-posted-email
  (let [response (server/app (mock-request/request
                              :post
                              "/api/v1/email"
                              email-fixtures/test-email-2))]
    (is (= (:status response) 200))
    (is (= (:body response)
           (json/encode {:message "E-mail successfully parsed."})))
    (is (not (empty? (task/fetch-task {:task "Dancing tiem!!"}))))
    (is (not (empty? (task/fetch-task {:task "Aint it a thing?"}))))))

(deftest unauthenticated-emails-return-401
  (let [email (assoc email-fixtures/test-email-2
                     :token
                     "neener")
        response (server/app (mock-request/request
                              :post
                              "/api/v1/email"
                              email))]
    (is (= (:status response) 401))
    (is (= (:body response)
           (json/encode {:message "Failed to authenticate email."})))))

(deftest a-user-can-only-post-via-email-to-a-team-they-belong-to
  (testing "a user on the team returns true"
    (let [email (assoc email-fixtures/test-email-2
                       :from
                       (:email model-fixtures/test-user-1)
                       :recipient
                       (:email model-fixtures/test-team-1))
          response (server/app (mock-request/request
                                :post
                                "/api/v1/email"
                                email))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/encode {:message "E-mail successfully parsed."})))))
  (testing "a user not on the team returns false"
    (let [email (assoc email-fixtures/test-email-2
                       :from
                       (:email model-fixtures/test-user-1)
                       :recipient
                       (:email model-fixtures/test-team-3))
          response (server/app (mock-request/request
                                :post
                                "/api/v1/email"
                                email))]
      (is (= (:status response) 401))
      (is (= (:body response)
             (json/encode {:message "E-mails from this address <savelago@gmail.com> are not authorized to post to this team address <jncake@mail.darg.io>."}))))))
