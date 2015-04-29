(ns darg.util.datetime
  "Helper functions for dealing with date and times."
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [clojure.tools.logging :as log]))

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
  "Takes a datetime object, and returns a datetime object for the same time
  in the given timezone (in long form, e.g. \"America/Los_Angeles\"."
  [dt tz-string]
  (t/to-time-zone
    dt
    (t/time-zone-for-id tz-string)))

(defn as-local-date
  "Given a date, return a localized datetime object for that date.
   
   Note that these will NOT have the same POSIX timestamp; rather, this
   takes the notion of, say, '2015-04-29' in abstract and returns an object
   corresponding to '2015-04-29' only as it relates to inhabitants of Los
   Angeles (for instance)."
  [dt tz-string]
  (t/from-time-zone
    dt
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

(defn sql-time-from-subject
   "Used to extract dates from the subject line. Assumes date format like 'Sept 23 2013' "
  [string]
  (c/to-sql-time (f/parse
                 (f/formatter "MMM dd YYY")
                 (re-find (re-pattern "(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{2}\\s\\d{4}") string))))

(defn sql-time-from-task
  "Convert a string into a SQL timestamp."
  [string]
  (log/warn string)
  (c/to-sql-time
    (c/from-string string)))

(defn datetime->date-str
  [dt]
  (f/unparse
    (f/formatters :date)
    dt))
