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
  (let [user (users/fetch-user-by-id 4)]
    (is (= (dargs/active-dates user)
           (list
             (c/to-sql-time (t/local-date 2012 05 17))
             (c/to-sql-time (t/local-date 2012 02 16)))))))

(deftest timeline-works
  (is (= (dargs/timeline 4 nil)
         [{:date "2012-05-17"
           :tasks (list (assoc fixture-data/test-task-3 :id 3)
                        (assoc fixture-data/test-task-5 :id 5))}
          {:date "2012-02-16"
           :tasks (list (assoc fixture-data/test-task-1 :id 1))}]
         )))
