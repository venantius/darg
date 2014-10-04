(ns darg.model.task-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.tasks :as tasks]))

(with-db-fixtures)

(deftest fetch-dates-works
 (is (= (tasks/fetch-active-dates 4)
         (list
           (c/to-sql-time (t/local-date 2012 05 17))
           (c/to-sql-time (t/local-date 2012 02 16))))))
