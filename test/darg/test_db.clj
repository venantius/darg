(ns darg.test-db
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.model :refer :all]))

(deftest darg-db-is-assigned
  (is korma/_default))

(deftest we-can-insert-users
  (insert users (values {:id 99, :email "domo@test.com", :username "arrigato"}))
  (is (= "arrigato" (:username (first (select users (where {:id 99}))))))
)

(deftest we-can-update-in-the-db
  (update users (set-fields {:username "irrashaimase"}) (where {:id 99}))
  (is (= "irrashaimase" (:username (first (select users (where {:id 99}))))))
  )

(deftest we-can-delete-from-the-db
  (delete users (where {:id 99}))
  (is (= nil (:username (first (select users (where {:id 99}))))))
)

