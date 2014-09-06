(ns darg.db-util
	(:use [korma.core :as korma]
                     [darg.model :refer :all]
                     [clj-time.format :as f]
                     [clj-time.coerce :as c]
                     [clj-time.core :as t]
		  )
	)


(defn get-userid 
	"Takes a fieldname and value name, searches DB for the correct user-id"
	[field value]
	(select users (fields :id) (where {(keyword field) value})))

(defn get-teamid
	"Takes a fieldname and value name, searches DB for the correct team-id"
	[field value]
	(select teams (fields :id) (where {(keyword field) value})))


(defn sql-date-from-subject
	"Used to extract dates from the subject line. Assumes date format like 'Sept 23 2013' "
	[string]
	(c/to-sql-date (f/parse 
		(f/formatter "MMM dd YYY") 
		(re-find (re-pattern "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s\\d{2}\\s\\d{4}") string))))



