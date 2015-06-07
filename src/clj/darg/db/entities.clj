(ns darg.db.entities
  "Namespace detailing relationships between database entities (tables).
   
   Note that in some cases relationships are set as 'has-many' even though
   the actual data relationship is 'has-one'; this has been done to take
   advantage of Korma's nesting of the associated table rather than
   the flattened results set."
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
 github-access-token
 github-oauth-state
 github-team-settings
 github-repo
 github-team-repo
 github-user)

(defentity user
  (table :darg.user :user)
  (entity-fields :id :name :email)
  (has-many task)
  (has-many role)
  (has-one github-user {:fk :darg_user_id})
  (has-one github-access-token {:fk :darg_user_id})
  (has-many github-oauth-state {:fk :darg_user_id})
  (many-to-many team :darg.role))

(defentity team
  (table :darg.team :team)
  (prepare (fn [{:keys [email] :as t}]
             (if email
               (assoc t :email (clojure.string/lower-case email))
               t)))
  (has-many task)
  (has-many role)
  (has-many github-team-settings {:fk :darg_team_id})
  (has-many github-oauth-state {:fk :darg_team_id})
  (has-many github-team-repo {:fk :darg_team_id})
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
  (belongs-to user)
  (belongs-to github-user)
  (has-many github-oauth-state {:fk :access_token_id}))

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

(defentity github-oauth-state
  (table :github.oauth_state :github_oauth_state)
  (belongs-to user)
  (belongs-to team))

(defentity github-team-settings
  (table :github.team_settings :github_team_settings)
  (belongs-to team)
  (belongs-to github-oauth-state))

(defentity github-repo
  (table :github.repo :github_repo)
  (has-many github-team-repo))

(defentity github-team-repo
  (table :github.team_repo :github_team_repo)
  (belongs-to github-repo)
  (belongs-to team))

(defentity github-user
  (table :github.user :github_user)
  (belongs-to user)
  (has-one github-access-token))
