(ns leiningen.migrate
	(:use [leiningen.core.eval :only [eval-in-project]])
	)

(defn migrate 
"Run Lobos Migrations"
	[project]
  (eval-in-project project
       
       `(lobos.connectivity/with-connection (assoc lobos.config/db :unsafe true) ; Establish a connection only when none exists (unsafe true)
       (binding [lobos.migration/*reload-migrations* true]
         (lobos.core/migrate)))
         '(require 'lobos.config 'lobos.core 'lobos.connectivity 'lobos.migration))
    )
 