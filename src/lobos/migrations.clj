(ns lobos.migrations
  (:require [darg.db])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema
               config)))

;; NOTE: Found this a handy reference on Lobos
;; http://vijaykiran.com/2012/01/web-application-development-with-clojure-part-2/

(defmigration add-users-table
  (up [] (create
           (table :users
                  (integer :id :auto-inc :primary-key)
                  (text :email :unique :not-null)
                  (text :first_name)
                  (text :last_name)
                  (boolean :admin (default false)))))
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
                  (integer :user_id [:refer :users :id :on-delete :cascade] :not-null)
                  (integer :team_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (boolean :admin (default false)))))
  (down [] (drop (table :team_users))))

(defmigration add-tasks-table
  (up [] (create
           (table :tasks
                  (integer :id :auto-inc :primary-key)
                  (date :date :not-null)
                  (integer :user_id [:refer :users :id :on-delete :no-action] :not-null)
                  (integer :team_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (text :task))))
  (down [] (drop (table :tasks))))
