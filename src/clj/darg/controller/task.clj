(ns darg.controller.task
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :as responses]
            [darg.model.task :as task]
            [darg.model.user :as user]
            [darg.util.datetime :as dt]))

(defn create!
  "/api/v1/task

  Method: POST

  Create a task."
  [{:keys [params user] :as request}]
  (log/info "Creating task:" params)
  (let [task (:task params)
        user-id (:id user)
        team-id (read-string (:team_id params))
        timestamp (-> params :timestamp dt/sql-time-from-task)]
    (cond
      (not (and task user-id team-id timestamp))
      (responses/bad-request "Request needs to include 'task', 'team-id' and 'timestamp'.")
      (not (user/user-in-team? user-id team-id))
      (responses/unauthorized "Not authorized.")
      :else
      (responses/ok (task/create-task! {:task task
                                        :user_id user-id
                                        :team_id team-id
                                        :timestamp timestamp})))))

(defn update!
  "/api/v1/task/:id
   
   Method: POST
   
   Update an existing task."
  [{:keys [params user] :as request}]
  (log/info "Updating task:" request)
  (let [new-task (:task params)
        user-id (:id user)
        id (-> params :id read-string)
        old-task (task/fetch-one-task {:id id})]
    (cond
      (not= (:user_id old-task) user-id)
      (responses/unauthorized "Not authorized.")
      :else
      (responses/ok (task/update-task! id {:task new-task
                                           :id id})))))
