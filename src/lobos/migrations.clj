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

(defmigration add-github-tokens-table
  (up [] (create
           (table :github_tokens
                  (integer :id :auto-inc :primary-key)
                  (text :gh_token :not-null)
                  (boolean :repo_scope (default false)))))
  (down [] (drop (table :github_tokens))))

(defmigration add-github-users-table
  (up [] (create
           (table :github_users
                  (integer :id :primary-key)
                  (text :gh_login)
                  (text :gh_email)
                  (text :gh_avatar_url)
                  (integer :github_tokens_id [:refer :github_tokens :id :on-delete :set-null]))))
  (down [] (drop (table :github_users))))

(defmigration add-users-table
  (up [] (create
           (table :users
                  (integer :id :auto-inc :primary-key)
                  (text :email :unique :not-null)
                  (text :password :not-null)
                  (text :name :not-null)
                  (boolean :admin (default false))
                  (boolean :bot (default true))
                  (integer :github_users_id [:refer :github_users :id :on-delete :set-null])
                  (boolean :active (default false)))))
  (down [] (drop (table :users))))

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
                  (integer :id :primary-key)
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
                  (integer :teams_id [:refer :teams :id :on-delete :cascade])
                  (boolean :active (default "false")))))
  (down [] (drop (table :team_repos))))

(defmigration add-github-pushes-table
  (up [] (create
           (table :github_pushes
                  (integer :id :auto-inc :primary-key)
                  (integer :github_users_id [:refer :github_users :id :on-delete :set-null])
                  (integer :github_repos_id [:refer :github_repos :id :on-delete :cascade] :not-null)
                  (integer :size :not-null) ;number of commits in push
                  (text :ref :not-null) ;full git ref that was pushed (repo + branch)
                  (text :head-commit-message :not-null) ;message on the top commit
                  (text :compare-url :not-null)
                  (timestamp :timestamp :not-null))))
  (down [] (drop (table :github_pushes))))

(defmigration add-github-issues-table
  (up [] (create
           (table :github_issues
                  (integer :id :auto-inc :primary-key)
                  (integer :github_users_id [:refer :github_users :id :on-delete :set-null])
                  (integer :github_repos_id [:refer :github_repos :id :on-delete :cascade] :not-null)
                  (text :action :not-null)
                  (integer :number :not-null)
                  (text :title :not-null)
                  (text :url :not-null)
                  (timestamp :timestamp :not-null))))
  (down [] (drop (table :github_issues))))

(defmigration add-github-pull_requests-table
  (up [] (create 
           (table :github_pull_requests
                  (integer :id :auto-inc :primary-key)
                  (integer :github_users_id [:refer :github_users :id :on-delete :set-null])
                  (integer :github_repos_id [:refer :github_repos :id :on-delete :cascade] :not-null)
                  (text :action :not-null)
                  (integer :number :not-null)
                  (text :title :not-null)
                  (text :url :not-null)
                  (timestamp :timestamp :not-null))))
  (down [] (drop (table :github_pull_requests))))

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
