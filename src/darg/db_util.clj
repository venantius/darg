(ns darg.db-util
	(:use [korma.core :as korma]
                     [darg.model :refer :all]
		  )
	)


(defn get-userid 
	[field value]
	(select users (fields :id) (where {(keyword field) value}))
)

(defn get-teamid
	[field value]
	(select teams (fields :id) (where {(keyword field) value}))
)

