(ns darg.model.dargs-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixture-data]
            [darg.model.dargs :as dargs]
            [darg.model.users :as users]))

(with-db-fixtures)

(deftest active-dates-works
  (let [user (users/get-user-by-id 4)]
    (is (= (dargs/active-dates user)
           (list
             (c/to-sql-time (t/local-date 2012 05 17))
             (c/to-sql-time (t/local-date 2012 02 16)))))))

(deftest timeline-works
  (is (= (dargs/timeline 4)
         [{:date "2012-05-17"
           :tasks (list fixture-data/test-task-3
                        fixture-data/test-task-5)}
          {:date "2012-02-16"
           :tasks (list fixture-data/test-task-1)}]
         )))
