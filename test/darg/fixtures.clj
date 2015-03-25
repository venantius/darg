(ns darg.fixtures
  (:require [clojure.test :refer [use-fixtures]]
            [darg.db :as db]
            [darg.db.migrations :as migrations]
            [darg.fixtures.db :as db-fixtures]))

(defn -db-fixtures
  "Migrates the database, and inserts fixture data into the database before 
   each test."
  [test-fn]
  (let [db (db/construct-db-map)]
    (migrations/rollback-all)
    (migrations/migrate-all)
    (db-fixtures/insert-db-fixture-data)
    (test-fn)
    (migrations/rollback-all)))

(defn -initialize-db-connection-fixture
  "Initializes the DB connection for Korma."
  [test-ns]
  (db/set-korma-db)
  (test-ns))

(defn with-db-fixtures
  "Use db fixtures for all tests in this namespace"
  []
  (use-fixtures :once -initialize-db-connection-fixture)
  (use-fixtures :each -db-fixtures))
