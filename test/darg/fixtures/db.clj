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

(defn insert-team-user-fixture-data
  []
  (insert team-user (values fmodel/test-team-user-pairs)))

(defn insert-password-reset-token-fixture-data
  []
  (insert password-reset-token (values fmodel/test-password-reset-tokens)))

(defn insert-github-user-fixture-data
  []
  (insert github-user (values fmodel/test-github-users)))

(defn insert-db-fixture-data
  []
  (insert-user-fixture-data)
  (insert-team-fixture-data)
  (insert-task-fixture-data)
  (insert-team-user-fixture-data)
  (insert-password-reset-token-fixture-data)
  
  (insert-github-user-fixture-data))

