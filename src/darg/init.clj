(ns darg.init
  (:require [clojure.tools.logging :as logging]
            [clojure.tools.nrepl.server :as nrepl]
            [darg.db :as db]
            [darg.fixtures :as fixtures]
            [darg.fixtures.db :as db-fixtures]
            [environ.core :as env]
            [lobos.config :as lconfig]
            [lobos.core :as lobos]))

(defn -reload-db
  "Load test fixture data so that it's available in development"
  []
  (logging/info "Rolling back the db...")
  (fixtures/silent-rollback :all)
  (logging/info "Migrating the db...")
  (fixtures/silent-migrate)
  (logging/info "Inserting test fixture data...")
  (db-fixtures/insert-db-fixture-data))

(defn configure
  "Do all the configuration that needs to happen.

  Right now that's the following:
   - Set up the logging level and pattern for the application root
   - Configure Lobos to target the right database
   - Run any migrations, as needed"
  []
  (lconfig/init)
  (db/set-korma-db)
  (if (and (= (env/env :darg-environment) :dev)
           (env/env :reload-db-on-run))
    (-reload-db))
  (logging/info "Starting nREPL server on port" 6001)
  (nrepl/start-server :port 6001))
