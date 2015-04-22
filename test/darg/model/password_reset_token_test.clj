(ns darg.model.password-reset-token-test
  (:require [clojure.test :refer :all]
            [darg.db.entities :refer [password-reset-token]]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.password-reset-token :as password-reset-token]
            [korma.core :refer :all]))

(with-db-fixtures)

(defn fetch-one
  "Fetch a single password reset token."
  [params]
  (first (select password-reset-token (where params))))

(deftest create!-and-fetch-one-work
  (let [token (password-reset-token/create! {:user_id 4})]
    (let [fetched-token (fetch-one {:user_id 4})]
      (is (= token fetched-token))
      (is (some? token))
      (is (not (empty? token)))
      (is (some? fetched-token)))))

(deftest fetch-one-valid-works
  (let [valid-token (password-reset-token/fetch-one-valid {:user_id 1})
        expired-token (password-reset-token/fetch-one-valid {:user_id 2})]
    (is (some? valid-token))
    (is (nil? expired-token))))
