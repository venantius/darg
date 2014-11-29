(ns darg.model
  (:require [korma.core :refer :all]))

(declare users teams tasks repos github-users github-tokens github-issues github-pushes github-pullrequests)

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
  (has-many teams {:FK :teams_id})
  (has-many users {:FK :users_id}))

(defentity tasks
  (belongs-to users {:FK :users_id})
  (belongs-to teams {:FK :teams_id}))

(defentity password-reset-tokens
  (table :password_reset_tokens)
  (belongs-to users {:FK :users_id}))

(defentity github-repos 
  (many-to-many teams :team_repos)
  (has-many github-issues)
  (has-many github-pushes)
  (has-many github-pullrequests))

(defentity github-users
  (has-one users)
  (belongs-to github-tokens)
  (has-many github-issues)
  (has-many github-pushes)
  (has-many github-pullrequests))

(defentity github_tokens
  (has-one github-users))

(defentity team-repos
  (table :team_repos)
  (has-many teams {:FK :teams_id}))

(defentity github-issues
  (belongs-to github-users {:FK :github_users_id})
  (belongs-to github-repos {:FK :github_repos_id}))

(defentity github-pushes
  (belongs-to github-users {:FK :github_users_id})
  (belongs-to github-repos {:FK :github_repos_id}))

(defentity github-pullrequests
  (belongs-to github-users {:FK :github_users_id})
  (belongs-to github-repos {:FK :github_repos_id}))
