(ns darg.model.users
  (:require [cemerick.url :as url]
            [clj-time.coerce :as c]
            [darg.model :as db]
            [darg.model.password-reset-tokens :as password-reset-tokens]
            [darg.services.mailgun :as mailgun]
            [korma.core :refer :all]
            [net.cgrand.enlive-html :as html])
  (:import [org.mindrot.jbcrypt BCrypt]))

(defn encrypt-password
  "Perform BCrypt hash of password"
  [password]
  (BCrypt/hashpw password (BCrypt/gensalt)))

(defn valid-password?
  "Verify that candidate password matches the hashed bcrypted password"
  [candidate hashed]
  (BCrypt/checkpw candidate hashed))

(defn create-user
  "Insert a user into the database.

  Required fields:
  :email - user's unique email (string)
  :name - user's name for display
  :active - boolean value that determines if a user is active or inactive
  :password - user's password, in plaintext.
  :admin (optional) - identifies the user as a darg.io admin"
  [params]
  (let [params (update-in params [:password] encrypt-password)]
    (insert db/users (values params))))

(defn create-user-from-signup-form
  "Create a user from the signup form"
  [account-map]
  (-> account-map
      (select-keys [:name :email :password])
      (assoc :active true)
      create-user))

(defn update-user
  "Updates the fields for a user.
  Takes a user-id as an integer and a map of fields + values to update."
  [id params]
  (update db/users (where {:id id}) (set-fields params)))

(defn- fetch-one-credentialed-user
  "Returns the user record WITH PASSWORD.

  Only for use in authentication functions."
  [params]
  (first (select db/users (where params))))

(defn fetch-user
  "Returns a safe user record from the db.

  Takes a map of fields for use in db lookup"
  [params]
  (select db/users
          (fields :id :active :bot :admin :name :email :timezone :email_hour)
          (where params)))

(defn fetch-one-user
  "Returns the first user from fetch-user"
  [params]
  (first (fetch-user params)))

(defn profile
  "Returns the profile for a given user, including the teams that they're on.

  If a list of team-ids is provided, only includes those teams. This is useful
  for limiting the visibility into which teams a user is a member of."
  ([params]
   (first
    (select db/users
            (fields :id :active :bot :admin :name :email :timezone :email_hour)
            (with db/teams)
            (where params))))
  ([params team-ids]
   (first
    (select db/users
            (fields :id :active :bot :admin :name :email :timezone :email_hour)
            (with db/teams
                  (where {:teams.id [in team-ids]}))
            (where params)))))

(defn fetch-user-id
  "Returns a user-id (integer)
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/users (fields :id) (where params)))))

(defn fetch-user-by-id
  "Returns a user record from the db.
  Takes a user-id as an integer"
  [id]
  (first (select db/users (where {:id id}))))

(defn delete-user
  "Deletes a user from the database
  Takes a user-id as an integer"
  [id]
  (delete db/users (where {:id id})))

; Github Account

(defn link-github-user
  "Associates a github account with a darg user
  Takes a users.id as the first value, and a github_users.id as the second"
  [users-id github-users-id]
  (update-user users-id {:github_users_id github-users-id}))

(defn fetch-user-github-account
  "Returns the associated github user"
  [userid]
  (let [usermap (first (select db/users
                               (where {:id userid})
                               (with db/github-users)))]
    {:user (merge (select-keys
                   usermap
                   [:id :email :name :admin :bot])
                  {:github_account (select-keys
                                    usermap
                                    [:gh_user_id :gh_login :gh_email :gh_avatar_url])})}))

; User Team Membership

(defn user-in-team?
  "Returns boolean true/false based on whether the use is a member of a given team
  Takes a user-id (integer) and team-id (integer)"
  [userid teamid]
  (if (empty? (select db/team-users (where {:users_id userid :teams_id teamid}))) false true))

(defn fetch-user-teams
  "Returns the map of teams that a user belongs to
  Takes a user-id (integer)"
  [user-id]
  (:teams (first (select db/users
                         (where {:id user-id})
                         (with db/teams)))))

(defn team-overlap
  "Returns a seq of team-maps that two users have in common
  Will return an empty seq if the users do not share any teams.
  Takes 2 user-id's (integer)"
  [userid1 userid2]
  (select db/teams
          (fields :id :name)
          (where (and {:id [in (subselect db/team-users
                                          (fields :teams_id)
                                          (where {:users_id userid1}))]}
                      {:id [in (subselect db/team-users
                                          (fields :teams_id)
                                          (where {:users_id userid2}))]}))))

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
  (select db/tasks
          (fields :id :date :users_id :teams_id :task)
          (where {:users_id (:id user)
                  :date (c/to-sql-time date)
                  :teams_id team-id})))

;; TODO: Fix this so that you can filter by team if you want
;; https://github.com/ursacorp/darg/issues/184
(defn fetch-task-dates
  "Returns a list of dates that a user posted tasks. Useful for timeline
  generation."
  ([user-id]
   (let [db-results (select db/tasks
                            (fields :date)
                            (where {:users_id user-id})
                            (order :date :desc)
                            (group :date))]
     (map :date db-results)))
  ([user-id team-id]
   (let [db-results (select db/tasks
                            (fields :date)
                            (where {:users_id user-id
                                    :teams_id team-id})
                            (order :date :desc)
                            (group :date))]
     (map :date db-results))))

(defn authenticate
  "Authenticate this user. Returns true if password is valid, else nil"
  [email password]
  (if-let [user (fetch-one-credentialed-user {:email email})]
    (valid-password? password (:password user))))

;; TODO - change the hardcoded URL here
;; https://github.com/ursacorp/darg/issues/159
(defn build-password-reset-link
  "Actually build a password reset link. Note that this is really the only time
  we should actually be creating a password reset token."
  [{:keys [id] :as user}]
  (let [base-reset-url (url/url "http://darg.herokuapp.com/new_password")
        token (:token (password-reset-tokens/create! {:users_id id}))]
    (str (assoc
          base-reset-url
          :query {:token token}))))

(html/deftemplate password-reset-template "email/templates/password_reset.html"
  [{:keys [name] :as user}]
  [:span.name] (html/content name)
  [:span.password-reset-link] (html/content (build-password-reset-link user)))

(defn build-password-reset-email
  "Composes a password reset e-mail."
  [user]
  (reduce str (password-reset-template user)))

(defn send-password-reset-email
  "This initiates the password reset workflow."
  [email]
  (let [user (fetch-one-user {:email email})
        email-content (build-password-reset-email user)]
    (mailgun/send-message {:from "support@darg.io"
                           :to email
                           :subject "Darg.io Password Reset Requested"
                           :text email-content
                           :html email-content})))
