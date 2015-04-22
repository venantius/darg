(ns darg.util.token-test
  (:require [clojure.test :refer :all]
            [darg.fixtures.model :as model]
            [darg.util.token :as token]))

(deftest valid-token?-works
  (is (false? (token/valid-token? model/test-password-reset-token-2)))
  (is (true? (token/valid-token? model/test-password-reset-token-1))))

