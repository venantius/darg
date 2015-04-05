(ns darg.db-util
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]))

(defn sql-date-from-subject
  "Used to extract dates from the subject line. Assumes date format like 'Sept 23 2013' "
  [string]
  (c/to-sql-date (f/parse
                 (f/formatter "MMM dd YYY")
                 (re-find (re-pattern "(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{2}\\s\\d{4}") string))))

(defn sql-date-from-task
  "Convert a string into a SQL date."
  [string]
  (c/to-sql-date
    (c/from-string string)))
