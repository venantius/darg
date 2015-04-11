(ns darg.model.email
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [darg.db-util :as dbutil]
            [darg.model.email.template :as template]
            [darg.model.task :as task]
            [darg.model.team :as team]
            [darg.model.user :as user]
            [darg.util.datetime :as dt]
            [darg.services.mailgun :as mailgun]))

(defn user-can-email-this-team?
  "Is the user who owns this e-mail authorized to post to this e-mail address?

  In other words, is this user a member of the team that owns this e-mail address?"
  [user-email team-email]
  (let [user-id (:id (user/fetch-one-user {:email user-email}))
        team-id (:id (team/fetch-one-team {:email team-email}))]
    (user/user-in-team? user-id team-id)))

(defn parse-email
  "Recieves a darg email from a user, parses tasklist, and inserts the tasks into
  the database.

  Email mapping to task metadata is:
    - sender -> uses email address to lookup :user_id
    - recipient -> uses email address to lookup :team_id
    - subject -> parses out date in format 'MMM dd YYYY' and converts to sqldate for :date
    - stripped-text -> Each newline in the body is parsed as a separate :task"
  [{:keys [sender recipient subject stripped-text] :as email}]
  (let [task-list (-> stripped-text
                      (str/split #"\n")
                      (->> (map str/trim)))
        email-metadata {:user_id (:id (user/fetch-one-user {:email sender}))
                        :team_id (:id (team/fetch-one-team {:email recipient}))
                        :date (dbutil/sql-date-from-subject subject)}]
    (task/create-task-list task-list email-metadata)))

(defn todays-subject-line
  [{:keys [timezone] :as user}]
  (let [today (dt/local-time (t/now) timezone)]
    (str "Darg.io: What did you do today? [" (f/unparse (f/formatter "MMMM dd YYYY") today) "]")))

(defn from
  [{:keys [email name] :as team}]
  (str name " (Darg.io) <" email ">"))

(defn send-one-personal-email
  "Send an e-mail for each team this user is on asking what they did today."
  [user team]
  (let [from (from team)
        to (:email user)
        subject (todays-subject-line user)]
    (log/info "Emailing" to "from" from)
    (mailgun/send-message {:from from
                           :to to
                           :subject subject
                           :html template/daily-email})))

(defn send-personal-emails
  "Look at what teams a user is part of, and send them the daily personal
   email for each of those teams."
  [user]
  (log/info "Sending daily e-mail for" user)
  (let [teams (user/fetch-user-teams user)]
    (doall (map #(send-one-personal-email user %) teams))))

(defn send-team-invitation
  "Send an invitation to join a team to a particular user."
  [email-address team]
  (log/info "Sending an invitation for" email-address
            "to join team" team)
  (let [from (from team)
        content (template/render-team-invite team)
        subject (str "You've been invited to join " (:name team) " on Darg.io!")]
    (mailgun/send-message {:from from
                           :to email-address
                           :subject subject
                           :html content})))
