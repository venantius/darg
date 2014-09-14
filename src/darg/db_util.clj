(ns darg.db-util
  (:use [korma.core :as korma]
        [darg.model])
  (:require [clj-time.format :as f]
            [clj-time.coerce :as c]))

(defn sql-date-from-subject
  "Used to extract dates from the subject line. Assumes date format like 'Sept 23 2013' "
  [string]
  (c/to-sql-date (f/parse 
                 (f/formatter "MMM dd YYY") 
                 (re-find (re-pattern "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s\\d{2}\\s\\d{4}") string))))
