(ns darg.util.datetime
  "Helper functions for dealing with date and times."
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(def hour-map
  {"1am" 1
   "2am" 2
   "3am" 3
   "4am" 4
   "5am" 5
   "6am" 6
   "7am" 7
   "8am" 8
   "9am" 9
   "10am" 10
   "11am" 11
   "noon" 12
   "1pm" 13
   "2pm" 14
   "3pm" 15
   "4pm" 16
   "5pm" 17
   "6pm" 18
   "7pm" 19
   "8pm" 20
   "9pm" 21
   "10pm" 22
   "11pm" 23})

(defn local-time
  [dt tz-string]
  "Get the current time for the given timezone (in long form, e.g.
  \"America/Los_Angeles\"."
  (t/to-time-zone
    (t/now)
    (t/time-zone-for-id tz-string)))

(defn nearest-hour
  "Return a constructed date-time object for the nearest hour (rounded
  down). If no datetime argument is passed in, assumes gets the nearest
  hour for the current time in UTC."
  [& [dt]]
  (let [now (or dt (t/now))
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
