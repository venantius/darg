(ns darg.model.users-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.users :as users]
            [korma.core :as korma]))

(with-db-fixtures)

(deftest fetch-dates-works
 (is (= (users/fetch-task-dates 4)
         (list
           (c/to-sql-time (t/local-date 2012 05 17))
           (c/to-sql-time (t/local-date 2012 02 16))))))

(deftest get-tasks-by-date-works
  (let [user (users/get-user-by-id 4)
        date (t/local-date 2012 02 16)
        date_2 (t/local-date 2012 02 17)]
    (is (= (users/get-tasks-by-date user date)
           (list {:task "Do a good deed everyday"
                  :teams_id 1
                  :users_id 4
                  :date (c/to-sql-time (t/local-date 2012 02 16))})))
    (is (= (users/get-tasks-by-date user date_2)
           (list)))))
