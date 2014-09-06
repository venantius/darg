(ns darg.fixtures.db
	(:use [korma.core :as korma]
		  [darg.model :refer :all]
		  )
	(:require [clj-yaml.core :as yaml]
		   [darg.fixtures.model :as fmodel])
	)

; (def fixture-yaml "./src/darg/resources/fixtures.yml")
; (def table-list [users teams tasks])
; ; (def fixture-map (yaml/parse-string (slurp fixture-yaml)))

; (defn insert-db-fixture-data 
; 	[]
; 	(for [x table-list] 
; 		(insert x (values ((keyword (:name x)) fixture-map)))))

(defn insert-user-fixture-data
	[fixture]
	(insert users (values fixture))
)

(defn insert-team-fixture-data
	[fixture]
	(insert teams (values fixture))
)

(defn insert-task-fixture-data
	[fixture]
	(insert tasks (values fixture))
)

(defn insert-db-fixture-data
	[]
	(insert-user-fixture-data fmodel/users-test-1)
	(insert-team-fixture-data fmodel/team-test-1)
	(insert-task-fixture-data fmodel/task-test-1)
)


