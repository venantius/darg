(ns darg.model.dargs
  (:require [darg.model.users :as users]
            [darg.model.tasks :as tasks]))

;; TODO

(defn timeline
  "Build a darg timeline"
  [user-id]
  (tasks/get-tasks-by-user-id user-id))

;; TODO

(defn active-dates
  [user]
  "Figure out which dates have user activity"
  )
