(ns darg.model.email
  (:require [clojure.string :as str]
            [darg.db-util :as dbutil]
            [darg.model.task :as task]
            [darg.model.team :as team]
            [darg.model.user :as user]
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
    - From -> uses email address to lookup :user_id
    - Recipient -> uses email address to lookup :team_id
    - Subject -> parses out date in format 'MMM dd YYYY' and converts to sqldate for :date
    - Body -> Each newline in the body is parsed as a separate :task"
  [email]
  (let [task-list (-> email
                      :stripped-text
                      (str/split #"\n")
                      (->> (map str/trim)))
        email-metadata {:user_id (:id (user/fetch-one-user {:email (:from email)}))
                        :team_id (:id (team/fetch-one-team {:email (:recipient email)}))
                        :date (dbutil/sql-date-from-subject (:subject email))}]
    (task/create-task-list task-list email-metadata)))

(defn send-one-personal-email
  [user team]
  (let [from (:email team)
        to (:email user)]
  (mailgun/send-message {:from from
                         :to to
                         :subject "This is a test email"
                         :text "What did you do today?"})))

(defn send-personal-emails
  "Look at what teams a user is part of, and send them the daily personal
   email for each of those teams."
  [user]
  (println "sending emails")
  (let [teams (user/fetch-user-teams user)]
    (doall (map #(send-one-personal-email user %) teams)))
  #_(mailgun/send-message {}))
