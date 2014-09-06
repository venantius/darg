(ns darg.db-util
	(:use [korma.core :as korma]
                     [darg.model :refer :all]
		  )
	)

; (def fixture-yaml "./src/resources/fixtures.yml")
; (def table-list [users teams tasks])
; (def fixture-map (parse-string (slurp fixture-yaml)))

; ; (defn reset-db
; ; 	[]
; ; "Resets db: Deletes all data, applies fixtures"
; ; 	(doseq [x table-list] (
; ; 		(delete x) 
; ; 		(insert x (values ((keyword (:name x)) (parse-string(slurp fixture-yaml))))))
; ; 	)
; ; )

(defn get-userid 
	[field value]
	(select users (fields :id) (where {(keyword field) value}))
)

(defn get-teamid
	[field value]
	(select teams (fields :id) (where {(keyword field) value}))
)

