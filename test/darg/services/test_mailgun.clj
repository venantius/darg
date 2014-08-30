(ns darg.services.test-mailgun
  (:require [clojure.test :refer :all]
            [darg.services.mailgun :as mailgun]))

(def test-message
  {:from "test@darg.io"
   :to "demo@darg.io"
   :subject "Hi!"
   :text "This is a test"})

(def test-message2
  {:from "test@darg.io"
   :to "demo@darg.io"
   :subject "Hi!"
   :html "<html>This is a test</html>"})

;; Events API tests

(deftest get-events-works
  (is (mailgun/get-events)))

;; Messages API tests

(deftest send-message-works
  (is (mailgun/send-message test-message))
  (is (mailgun/send-message test-message2)))

(deftest delete-message-works
  (is (= 1 1)))
