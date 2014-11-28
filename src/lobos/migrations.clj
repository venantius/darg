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
                  (text :password :not-null)
                  (text :name :not-null)
                  (boolean :admin (default false))
                  (boolean :bot (default true))
                  (boolean :active (default false)))))
  (down [] (drop (table :users))))

(defmigration add-github-token-table
  (up [] (create
            (table :github_token
              (integer :id :auto-inc :primary-key)
              (integer :users_id [:refer :users :id :on-delete :cascade] :not-null)
              (text :gh_access_token :not-null))))
  (down [] (drop (table :github_token))))

(defmigration add-teams-table
  (up [] (create
           (table :teams
                  (integer :id :auto-inc :primary-key)
                  (text :name :not-null)
                  (text :email :not-null :unique))))
  (down [] (drop (table :teams))))

(defmigration add-tasks-table
  (up [] (create
           (table :tasks
                  (integer :id :auto-inc :primary-key)
                  (timestamp :date :not-null)
                  (integer :users_id [:refer :users :id :on-delete :set-null])
                  (integer :teams_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (text :task))))
  (down [] (drop (table :tasks))))

(defmigration add-github-repos-table
  (up [] (create 
             (table :github_repos
                (integer :id :auto-inc :primary-key)
                (text :name :not-null)
                (text :description)
                (text :html-url :not-null))))
  (down [] (drop (table :github_repos))))

(defmigration add-team-users-table
  (up [] (create
           (table :team_users
                  (integer :id :auto-inc :primary-key)
                  (text :role)
                  (integer :users_id [:refer :users :id :on-delete :cascade] :not-null)
                  (integer :teams_id [:refer :teams :id :on-delete :cascade] :not-null)
                  (boolean :admin (default false)))))
  (down [] (drop (table :team_users))))

(defmigration add-team-repos-table
  (up [] (create
             (table :team_repos
                (integer :id :auto-inc :primary-key)
                (integer :github_repos_id [:refer :github_repos :id :on-delete :cascade] :not-null)
                (integer :teams_id [:refer :teams :id :on-delete :cascade]))))
  (down [] (drop (table :team_repos))))

(defmigration add-api-keys-table
  (up [] (create
           (table :api_keys
                  (integer :id :auto-inc :primary-key)
                  (text :api-key :unique :not-null)
                  (integer :users_id [:refer :users :id :on-delete :cascade] :not-null))))
  (down [] (drop (table :api_keys))))

(defmigration add-password-reset-tokens-table
  (up [] (create
           (table :password_reset_tokens
                  (integer :id :auto-inc :primary-key)
                  (text :token :unique :not-null)
                  (integer :users_id [:refer :users :id :on-delete :cascade] :not-null)
                  (timestamp :expires_at :not-null (default (now))))))
  (down [] (drop (table :password_reset_tokens))))
