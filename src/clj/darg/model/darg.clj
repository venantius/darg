(ns darg.model.darg
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.tools.logging :as log]
            [darg.db.entities :as db]
            [darg.model.user :as user]
            [darg.model.team :as team]
            [darg.util :as util]
            [darg.util.datetime :as dt]
            [korma.core :refer [fields select with where]]))

(defn team-darg-by-date
  "Generate a darg for a specific team for a specific dates"
  [team-id role-ids date]
  (let [from (c/to-sql-time date)
        to (c/to-sql-time (t/plus date (t/days 1)))]
    (select db/user
            (fields [:email])
            (with db/task
                  (where {:team_id team-id
                          :timestamp [between [from to]]}))
            (where {:id [in role-ids]}))))

(defn- formatted-team-darg-by-date
  "A helper function for formatting team dargs."
  [team-id role-ids dt]
  {:date (dt/datetime->date-str dt)
   :user (vec (team-darg-by-date team-id role-ids dt))})

;; take a date string
;; construct a datetime object with that date in that timezone

(defn team-timeline
  "Build a darg timeline for a given team. Also needs to know which user
   is requesting the timeline, so that we can report on events in local
   time."
  [user team-id date]
  (let [role-ids (map :id (team/fetch-roles team-id))
        local-date (dt/as-local-date date (:timezone user))]
    (list (formatted-team-darg-by-date
            team-id role-ids local-date))))

(defn email-timeline
  [team-id date]
  (log/info date)
  (let [role-ids (map :id (team/fetch-roles team-id))
        date (c/to-sql-date date)]
    (log/info date)
    (list (formatted-team-darg-by-date
      team-id role-ids date))))
