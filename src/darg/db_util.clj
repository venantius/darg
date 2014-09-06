(ns darg.db-util
	(:use [korma.core :as korma]
                     [darg.model :refer :all]
                     [clj-time.format :as f]
                     [clj-time.coerce :as c]
                     [clj-time.core :as t]
		  )
	)


(defn get-userid 
	[field value]
	(select users (fields :id) (where {(keyword field) value})))

(defn get-teamid
	[field value]
	(select teams (fields :id) (where {(keyword field) value})))

; (defn date-extract
; 	[string]
; 	(re-find (re-pattern "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s\\d{2}\\s\\d{4}") string))

(defn sql-date-from-subject
	[string]
	(c/to-sql-date (f/parse 
		(f/formatter "MMM dd YYY") 
		(re-find (re-pattern "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s\\d{2}\\s\\d{4}") string))))



; (defn add-teammembers
; 	[teamid [userids]]
; 	(doseq [id (userids)]
; 		(insert team_users (values {:team-id teamid :user-id id})))
; 	)
