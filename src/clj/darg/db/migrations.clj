(ns darg.db.migrations
  "Migrations functions for the database."
  (:require [clojure.java.jdbc :as sql]
            [darg.db :as db]
            [ragtime.core :as ragtime]
            [ragtime.sql.database] ;; import side effects
            [ragtime.sql.resources :refer [migrations]]))

(def migration-list
  ["2015-03-24-initial-data"
   "2015-03-25-github-integration"
   "2015-04-09-role-title"
   "2015-04-21-team-invitations"
   "2015-04-21-team-invitations-expiry"
   "2015-04-24-email-confirmation"
   "2015-04-27-daily-digest"
   "2015-04-28-timestamp-tasks"
   "2015-05-01-user-opts"])

(defn migrate-all
  "Run all of our migrations."
  []
  (let [db-str (:jdbc-url (db/construct-db-map))]
    (sql/with-db-connection [db (ragtime/connection db-str)]
      (ragtime/migrate-all db ((migrations migration-list))))))

(defn- update-defined-migrations
  "Ragtime has a bug wherein it keeps a list of migrations in two places.
   This function makes sure that the atom in ragtime.core is up-to-date
   with the database table `ragtime_migrations`."
  []
  (let [db-str (:jdbc-url (db/construct-db-map))]
    (sql/with-db-connection [db (ragtime/connection db-str)]
      (let [migrations ((migrations migration-list))
            applied-ids (set (.applied-migration-ids db))]
        (doall 
          (map ragtime/remember-migration
               (filter #(applied-ids (:id %)) migrations)))))))

(defn rollback-all
  "Rollback all of our migrations."
  []
  (update-defined-migrations)
  (let [db-str (:jdbc-url (db/construct-db-map))]
    (sql/with-db-connection [db (ragtime/connection db-str)]
      (let [n (count (.applied-migration-ids db))]
        (ragtime/rollback-last db n)))))
