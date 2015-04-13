(ns darg.db.entities
  (:require [clojure.tools.logging :as log]
            [korma.core :refer :all]))

(declare
 user
 team
 task
 role
 repo
 github-user
 github-token
 github-issue
 github-push
 github-pull-request)

(defentity user
  (table :darg.user :user)
  (entity-fields :id :name)
  (has-many task)
  (has-many role)
  (belongs-to github-user)
  (many-to-many team :darg.role))

(defentity team
  (table :darg.team :team)
  (prepare (fn [{:keys [email] :as t}]
             (if email
               (assoc t :email (clojure.string/lower-case email))
               t)))
  (has-many task)
  (has-many repo :team_repo)
  (has-many role)
  (many-to-many user :darg.role))

(defentity role
  (table :darg.role :role)
  (belongs-to team)
  (belongs-to user))

(defentity task
  (table :darg.task :task)
  (belongs-to user {:fk :user_id})
  (belongs-to team {:fk :team_id}))

(defentity password-reset-token
  (table :darg.password_reset_token :password_reset_token)
  (belongs-to user {:fk :user_id}))

(defentity github-repo
  (table :github.repo :team_repo)
  (many-to-many team :darg.team_repo)
  (has-many github-issue)
  (has-many github-push)
  (has-many github-pull-request))

(defentity github-user
  (table :github.user :github_user)
  (has-one user)
  (belongs-to github-token)
  (has-many github-issue)
  (has-many github-push)
  (has-many github-pull-request))

(defentity github-token
  (table :github.token :github_token)
  (has-one github-user))

(defentity team-repo
  (table :darg.team_repo :team_repo)
  (has-many team {:fk :team_id}))

(defentity github-issue
  (table :github.issue :github_issue)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :gh_repo_id}))

(defentity github-push
  (table :github.push :github_push)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :github_repo_id}))

(defentity github-pull-request
  (table :github.pull_request :github_pull_request)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :github_repo_id}))
