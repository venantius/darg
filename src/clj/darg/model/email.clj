(ns darg.model.email
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [darg.email.template :as template]
            [darg.model.darg :as darg]
            [darg.model.task :as task]
            [darg.model.team :as team]
            [darg.model.user :as user]
            [darg.util.datetime :as dt]
            [darg.services.mailgun :as mailgun]))

(defn within-the-hour?
  "Is the provided datetime within an hour of the desired target hour?"
  [dt timezone target_hour]
  (let [email-hour (get dt/hour-map (clojure.string/lower-case target_hour))
        current-local-hour (t/hour (dt/local-time dt timezone))]
    (if (= email-hour current-local-hour) true false)))


(defn send-personal-email-now?
  "Is now the right time to send a user an email?"
  [dt {:keys [timezone email_hour send_daily_email] :as user}]
  (and
    send_daily_email
    (within-the-hour? dt timezone email_hour)))


(defn send-digest-email-now?
  "Is now the right time to send a user their daily digest?"
  [dt {:keys [timezone digest_hour send_digest_email] :as user}]
  (and
    send_digest_email
    (within-the-hour? dt timezone digest_hour)))


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
                        :timestamp (dt/sql-time-from-subject subject)}]
    (task/create-task-list task-list email-metadata)))


(defn daily-subject-line
  [{:keys [timezone] :as user}]
  (let [today (dt/local-time (t/now) timezone)]
    (str "Darg.io: What did you do today? [" 
         (f/unparse (f/formatter-local "MMMM dd, YYYY") today) 
         "]")))

(defn digest-subject-line
  [{:keys [timezone] :as user}]
  (let [today (dt/local-time (t/now) timezone)]
    (str "Darg.io: Daily activity report ["
         (f/unparse (f/formatter-local "MMMM dd, YYYY") today)
         "]")))

(defn from-team
  [{:keys [email name] :as team}]
  (str name " <" email ">"))


(defn send-one-personal-email
  "Send an e-mail for each team this user is on asking what they did today."
  [user team]
  (let [from (from-team team)
        to (:email user)
        subject (daily-subject-line user)]
    (log/info "Emailing" to "from" from)
    (mailgun/send-message {:from from
                           :to to
                           :subject subject
                           :html template/daily-email})))


(defn send-one-digest-email
  "Send an e-mail for each team this user is on with a digest for the last
   24 hours."
  [user dt team]
  (let [current-local-time (dt/as-local-date dt (:timezone user))
        one-day-ago (t/minus current-local-time (t/days 1))
        from (from-team team)
        to (:email user)
        subject (digest-subject-line user)
        darg (first (darg/team-timeline user (:id team) one-day-ago))
        html (template/render-digest-email darg)]
    (log/warn html)
    (log/warn (count html))
    (log/info "Sending digest email to" to "from" from "for period starting" one-day-ago "to" current-local-time)
    (spit "demo.html" html)
    (mailgun/send-message {:from from
                           :to to
                           :subject subject
                           :html html})))


(defn send-personal-emails
  "Look at what teams a user is part of, and send them the daily personal
   email for each of those teams."
  [user teams]
  (log/info "Sending daily e-mail for" user)
  (doall (map (partial send-one-personal-email user) teams)))


(defn send-digest-emails
  "Look at what teams a user is part of, and send them a daily digest email
   for each of those teams."
  [user teams dt]
  (log/info "Sending digest emails for" user)
  (doall (map (partial send-one-digest-email user dt) teams)))


(defn send-emails
  "Send any and all daily emails for this user."
  [dt user]
  (let [send-personal? (send-personal-email-now? dt user)
        send-digest? (send-digest-email-now? dt user)]
    (when (or send-personal? send-digest?)
      (let [teams (user/fetch-user-teams user)]
        (when send-personal?
          (send-personal-emails user teams))
        (when send-digest? 
          (send-digest-emails user teams dt))))))


(defn send-team-invitation
  "Send an invitation to join a team to a particular user."
  [email-address team token]
  (log/info "Sending an invitation for" email-address
            "to join team" team)
  (let [from (from-team team)
        content (template/render-team-invite team token)
        subject (str "You've been invited to join " (:name team) " on Darg.io!")]
    (mailgun/send-message {:from from
                           :to email-address
                           :subject subject
                           :html content})))


(defn from-darg
  "The 'from' email header for the main Darg site."
  []
  (str "David Jarvis (Darg.io) <david@darg.io>"))


(defn send-welcome-email
  "Send an email to a new user welcoming them to Darg and asking them to
   confirm their email address."
  [user]
  (let [from (from-darg)
        to (:email user)
        subject "Welcome to Darg.io!"
        content (template/render-welcome-email user)]
    (mailgun/send-message {:from from
                           :to to
                           :subject subject
                           :html content})))
