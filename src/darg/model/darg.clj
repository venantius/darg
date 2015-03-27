(ns darg.model.darg
  (:require [clj-time.coerce :as c]
            [darg.db.entities :as db]
            [darg.model.user :as user]
            [darg.model.team :as team]
            [darg.util :as util]
            [darg.util.datetime :as dt]
            [korma.core :refer [fields select with where]]))

(defn team-darg-by-date
  "Generate a darg for a specific team for a specific dates"
  [team-id team-user-ids date]
  (select db/user
          (fields :id :name)
          (with db/task
            (where {:team_id team-id
                    :date (c/to-sql-date date)}))
          (where {:id [in team-user-ids]})))

(defn- formatted-team-darg-by-date
  "A helper function for formatting team dargs."
  [team-id team-user-ids date]
  {:date (util/sql-datetime->date-str date)
   :user (vec (team-darg-by-date team-id team-user-ids date))})

(defn team-timeline
  "Build a darg timeline for a given team."
  [team-id]
  (let [team-user-ids (map :id (team/fetch-team-users team-id))
        dates (map c/to-sql-date (dt/date-range 5))
        date (first dates)]
    (map (partial
           formatted-team-darg-by-date
           team-id team-user-ids) dates)))

(defn personal-timeline
  "Build a darg timeline for a single person on a single team.
  This is a naive implementation for now that can be
  expanded to include other darg subtypes as we go."
  [user-id team-id]
  (let [user (user/fetch-user-by-id user-id)
        dates (map c/to-sql-date (dt/date-range 5))
        tasks-by-date (map (partial user/fetch-tasks-by-team-and-date user team-id) dates)]
    (reduce conj []
      (map (fn [k t] {:date (util/sql-datetime->date-str k)
                      :tasks t})
           dates tasks-by-date))))
