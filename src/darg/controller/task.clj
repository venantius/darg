(ns darg.controller.task
  (:require [clojure.tools.logging :as log]
            [darg.db-util :as dbutil]
            [darg.api.responses :as responses]
            [darg.model.task :as task]
            [darg.model.user :as user]))

(defn create!
  "/api/v1/task

  Method: POST

  Create a task."
  [{:keys [params user] :as request}]
  (log/info params)
  (let [task (:task params)
        user-id (:id user)
        team-id (read-string (:team_id params))
        date (-> params :date dbutil/sql-date-from-task)]
    (cond
      (not (and task user-id team-id date))
      (responses/bad-request "Request needs to include 'task', 'team-id' and 'date'.")
      (not (user/user-in-team? user-id team-id))
      (responses/unauthorized "Not authorized.")
      :else
      (task/create-task! {:task task
                          :user_id user-id
                          :team_id team-id
                          :date date}))))
