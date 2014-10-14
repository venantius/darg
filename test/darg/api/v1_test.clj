(ns darg.api.v1-test
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.core :as core]
            [darg.db :as db]
            [darg.fixtures :refer :all]
            [darg.fixtures.email :as email-fixtures]
            [darg.model :as table]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]
            [darg.services.stormpath :as stormpath]
            [darg.services.stormpath-test :as stormpath-test]
            [korma.core :refer :all]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

;; /api/v1/login

(deftest ^:integration i-can-login-and-it-set-my-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email stormpath-test/user-2)
                                   :password (:password stormpath-test/user-2)}))]
    (is (= (:body auth-response) "Successfully authenticated"))
    (is (= (:status auth-response) 200))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest ^:integration i-can't-login-and-it-don't-set-no-cookies
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
  (is (= (api/gravatar {:session {:email "venantius@gmail.com"}
                        :params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/6b653616a592b8bdc296b0abf6207a71?s=40"
          :status 200}))
  (is (= (api/gravatar {:params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/?s=40"
          :status 200})))

;; /api/v1/signup

(deftest ^:integration i-can-register-and-it-wrote-to-the-database-and-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  stormpath-test/user-1))]
  (is (= (:body auth-response) "Account successfully created"))
  (is (= (:status auth-response) 200))
  (is (not (empty? (users/get-user {:email ["test-user@darg.io"]}))))
  (is (some #{"logged-in=true;Path=/"}
    (get (:headers auth-response) "Set-Cookie")))
  (stormpath/delete-account-by-email (:email stormpath-test/user-1))))

(deftest ^:integration i-cant-write-the-same-thing-twice
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  stormpath-test/user-2))]
  (is (= (:body auth-response) "Account already exists"))
  (is (= (:status auth-response) 409))))

(deftest ^:integration signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/signup"
                                  stormpath-test/quasi-user))]
  (is (= (:body auth-response) "Failed to create account"))
  (is (= (:status auth-response) 400))
  (is (empty? (users/get-user {:email ["quasi-user@darg.io"]})))
  (is (not (some #{"logged-in=true;Path=/"}
                 (get (:headers auth-response) "Set-Cookie"))))))

;ANY v1/darg

(deftest we-can-handle-disallowed-get-methods
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :patch}
        response (api/darg sample-request)]
     (is (= (:status response) 405))
     (is (= (:body response) "Method not allowed"))))

;GET v1/darg

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get}
        response (api/darg sample-request)]
    (is (= (:status response) 200))))

(deftest unauthenticated-user-cant-view-a-darg
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io" :id 4}
                        :request-method :get}
        response (api/darg sample-request)]
    (is (= (:status response) 403))
    (is (= (:body response) "User not authenticated"))))

(deftest user-cant-view-a-darg-without-an-email
  (let [sample-request {:session {:authenticated true}
                        :request-method :get}
        response (api/darg sample-request)]
    (is (= (:status response) 403))
    (is (= (:body response) "User not authenticated"))))

;POST v1/darg

(deftest unauthenticated-user-cant-post-a-darg
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 2
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response (api/darg sample-request)
        test-user-id (users/get-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 403))
    (is (= (:body response) "User not authenticated"))
    (is (= (count (tasks/get-tasks-by-user-id test-user-id)) 3))))

(deftest user-cant-post-to-a-team-they-arent-on
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 3
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response (api/darg sample-request)
        test-user-id (users/get-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 403))
    (is (= (:body response) "User is not a registered member of this team"))
    (is (= (count (tasks/get-tasks-by-user-id test-user-id)) 3))))

(deftest authenticated-user-can-post-a-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 2
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response (api/darg sample-request)
        test-user-id (users/get-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 200))
    (is (= (:body response) "Tasks Created Successfully"))
    (is (= (count (tasks/get-tasks-by-user-id test-user-id)) 6))))

;; GET api/v1/user/:userid/
(deftest unauthenticated-user-can't-make-user-api-call
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "3" :resource "darg"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 403))
    (is (= (:body response) "User not authenticated"))))

(deftest user-recieves-404-when-submitting-invalid-function
 (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "3" :resource "magic"}}
       response (api/get-user sample-request)]
    (is (= (:status response) 404))
    (is (= (:body response) "Resource does not exist"))))


;; GET api/v1/user/:userid/darg

(deftest user-can-get-teammates-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "3" :resource "darg"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (tasks/get-task {:teams_id 1 :users_id 3})))))

(deftest user-can-get-their-own-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "4" :resource "darg"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (tasks/get-task {:users_id 4})))))

(deftest user-cant-see-darg-for-non-teammate
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "2" :resource "darg"}}
        response (api/get-user sample-request)]
     (is (= (:status response) 403))
     (is (= (:body response) "You do not have access to this user"))))

 ;; GET api/v1/user/:userid/teams

(deftest user-can-get-teammates-teams
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "3" :resource "teams"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (teams/get-team {:id 1})))))

(deftest user-cant-see-teams-for-non-teammate
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "2" :resource "teams"}}
        response (api/get-user sample-request)]
     (is (= (:status response) 403))
     (is (= (:body response) "You do not have access to this user"))))

;; GET api/v1/user/:userid/profile

(deftest user-can-get-teammates-profile
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "1" :resource "profile"}}
        response (api/get-user sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response) (users/get-user {:id 1})))))

(deftest user-cant-see-profile-for-non-teammate
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:user-id "2" :resource "profile"}}
        response (api/get-user sample-request)]
     (is (= (:status response) 403))
     (is (= (:body response) "You do not have access to this user"))))

;; api/v1/email

(deftest we-can-successfully-parse-a-posted-email
  (let [response (core/app (mock-request/request
                             :post
                             "/api/v1/email"
                             email-fixtures/test-email-2))]
    (is (= (:status response) 200))
    (is (= (:body response)
           (json/encode {:message "E-mail successfully parsed."})))
    (is (not (empty? (tasks/get-task {:task "Dancing tiem!!"}))))
    (is (not (empty? (tasks/get-task {:task "Aint it a thing?"}))))))

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
           (json/encode {:message "Failed to authenticate email"})))))
