(ns darg.model.darg-test
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixture-data]
            [darg.model.darg :as darg]
            [darg.util :as util]))

(with-db-fixtures)

(deftest timeline-works
  (is (= (darg/personal-timeline 4 1)
         [{:date (util/sql-datetime->date-str (:date fixture-data/test-task-1))
           :tasks (list (assoc fixture-data/test-task-1 :id 1))}
          {:date (util/sql-datetime->date-str (:date fixture-data/test-task-3))
           :tasks (list (assoc fixture-data/test-task-3 :id 3)
                        (assoc fixture-data/test-task-5 :id 5))}
          {:date (util/sql-datetime->date-str
                   (c/to-sql-date
                     (t/minus (t/today) (t/days 2))))
           :tasks (list)}
          {:date (util/sql-datetime->date-str
                   (c/to-sql-date
                     (t/minus (t/today) (t/days 3))))
           :tasks (list)}
          {:date (util/sql-datetime->date-str
                   (c/to-sql-date
                     (t/minus (t/today) (t/days 4))))
           :tasks (list)}
          ])))

(deftest team-timeline-works
  (is (= {:user [{:task (list)
                   :name "John Lago"
                   :id 1}
                  {:task (list)
                   :name "The Couch"
                   :id 3}
                  {:task (list
                            {:task "Do a good deed everyday"
                             :team_id 1
                             :user_id 4
                             :date (:date fixture-data/test-task-1)
                             :id 1})
                   :name "Finn the Human"
                   :id 4}
                  {:task (list)
                   :name "David Jarvis"
                   :id 6}]
          :date (util/sql-datetime->date-str (:date fixture-data/test-task-1))
          }
          (first (darg/team-timeline 1)))))
