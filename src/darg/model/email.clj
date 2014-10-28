(ns darg.model.email
  (:require [clojure.string :as str]
            [darg.db-util :as dbutil]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]))

(defn user-can-email-this-team?
  "Is the user who owns this e-mail authorized to post to this e-mail address?

  In other words, is this user a member of the team that owns this e-mail address?"
  [user-email team-email]
  (let [user-id (users/get-user-id {:email user-email})
        team-id (teams/get-team-id {:email team-email})]
    (users/user-in-team? user-id team-id)))

(defn parse-email
  "Recieves a darg email from a user, parses tasklist, and inserts the tasks into
  the database.

  Email mapping to task metadata is:
    - From -> uses email address to lookup :users_id
    - Recipient -> uses email address to lookup :teams_id
    - Subject -> parses out date in format 'MMM dd YYYY' and converts to sqldate for :date
    - Body -> Each newline in the body is parsed as a separate :task"
  [email]
  (let [task-list (-> email
                      :stripped-text
                      (str/split #"\n")
                      (->> (map str/trim)))
        email-metadata {:users_id (users/fetch-user-id {:email (:from email)})
                        :teams_id (teams/fetch-team-id {:email (:recipient email)})
                        :date (dbutil/sql-date-from-subject (:subject email))}]
    (tasks/create-task-list task-list email-metadata)))

