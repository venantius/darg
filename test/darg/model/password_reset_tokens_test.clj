(ns darg.model.password-reset-tokens-test
  (:require [clojure.test :refer :all]
            [darg.model :refer [password-reset-token]]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.password-reset-tokens :as password-reset-tokens]
            [korma.core :refer :all]))

(with-db-fixtures)

(defn fetch-one
  "Fetch a single password reset token."
  [params]
  (first (select password-reset-token (where params))))

(deftest create!-and-fetch-one-work
  (let [token (password-reset-tokens/create! {:user_id 4})]
    (let [fetched-token (fetch-one {:user_id 4})]
      (is (= token fetched-token))
      (is (some? token))
      (is (not (empty? token)))
      (is (some? fetched-token)))))

(deftest fetch-one-valid-works
  (let [valid-token (password-reset-tokens/fetch-one-valid {:user_id 1})
        expired-token (password-reset-tokens/fetch-one-valid {:user_id 2})]
    (is (some? valid-token))
    (is (nil? expired-token))))

(deftest valid-token?-works
  (let [valid-token (fetch-one {:user_id 1})
        expired-token (fetch-one {:user_id 2})]
    (is (false? (password-reset-tokens/valid-token? expired-token)))
    (is (true? (password-reset-tokens/valid-token? valid-token)))))
