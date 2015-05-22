(ns darg.fixtures.db
  (:require [darg.fixtures.model :as fmodel]
            [darg.db.entities :refer :all]
            [korma.core :refer :all]))

(defn insert-fixtures
  [entity fixtures]
  (doall (map #(insert entity (values %)) fixtures)))

(defn insert-user-fixture-data
  []
  (insert-fixtures user fmodel/test-users))

(defn insert-team-fixture-data
  []
  (insert-fixtures team fmodel/test-teams))

(defn insert-task-fixture-data
  []
  (insert-fixtures task fmodel/test-tasks))

(defn insert-role-fixture-data
  []
  (insert-fixtures role fmodel/test-role-pairs))

(defn insert-password-reset-token-fixture-data
  []
  (insert-fixtures password-reset-token fmodel/test-password-reset-tokens))

(defn insert-github-user-fixture-data
  []
  (insert-fixtures github-user fmodel/test-github-users))

(defn insert-test-user-email-confirmations
  []
  (insert-fixtures user-email-confirmation fmodel/test-user-email-confirmations))

(defn insert-db-fixture-data
  []
  (insert-user-fixture-data)
  (insert-team-fixture-data)
  (insert-task-fixture-data)
  (insert-role-fixture-data)
  (insert-password-reset-token-fixture-data)
  (insert-github-user-fixture-data)
  (insert-test-user-email-confirmations))
