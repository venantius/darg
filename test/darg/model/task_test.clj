(ns darg.model.task-test
  (:require [clojure.test :refer :all]
            [darg.db-util :as dbutil]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.task :as task]))

(with-db-fixtures)

(deftest we-can-insert-task-into-db
  (task/create-task! {:date (dbutil/sql-date-from-subject "September 22 2014")
                      :user_id 2
                      :team_id 3
                      :task "Interrupt the Cellular Mitosis"})
  (is (task/fetch-task {:task "Interrupt the Cellular Mitosis"})))
