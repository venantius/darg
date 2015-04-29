(ns darg.controller.email-test
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [darg.controller.email :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.email :as email-fixtures]
            [darg.fixtures.model :as model-fixtures]
            [darg.model.email :as email]
            [darg.model.task :as task]
            [darg.process.server :as server]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

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
             (json/encode {:message "E-mails from this address <savelago@darg.io> are not authorized to post to this team address <jncake@mail.darg.io>."}))))))


;; look at our users, make sure the count of send-personal-email and send-digest-email lines up with when we expect.
(deftest email-send-endpoint-sends-at-the-right-times
  (is (= 0 1))
  )
