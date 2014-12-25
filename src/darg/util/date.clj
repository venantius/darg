(ns darg.util.date
  "Helper functions for dealing with dates"
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]))

(defn- offset-date
  [start diff]
  (t/minus start (t/days diff)))

(defn date-range
  "Generate a range of dates, starting today and going backwards.

  Accepts an optional :offset key, which can be used to offset the most
  recent date by an integer number of days."
  [size & {:keys [offset] :or {offset 0}}]
  (let [most-recent-day (t/minus (t/today) (t/days offset))]
    (map (partial offset-date most-recent-day) (range size))))

(defn sql-date-from-task
  "Convert a string into a SQL date."
  [string]
  (c/to-sql-date
    (c/from-string string)))
