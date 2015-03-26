(ns darg.model.tasks-test
  (:require [clojure.test :refer :all]
            [darg.db-util :as dbutil]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.tasks :as tasks]))

(with-db-fixtures)

(deftest we-can-insert-task-into-db
  (tasks/create-task! {:date (dbutil/sql-date-from-subject "Sep 22 2014")
                      :user_id 2
                      :team_id 3
                      :task "Interrupt the Cellular Mitosis"})
  (is (tasks/fetch-task {:task "Interrupt the Cellular Mitosis"})))
