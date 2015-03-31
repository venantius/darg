(ns darg.api.v1
  (:require [darg.api.responses :as responses]
            [darg.controller.auth :as auth-api]
            [darg.controller.email :as email-api]
            [darg.controller.gravatar :as gravatar-api]
            [darg.controller.task :as task-api]
            [darg.controller.user :as user-api]
            [darg.model.darg :as darg]))

(def update-user user-api/update!)


(def gravatar gravatar-api/gravatar)

(def post-task task-api/create!)

;; dargs

(defn get-darg
  "/api/v1/darg/:team-id

  Method: GET

  Retrieve all dargs for the current user for the target team"
  [{:keys [params user] :as request}]
  (let [team-id (-> params :team_id read-string)]
    (responses/ok
     {:darg (darg/personal-timeline (:id user) team-id)})))

(defn get-team-darg
  "/api/v1/darg/team/:team-id

  Method: GET

  Retrieve all dargs for a given team"
  [{:keys [params user]}]
  (let [team-id (-> params :team_id read-string)]
    (responses/ok
     {:darg (darg/team-timeline team-id)})))

(def email email-api/email)


(def get-user user-api/get)
