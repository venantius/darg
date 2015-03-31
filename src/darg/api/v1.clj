(ns darg.api.v1
  (:require [darg.api.responses :as responses]
            [darg.controller.task :as task-api]
            [darg.model.darg :as darg]))

(def post-task task-api/create!)

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
