(ns darg.controller.darg
  (:require [darg.api.responses :refer [ok unauthorized]]
            [darg.model.darg :as darg]
            [darg.model.user :as user]))

(defn get-darg
  "/api/v1/darg/:team-id

  Method: GET

  Retrieve all dargs for the current user for the target team"
  [{:keys [params user] :as request}]
  (let [team-id (-> params :team_id read-string)]
    (ok
     {:darg (darg/personal-timeline (:id user) team-id)})))

(defn get-team-darg
  "/api/v1/darg/team/:team-id

  Method: GET

  Retrieve all dargs for a given team"
  [{:keys [params user]}]
  (let [team-id (-> params :team_id read-string)]
    (cond
      (not (user/user-in-team? (:id user) team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok
       {:darg (darg/team-timeline team-id)}))))
