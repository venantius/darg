(ns darg.controller.user.email-confirmation-test
  (:require [clojure.test :refer :all]
            [darg.controller.user.email-confirmation :as api]))

;; should start with a confirmation token for a user that hasn't been confirmed
;; then post to that endpoint and confirm the token
;; then verify the user is updated
(deftest we-can-confirm-a-users-email
  (is (= 0 1))
  )
