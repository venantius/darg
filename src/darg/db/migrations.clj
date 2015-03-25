(ns darg.db.migrations
  "Migrations functions for the database."
  (:require [clojure.java.jdbc :as sql]
            [darg.db :as db]
            [ragtime.core :as ragtime]
            [ragtime.sql.database] ;; import side effects
            [ragtime.sql.files :refer [migrations]]))

(defn migrate-all
  "Run all of our migrations."
  []
  (let [db-str (:jdbc-url (db/construct-db-map))]
    (sql/with-db-connection [db (ragtime/connection db-str)]
      (ragtime/migrate-all db (migrations)))))

(defn- update-defined-migrations
  "Ragtime has a bug wherein it keeps a list of migrations in two places.
   This function makes sure that the atom in ragtime.core is up-to-date
   with the database table `ragtime_migrations`."
  []
  (let [db-str (:jdbc-url (db/construct-db-map))]
    (sql/with-db-connection [db (ragtime/connection db-str)]
      (let [migrations (ragtime.sql.files/migrations)
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

