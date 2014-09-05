(ns darg.fixtures
  (:require [clojure.test :refer [use-fixtures]]
            [darg.fixtures.db :as db]
            [lobos.migration :as mig]
            [lobos.config :as lconfig]
            [lobos.core :as lobos]))

(lobos/defcommand silent-migrate
  "Replicate lobos/migrate with silent on"
  [& names]
  (let [names (if (empty? names)
                (mig/pending-migrations db-spec sname)
                names)]
    (mig/do-migrations db-spec sname :up names )))

(lobos/defcommand silent-rollback
  "Replace lobos/rollback with silent on"
  [& args]
  (let [names (cond
               (empty? args)
               [(last (mig/query-migrations-table db-spec sname))]
               (= 1 (count args))
               (let [arg (first args)
                     migs (mig/query-migrations-table db-spec sname)]
                 (cond
                  (integer? arg) (take arg (reverse migs))
                  (= arg :all) migs
                  :else args))
               :else args)]
    (mig/do-migrations db-spec sname :down names )))

(defn -db-fixtures
  "Migrates the database using Lobos, and inserts fixture data into
  the database before each test"
  [test-fn]
  (silent-migrate)
  (db/insert-db-fixture-data)
  (test-fn)
  (silent-rollback :all))

(defn -initialize-db-connection-fixture
  "Initializes the DB connection for Lobos and Korma"
  [test-ns]
  (lconfig/init)
  (test-ns))

(defn with-db-fixtures
  "Use db fixtures for all tests in this namespace"
  []
  (use-fixtures :once -initialize-db-connection-fixture)
  (use-fixtures :each -db-fixtures))
