(ns darg.test-db
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [darg.db :as db]))

(deftest darg-db-is-assigned
  (is korma/_default))

(deftest we-can-insert-into-the-db
  )

(deftest we-can-delete-from-the-db)

(deftest we-can-update-in-the-db)
