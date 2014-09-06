(ns darg.init
  (:require [darg.db :as db]
            [darg.logging :as logging]
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
  (lobos/migrate))
