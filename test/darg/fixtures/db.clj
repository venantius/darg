(ns darg.fixtures.db
	(:use [korma.core :as korma]
		  [clj-yaml.core]
		  [darg.model :refer :all]
		  )
	)

(def fixture-yaml "./src/darg/resources/fixtures.yml")
(def table-list [users teams tasks])
(def fixture-map (parse-string (slurp fixture-yaml)))

(defn insert-db-fixture-data 
	[]
	(for [x table-list] 
		(insert x (values ((keyword (:name x)) fixture-map)))))
