(ns darg.db-test
  (:use darg.fixtures)
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.model :refer :all]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]))

(with-db-fixtures)

(deftest darg-db-is-assigned
  (is korma/_default))

(deftest we-can-insert-into-the-db
	(insert users (values {:id 5, :email "haruko@test.com", :first_name "haruko"}))
	(is (= "haruko" (:first_name (first (select users (where {:id 5})))))))

(deftest we-can-update-in-the-db
  (update users (set-fields {:first_name "irrashaimase"}) (where {:id 3}))
  (is (= "irrashaimase" (:first_name (first (select users (where {:id 3})))))))

(deftest we-can-delete-from-the-db
	(delete users (where{:id 3}))
	(is (= nil (first (select users (where {:id 3}))))))
