(ns darg.util.datetime
  "Helper functions for dealing with date and times."
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]))

(defn current-local-time
  [tz-string]
  "Get the current time for the given timezone (in long form, e.g.
  \"America/Los_Angeles\"."
  (t/to-time-zone
    (t/now)
    (t/time-zone-for-id tz-string)))

(defn nearest-hour
  "Return a constructed date-time object for the nearest UTC hour (rounded
  down)."
  []
  (let [now (t/now)
        year (t/year now)
        month (t/month now)
        day (t/day now)
        hour (t/hour now)]
    (t/date-time year month day hour)))

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
