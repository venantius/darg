(ns darg.init
  (:require [clojure.tools.logging :as logging]
            [clojure.tools.nrepl.server :as nrepl]
            [darg.db :as db]
            [darg.db.migrations :as migrations]
            [darg.fixtures :as fixtures]
            [darg.fixtures.db :as db-fixtures]
            [environ.core :as env]))

(defn -reload-db
  "Load test fixture data so that it's available in development"
  []
  {:pre [(false?
           (= (env/env :darg-environment)
              "production"))]}
  (let [db (db/construct-db-map)]
    (logging/info "Rolling back the db...")
    (migrations/rollback-all)
    (logging/info "Migrating the db...")
    (migrations/migrate-all)
    (logging/info "Inserting test fixture data...")
    (db-fixtures/insert-db-fixture-data)))

(defn -prod-migrate
  "Migrate in production"
  []
  (let [db (db/construct-db-map)]
    (logging/info "Migrating the db...")
    (migrations/migrate-all)))

(defn configure
  "Do all the configuration that needs to happen.

  Right now that's the following:
   - Configure Korma's database settings
   - Run any migrations"
  []
  (db/set-korma-db)
  (cond (and (= (env/env :darg-environment) "dev")
             (env/env :reload-db-on-run)) (-reload-db)
        (= (env/env :darg-environment) "production")
          (-prod-migrate))
  (logging/info "Starting nREPL server on port" 6001)
  (nrepl/start-server :port 6001))
