(ns darg.fixtures.db
  (:require [darg.fixtures.model :as fmodel]
            [darg.model :refer :all]
            [korma.core :refer :all]))

(defn insert-user-fixture-data
  [fixture]
  (insert users (values fixture)))

(defn insert-team-fixture-data
  [fixture]
  (insert teams (values fixture)))

(defn insert-task-fixture-data
  [fixture]
  (insert tasks (values fixture)))

(defn insert-team-user-fixture-data
  [fixture]
  (insert team-users (values fixture)))

(defn insert-db-fixture-data
  []
  (insert-user-fixture-data fmodel/test-users)
  (insert-team-fixture-data fmodel/test-teams)
  (insert-task-fixture-data fmodel/test-tasks)
  (insert-team-user-fixture-data fmodel/test-team-user-pairs))
