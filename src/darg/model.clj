(ns darg.model
  (:require [korma.core :refer :all]))

(declare
 users
 teams
 tasks
 repos
 github-users
 github-tokens
 github-issues
 github-pushes
 github-pull-requests)

(defentity users
  (has-many tasks)
  (belongs-to github-users)
  (many-to-many teams :team_users))

(defentity teams
  (has-many tasks)
  (has-many repos :team_repos)
  (many-to-many users :team_users))

(defentity team-users
  (table :team_users)
  (has-many teams {:fk :teams_id})
  (has-many users {:fk :users_id}))

(defentity tasks
  (belongs-to users {:fk :users_id})
  (belongs-to teams {:fk :teams_id}))

(defentity password-reset-tokens
  (table :password_reset_tokens)
  (belongs-to users {:fk :users_id}))

(defentity github-repos
  (table :github_repos)
  (many-to-many teams :team_repos)
  (has-many github-issues)
  (has-many github-pushes)
  (has-many github-pull-requests))

(defentity github-users
  (table :github_users)
  (has-one users)
  (belongs-to github-tokens)
  (has-many github-issues)
  (has-many github-pushes)
  (has-many github-pull-requests))

(defentity github-tokens
  (table :github_tokens)
  (has-one github-users))

(defentity team-repos
  (table :team_repos)
  (has-many teams {:fk :teams_id}))

(defentity github-issues
  (table :github_issues)
  (belongs-to github-users)
  (belongs-to github-repos {:fk :github_repos_id}))

(defentity github-pushes
  (table :github_pushes)
  (belongs-to github-users)
  (belongs-to github-repos {:fk :github_repos_id}))

(defentity github-pull-requests
  (table :github_pull_requests)
  (belongs-to github-users)
  (belongs-to github-repos {:fk :github_repos_id}))
