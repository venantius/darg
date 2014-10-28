(ns darg.model
  (:require [korma.core :refer :all]))

(declare users teams tasks)

(defentity users
  (has-many tasks)
  (many-to-many teams :team_users))

(defentity teams
  (has-many tasks)
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
