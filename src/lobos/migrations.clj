(ns lobos.migrations
  (:require [darg.db]
            [lobos.config :refer :all]
            [lobos.core :refer :all]
            [lobos.migration :refer [defmigration]]
            [lobos.schema :refer :all])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time]))

;; NOTE: Found this a handy reference on Lobos
;; http://vijaykiran.com/2012/01/web-application-development-with-clojure-part-2/

(defmigration add-users-table
  (up [] (create
           (table :users
                  (integer :id :auto-inc :primary-key)
                  (text :email :unique :not-null)
                  (text :name)
                  (boolean :admin (default false))
                  (boolean :active (default false)))))
  (down [] (drop (table :users))))

(defmigration add-teams-table
  (up [] (create
           (table :teams
                  (integer :id :auto-inc :primary-key)
                  (text :name :not-null)
                  (text :email :not-null :unique))))
  (down [] (drop (table :teams))))

(defmigration add-team-users-table
  (up [] (create
           (table :team_users
                  (integer :id :auto-inc :primary-key)
                  (text :role)
                  (integer :users_id [:refer :users :id :on-delete :cascade] :not-null)
                  (integer :teams_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (boolean :admin (default false)))))
  (down [] (drop (table :team_users))))

(defmigration add-tasks-table
  (up [] (create
           (table :tasks
                  (integer :id :auto-inc :primary-key)
                  (timestamp :date :not-null)
                  (integer :users_id [:refer :users :id :on-delete :set-null])
                  (integer :teams_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (text :task))))
  (down [] (drop (table :tasks))))

(defmigration add-api-keys-table
  (up [] (create
           (table :api_keys
                  (integer :id :auto-inc :primary-key)
                  (text :api-key :unique :not-null)
                  (integer :users_id [:refer :users :id :on-delete :cascade] :not-null))))
  (down [] (drop (table :api_keys))))
