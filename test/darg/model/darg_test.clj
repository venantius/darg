(ns darg.model.darg-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixture-data]
            [darg.model.darg :as darg]
            [darg.util :as util]))

(with-db-fixtures)

(deftest team-timeline-works
  (let [task-date (c/from-sql-time 
                    (:timestamp fixture-data/test-task-1))
        year (t/year task-date)
        month (t/month task-date)
        day (t/day task-date)]
  (is (= {:user [{:task (list)
                   :name "John Lago"
                   :email "savelago@darg.io"
                   :id 1}
                  {:task (list)
                   :name "The Couch"
                   :email "davidst@darg.io"
                   :id 3}
                  {:task (list
                            {:task "Do a good deed everyday"
                             :team_id 1
                             :user_id 4
                             :timestamp (:timestamp fixture-data/test-task-1)
                             :id 1})
                   :name "Finn the Human"
                   :email "test-user2@darg.io"
                   :id 4}
                  {:task (list)
                   :name "David Jarvis"
                   :email "david@ursacorp.io"
                   :id 6}
                  {:task (list)
                   :name "Dave"
                   :email "venantius@gmail.com"
                   :id 7}]
          :date (util/sql-datetime->date-str (:timestamp fixture-data/test-task-1))}
          (first (darg/team-timeline 
                   fixture-data/test-user-4 1 
                   (t/date-time year month day)))))))
