(ns darg.model
  (:require [korma.core :refer :all]))

(declare users teams tasks repos github-users gh-issue gh-push gh-pullrequest)

(defentity users
  (has-many tasks)
  (has-one github-users)
  (has-many gh-issue)
  (has-many gh-push)
  (has-many gh-pullrequest)
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
  (has-many gh-issue)
  (has-many gh-push)
  (has-many gh-pullrequest))

(defentity github-users
  (belongs-to users))

(defentity team-repos
  (table :team_repos)
  (has-many teams {:FK :teams_id}))

(defentity gh-issue
  (belongs-to users {:FK :users_id})
  (belongs-to github-repos {:FK :github_repos_id}))

(defentity gh-push
  (belongs-to users {:FK :users_id})
  (belongs-to github-repos {:FK :github_repos_id}))

(defentity gh-pullrequest
  (belongs-to users {:FK :users_id})
  (belongs-to github-repos {:FK :github_repos_id}))