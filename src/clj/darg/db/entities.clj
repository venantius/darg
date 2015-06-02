(ns darg.db.entities
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.tools.logging :as log]
            [darg.util.datetime :as dt]
            [darg.util.token :as token]
            [korma.core :refer :all]))

(declare
 user
 team
 task
 role
 github-access-token)

(defentity user
  (table :darg.user :user)
  (entity-fields :id :name :email)
  (has-many task)
  (has-many role)
  (has-one github-access-token {:fk :darg_user_id})
  (many-to-many team :darg.role))

(defentity team
  (table :darg.team :team)
  (prepare (fn [{:keys [email] :as t}]
             (if email
               (assoc t :email (clojure.string/lower-case email))
               t)))
  (has-many task)
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

(defentity github-access-token
  (table :github.access_token :github_access_token)
  (belongs-to user))

(defentity team-invitation
  (table :darg.team_invitation)
  (belongs-to user)
  (belongs-to team)
  (prepare (fn [i]
             (assoc i 
                    :token (token/generate-token)
                    :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))))))

(defentity user-email-confirmation
  (table :darg.email_confirmation)
  (prepare (fn [ec]
             (if (:token ec)
               ec
               (assoc ec :token (token/generate-token)))))
  (belongs-to user))
