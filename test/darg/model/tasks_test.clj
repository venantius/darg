(ns darg.model.tasks-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.db-util :as dbutil]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.tasks :as tasks]))

(with-db-fixtures)

(deftest we-can-insert-task-into-db
  (tasks/create-task {:date (dbutil/sql-date-from-subject "Sep 22 2014")
                      :users_id 2
                      :teams_id 3
                      :task "Interrupt the Cellular Mitosis"})
  (is (tasks/fetch-task {:task "Interrupt the Cellular Mitosis"})))

(deftest we-can-update-task-in-db
  (tasks/update-task 1 {:task "Understand the concept of love"})
  (is (= "Understand the concept of love" (:task (tasks/fetch-task-by-id 1)))))

(deftest we-can-fetch-a-taskid
  (is (= 1 (tasks/fetch-task-id {:id 1}))))
