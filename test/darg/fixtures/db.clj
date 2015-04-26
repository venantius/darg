(ns darg.fixtures.db
  (:require [darg.fixtures.model :as fmodel]
            [darg.db.entities :refer :all]
            [korma.core :refer :all]))

(defn insert-user-fixture-data
  []
  (insert user (values fmodel/test-users)))

(defn insert-team-fixture-data
  []
  (insert team (values fmodel/test-teams)))

(defn insert-task-fixture-data
  []
  (insert task (values fmodel/test-tasks)))

(defn insert-role-fixture-data
  []
  (insert role (values fmodel/test-role-pairs)))

(defn insert-password-reset-token-fixture-data
  []
  (insert password-reset-token (values fmodel/test-password-reset-tokens)))

(defn insert-github-user-fixture-data
  []
  (insert github-user (values fmodel/test-github-users)))

(defn insert-test-user-email-confirmations
  []
  (insert user-email-confirmation (values fmodel/test-user-email-confirmations)))

(defn insert-db-fixture-data
  []
  (insert-user-fixture-data)
  (insert-team-fixture-data)
  (insert-task-fixture-data)
  (insert-role-fixture-data)
  (insert-password-reset-token-fixture-data)
  (insert-github-user-fixture-data)
  (insert-test-user-email-confirmations))

