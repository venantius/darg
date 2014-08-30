(ns darg.services.test-mailgun
  (:require [clojure.test :refer :all]
            [darg.services.mailgun :as mailgun]))

(def test-text-message
  {:from "test@darg.io"
   :to "demo@darg.io"
   :subject "Hi!"
   :text "This is a test"})

(def test-html-message
  {:from "test@darg.io"
   :to "demo@darg.io"
   :subject "Hi!"
   :html "<html>This is a test</html>"})

;; Messages API tests

(deftest send-message-works
  (is (mailgun/send-message test-text-message))
  (is (mailgun/send-message test-html-message)))
