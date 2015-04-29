(ns darg.controller.darg
  (:require [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [darg.api.responses :refer [ok unauthorized]]
            [darg.model.darg :as darg]
            [darg.model.user :as user]))

(defn get-team-darg-by-date
  "/api/v1/darg/team/:team_id/:date
   
  Method: GET
   
  Retrieve a timeline for a particular date"
  [{:keys [params user]}]
  (let [team-id (-> params :team_id read-string)
        user (user/fetch-one-user {:id (:id user)})
        date (-> params :date f/parse)]
    (cond
      (not (user/user-in-team? (:id user) team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok
        (darg/team-timeline user team-id date)))))
