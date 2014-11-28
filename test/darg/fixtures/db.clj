(ns darg.fixtures.db
  (:require [darg.fixtures.model :as fmodel]
            [darg.model :refer :all]
            [korma.core :refer :all]))

(defn insert-user-fixture-data
  []
  (insert users (values fmodel/test-users)))

(defn insert-team-fixture-data
  []
  (insert teams (values fmodel/test-teams)))

(defn insert-task-fixture-data
  []
  (insert tasks (values fmodel/test-tasks)))

(defn insert-team-user-fixture-data
  []
  (insert team-users (values fmodel/test-team-user-pairs)))

(defn insert-password-reset-token-fixture-data
  []
  (insert password-reset-tokens (values fmodel/test-password-reset-tokens)))

(defn insert-db-fixture-data
  []
  (insert-user-fixture-data)
  (insert-team-fixture-data)
  (insert-task-fixture-data)
  (insert-team-user-fixture-data)
  (insert-password-reset-token-fixture-data))
