(ns darg.model.dargs
  (:require [darg.model.users :as users]
            [darg.model.tasks :as tasks]
            [darg.util :as util]))

(defn active-dates
  "Figure out which dates have user activity"
  [user]
  (users/fetch-task-dates (:id user)))

(defn timeline
  "Build a darg timeline. This is a naive implementation for now that can be
  expanded to include other darg subtypes as we go."
  [user-id]
  (let [user (users/fetch-user-by-id user-id)
        dates (active-dates user)
        tasks-by-date (map (partial users/fetch-tasks-by-date user) dates)]
    (reduce conj []
      (map (fn [k t] {:date (util/sql-datetime->date-str k)
                      :tasks t})
           dates tasks-by-date))))


