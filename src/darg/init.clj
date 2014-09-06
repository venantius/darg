(ns darg.init
  (:require [clojure.tools.logging :as logging]
            [clojure.tools.nrepl.server :as nrepl]
            [darg.db :as db]
            [lobos.config :as lconfig]
            [lobos.core :as lobos]))

(defn configure
  "Do all the configuration that needs to happen.

  Right now that's the following:
   - Set up the logging level and pattern for the application root
   - Configure Lobos to target the right database
   - Run any migrations, as needed"
  []
  (lconfig/init)
  (lobos/migrate)
  (db/set-korma-db)
  (logging/info "Starting nREPL server on port" 6001)
  (nrepl/start-server :port 6001))
