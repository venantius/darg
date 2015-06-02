(ns darg.model.user
  (:require [cemerick.url :as url]
            [clj-time.coerce :as c]
            [crypto.random :as random]
            [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.model.password-reset-token :as password-reset-token]
            [darg.services.mailgun :as mailgun]
            [environ.core :as env]
            [korma.core :refer :all]
            [net.cgrand.enlive-html :as html]
            [schema.core :as s])
  (:import [org.mindrot.jbcrypt BCrypt]))

(defmodel db/user
  {(s/optional-key :id) s/Int
   (s/optional-key :email) s/Str
   (s/optional-key :password) s/Str
   (s/optional-key :name) s/Str
   (s/optional-key :timezone) s/Str
   (s/optional-key :email_hour) s/Str
   (s/optional-key :admin) s/Bool
   (s/optional-key :bot) s/Bool
   (s/optional-key :active) s/Bool
   (s/optional-key :confirmed_email) s/Bool
   (s/optional-key :digest_hour) s/Str
   (s/optional-key :created_at) s/Any
   (s/optional-key :send_daily_email) s/Bool
   (s/optional-key :send_digest_email) s/Bool})

(defn encrypt-password
  "Perform BCrypt hash of password"
  [password]
  (BCrypt/hashpw password (BCrypt/gensalt)))

(defn valid-password?
  "Verify that candidate password matches the hashed bcrypted password"
  [candidate hashed]
  (BCrypt/checkpw candidate hashed))

(defn create-user!
  "Insert a user into the database.

  Required fields:
  :email - user's unique email (string)
  :name - user's name for display
  :active - boolean value that determines if a user is active or inactive
  :password - user's password, in plaintext.
  :admin (optional) - identifies the user as a darg.io admin"
  [params]
  (let [params (-> params
                   (update-in [:password] encrypt-password)
                   (update-in [:email] clojure.string/lower-case))]
    (insert db/user (values params))))

(defn create-user-from-signup-form
  "Create a user from the signup form"
  [account-map]
  (-> account-map
      (select-keys [:name :email :password])
      (assoc :active true)
      create-user!))

(defn- fetch-one-credentialed-user
  "Returns the user record WITH PASSWORD.

  Only for use in authentication functions."
  [params]
  (first (select db/user 
                 (fields :id :password)
                 (where params))))

(defn fetch-user
  "Returns a safe user record from the db.

  Takes a map of fields for use in db lookup"
  [params]
  (select db/user
          (fields :timezone :email_hour :admin :bot :active
                  :confirmed_email :digest_hour :created_at
                  :send_daily_email :send_digest_email)
          (where params)))

(defn fetch-one-user
  "Returns the first user from fetch-user"
  [params]
  (first (fetch-user params)))

(defn update-user!
  "Updates the fields for a user.
  Takes a user-id as an integer and a map of fields + values to update."
  [id params]
  (let [params (if (:email params)
                 (update-in params [:email] clojure.string/lower-case)
                 params)]
    (update db/user (where {:id id}) (set-fields params))
    (fetch-one-user {:id id})))

(defn delete-user!
  [params]
  (delete db/user (where params)))

(defn profile
  "Returns the profile for a given user, including the teams that they're on.

  If a list of team-ids is provided, only includes those teams. This is useful
  for limiting the visibility into which teams a user is a member of."
  ([params]
   (first
    (select db/user
            (fields :timezone :email_hour :admin :bot :active
                    :confirmed_email :digest_hour :created_at
                    :send_daily_email :send_digest_email)
            (with db/team
                  (order :team.name :asc))
            (where params))))
  ([params team-ids]
   (first
    (select db/user
            (fields 
             :confirmed_email
             :created_at
             :active
             :bot
             :admin
             :timezone
             :email_hour
             :digest_hour
             :send_daily_email
             :send_digest_email)
            (with db/team
                  (where {:team.id [in team-ids]})
                  (order :team.name :asc))
            (where params)))))

(defn fetch-one-with-github-access-token
  "Retrieve this user with their GitHub access token, if any."
  [user]
  (first (select db/user
                 (where user)
                 (with db/github-access-token
                   (fields [:token :github_access_token])))))

; User Team Membership

(defn user-in-team?
  "Returns boolean true/false based on whether the use is a member of a given team
  Takes a user-id (integer) and team-id (integer)"
  [userid teamid]
  (if (empty? (select db/role (where {:user_id userid :team_id teamid}))) false true))

(defn fetch-user-teams
  "Returns the map of teams that a user belongs to."
  [user]
  (:team (first (select db/user
                        (where user)
                        (with db/team)))))

(defn team-overlap
  "Returns a seq of team-maps that two users have in common
  Will return an empty seq if the users do not share any teams.
  Takes 2 user-id's (integer)"
  [userid1 userid2]
  (select db/team
          (fields :id :name)
          (where (and {:id [in (subselect db/role
                                          (fields :team_id)
                                          (where {:user_id userid1}))]}
                      {:id [in (subselect db/role
                                          (fields :team_id)
                                          (where {:user_id userid2}))]}))))

(defn users-on-same-team?
  "Returns boolean true/false based on whether user's are on the same team
  Takes 2 user-ids (integers)"
  [userid1 userid2]
  (if (= userid1 userid2)
    true
    (let [teamlist (team-overlap userid1 userid2)]
      (if (empty? teamlist)
        false
        true))))

;; tasks

(defn fetch-tasks-by-team-and-date
  "Find tasks for this user by date and team"
  [user team-id date]
  (select db/task
          (where {:user_id (:id user)
                  :timestamp (c/to-sql-time date)
                  :team_id team-id})))

(defn authenticate
  "Authenticate this user. Returns true if password is valid, else nil"
  [email password]
  (when-let [user (fetch-one-credentialed-user {:email email})]
    (valid-password? password (:password user))))

(defn build-password-reset-link
  "Build a password reset link. Note that this is really the only time
  we should actually be creating a password reset token."
  [{:keys [id] :as user}]
  (let [root-url (if (= (env/env :darg-environment) "dev")
                   "localhost"
                   "darg.io")
        base-reset-url (url/url (str "http://"
                                     root-url
                                     "/new_password"))
        token (:token (password-reset-token/create! {:user_id id}))]
    (str (assoc
          base-reset-url
          :query {:token token}))))

(html/deftemplate password-reset-template "email/templates/raw/password_reset.html"
  [{:keys [name] :as user}]
  [:span.name] (html/content name)
  [:span.password-reset-link] (html/content (build-password-reset-link user)))

(defn build-password-reset-email
  "Composes a password reset e-mail."
  [user]
  (reduce str (password-reset-template user)))

(defn send-password-reset-email
  "This initiates the password reset workflow."
  [user]
  (let [email-content (build-password-reset-email user)]
    (mailgun/send-message {:from "support@darg.io"
                           :to (:email user)
                           :subject "Darg.io Password Reset Requested"
                           :text email-content
                           :html email-content})))
