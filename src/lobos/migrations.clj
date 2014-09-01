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
                  (integer :id :primary-key)
                  (text :email :unique :not-null)
                  (text :username :unique :not-null)
                  (text :address)
                  (boolean :admin (default false)))))
  (down [] (drop (table :users))))

(defmigration add-teams-table
  (up [] (create
           (table :teams
                  (integer :id :primary-key)
                  (text :name))))
  (down [] (drop (table :teams))))

(defmigration add-team-users-table
  (up [] (create
           (table :team_users
                  (integer :id :primary-key)
                  (integer :user-id [:refer :users :id] :not-null)
                  (integer :team-id [:refer :teams :id] :not-null)
                  (boolean :admin (default false)))))
  (down [] (drop (table :team-admins))))

(defmigration add-tasks-table
  (up [] (create
           (table :tasks
                  (integer :id :primary-key)
                  (date :date :not-null)
                  (integer :user-id [:refer :users :id] :not-null)
                  (integer :team-id [:refer :teams :id] :not-null)
                  (text :title)
                  (text :task))))
  (down [] (drop (table :tasks))))
