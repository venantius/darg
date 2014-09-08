(ns darg.api.v1-test
  (:use [darg.fixtures]
        [korma.core]
        [darg.model])
  (:require [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.core :as core]
            [darg.db :as db]
            [darg.services.stormpath-test :as stormpath-test]
            [ring.mock.request :as mock-request]))

(def test-received-params-1
  ;; this is an example of what we actually get forwarded to us from Mailgun
  {:stripped-html "<p>Dancing tiem!!</p><p>Aint it a thing?</p>"
   :From "butts@darg.io"
   :message-headers [["Received", "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000",]
                     ["Mime-Version", "1.0"],
                     ["Content-Type", "text/plain; charset=\"ascii\""],
                     ["Subject", "Let's dance mofo"],
                     ["From", "butts@darg.io"],
                     ["To", "test.api@darg.io"],
                     ["Message-Id", "<20140902015129.23125.83955@darg.io>"],
                     ["Content-Transfer-Encoding", "7bit"]],
   :stripped-signature ""
   :signature "ad94075a8d99b540f4cb4aa0847eb49328bde219a67fb5639448b559a1b92102"
   :recipient "test.api@darg.io"
   :stripped-text "Dancing tiem!!
   Aint it a thing?"
   :Subject "Let's dance mofo"
   :Mime-Version 1.0
   :token "ee55af9ce04725e2b93ca5844b14621ac96de7e9144b21222f"
   :from "butts@darg.io"
   :Received "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000"
   :sender "butts@darg.io"
   :Message-Id "<20140902015129.23125.83955@darg.io>"
   :To "test.api@darg.io"
   :Content-Transfer-Encoding "7bit"
   :timestamp 1409622691
   :Content-Type "text/plain; charset=\"ascii\""
   :subject "Let's dance mofo"
   :body-plain "Dancing tiem!!
   Aint it a thing?"})

(def test-received-params-2
  ;; this is an example of what we actually get forwarded to us from Mailgun
  {:stripped-html "<p>Dancing tiem!!</p><p>Aint it a thing?</p><p>Reticulated Splines</p>"
   :From "domo@darg.io"
   :message-headers [["Received", "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000",]
                     ["Mime-Version", "1.0"],
                     ["Content-Type", "text/plain; charset=\"ascii\""],
                     ["Subject", "Send in your log for Today: Sep 06 2014"],
                     ["From", "butts@darg.io"],
                     ["To", "test.api@darg.io"],
                     ["Message-Id", "<20140902015129.23125.83955@darg.io>"],
                     ["Content-Transfer-Encoding", "7bit"]],
   :stripped-signature ""
   :signature "ad94075a8d99b540f4cb4aa0847eb49328bde219a67fb5639448b559a1b92102"
   :recipient "test.api@darg.io"
   :stripped-text "Dancing tiem!!
   Aint it a thing?
   Reticulated Splines"
   :Subject "Send in your log for Today: Sep 06 2014"
   :Mime-Version 1.0
   :token "ee55af9ce04725e2b93ca5844b14621ac96de7e9144b21222f"
   :from "domo@darg.io"
   :Received "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000"
   :sender "domo@darg.io"
   :Message-Id "<20140902015129.23125.83955@darg.io>"
   :To "test.api@darg.io"
   :Content-Transfer-Encoding "7bit"
   :timestamp 1409622691
   :Content-Type "text/plain; charset=\"ascii\""
   :subject "Send in your log for Today: Sep 06 2014"
   :body-plain "Dancing tiem!!
   Aint it a thing?
   Reticulated Splines"})

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
  (api/parse-email test-received-params-2)
  (is (select tasks (where {:task "Dancing tiem!!"})))
  (is (select tasks (where {:task "Reticulated Splines"}))))
