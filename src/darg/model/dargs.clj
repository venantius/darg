(ns darg.model.dargs
  (:require [darg.model.users :as users]
            [darg.model.tasks :as tasks]
            [darg.util :as util]
            [clojure.tools.logging :as logging]
            ))

(defn active-dates
  "Figure out which dates have user activity"
  ([user-id]
   (users/fetch-task-dates user-id))
  ([user-id team-id]
   (users/fetch-task-dates user-id team-id)))

(defn timeline
  "Build a darg timeline. This is a naive implementation for now that can be
  expanded to include other darg subtypes as we go."
  [user-id team-id]
  (let [user (users/fetch-user-by-id user-id)
        dates (active-dates user-id team-id)
        tasks-by-date (map (partial users/fetch-tasks-by-team-and-date user team-id) dates)]
    (reduce conj []
      (map (fn [k t] {:date (util/sql-datetime->date-str k)
                      :tasks t})
           dates tasks-by-date))))


