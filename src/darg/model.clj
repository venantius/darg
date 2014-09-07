(ns darg.model
  (:use korma.core))

(declare users teams tasks)

(defentity users
  (has-many tasks)
  (many-to-many teams :team_users))

(defentity teams
  (has-many tasks)
  (many-to-many users :team_users))

(defentity team-users
  (table :team_users)
  (has-many teams {:FK :team_id})
  (has-many users {:FK :user_id}))

(defentity tasks
  (belongs-to users {:FK :user_id})
  (belongs-to teams {:FK :team_id}))
