(ns darg.db-util
	(:use [korma.core :as korma]
		  [clj-yaml.core]
		  [darg.model :refer :all]
		  )
	)

(def fixture-yaml "./src/resources/fixtures.yml")
(def table-list [users teams tasks])
(def fixture-map (parse-string (slurp fixture-yaml)))


(defn add-to-users
[x params]
(insert x (values (
	(keyword (:name x)) params))))

