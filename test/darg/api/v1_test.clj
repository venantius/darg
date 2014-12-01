(ns darg.api.v1-test
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.core :as core]
            [darg.db :as db]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.email :as email-fixtures]
            [darg.fixtures.model :as model-fixtures]
            [darg.model :as table]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]
            [korma.core :refer :all]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

;; /api/v1/login

(deftest i-can-login-and-it-set-my-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email model-fixtures/test-user-4)
                                   :password "samurai"}))]
    (is (= (:body auth-response) "Successfully authenticated"))
    (is (= (:status auth-response) 200))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest i-can't-login-and-it-don't-set-no-cookies
  (let [auth-response (core/app (mock-request/request
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
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  {:email "dummy@darg.io"
                                   :password "test"
                                   :name "Crash dummy"}))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "Account successfully created"}))
    (is (= (:status auth-response) 200))
    (is (not (empty? (users/fetch-user {:email "dummy@darg.io"}))))
    (is (some #{"logged-in=true;Path=/"}
    (get (:headers auth-response) "Set-Cookie")))))

(deftest i-cant-write-the-same-thing-twice
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  model-fixtures/test-user-4))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "A user with that e-mail already exists."}))
    (is (= (:status auth-response) 409))))

(deftest signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  {:email "quasi-user@darg.io"}))]
  (is (= (json/parse-string (:body auth-response) true)
         {:message "The signup form needs an e-mail, a name, and a password."}))
  (is (= (:status auth-response) 400))
  (is (empty? (users/fetch-user {:email "quasi-user@darg.io"})))
  (is (not (some #{"logged-in=true;Path=/"}
                 (get (:headers auth-response) "Set-Cookie"))))))

;GET v1/darg

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get}
        response (api/get-darg sample-request)]
    (is (= (:status response) 200))))

;POST v1/darg

(deftest user-cant-post-to-a-team-they-arent-on
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 3
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response (api/post-darg sample-request)
        test-user-id (users/fetch-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "User not authorized."}))
    (is (= (count (tasks/fetch-tasks-by-user-id test-user-id)) 3))))

(deftest authenticated-user-can-post-a-darg
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 2
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response (api/post-darg sample-request)
        test-user-id (users/fetch-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 200))
    (is (= (:body response) "Tasks created successfully."))
    (is (= (count (tasks/fetch-tasks-by-user-id test-user-id)) 6))))

;; GET api/v1/user/:userid/darg

(deftest user-can-get-teammates-darg
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "3"}}
        response (api/get-user-darg sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (tasks/fetch-task {:teams_id 1 :users_id 3})))))

(deftest user-can-get-their-own-darg
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "4"}}
        response (api/get-user-darg sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (tasks/fetch-task {:users_id 4})))))

(deftest user-cant-see-darg-for-non-teammate
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "2"}}
        response (api/get-user-darg sample-request)]
     (is (= (:status response) 401))
     (is (= (:body response)
            {:message "Not authorized."}))))

;; GET api/v1/user/:userid/profile

(deftest user-can-get-teammates-profile
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "1"}}
        response (api/get-user-profile sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (users/fetch-one-user {:id 1})))))

(deftest user-cant-see-profile-for-non-teammate
  (let [sample-request {:user {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "2" :resource "profile"}}
        response (api/get-user-profile sample-request)]
     (is (= (:status response) 401))
     (is (= (:body response)
            {:message "Not authorized."}))))

;; api/v1/email

(deftest we-can-successfully-parse-a-posted-email
  (let [response (core/app (mock-request/request
                             :post
                             "/api/v1/email"
                             email-fixtures/test-email-2))]
    (is (= (:status response) 200))
    (is (= (:body response)
           (json/encode {:message "E-mail successfully parsed."})))
    (is (not (empty? (tasks/fetch-task {:task "Dancing tiem!!"}))))
    (is (not (empty? (tasks/fetch-task {:task "Aint it a thing?"}))))))

(deftest unauthenticated-emails-return-401
  (let [email (assoc email-fixtures/test-email-2
                     :token
                     "neener")
        response (core/app (mock-request/request
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
          response (core/app (mock-request/request
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
          response (core/app (mock-request/request
                               :post
                               "/api/v1/email"
                               email))]
      (is (= (:status response) 401))
      (is (= (:body response)
             (json/encode {:message "E-mails from this address <savelago@gmail.com> are not authorized to post to this team address <jncake@darg.io>."}))))))
