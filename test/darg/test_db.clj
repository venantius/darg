(ns darg.test-db
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.model :refer :all]
            [lobos.core :as lobos]
            [lobos.config :as lconfig]
            ))

(deftest darg-db-is-assigned
  (is korma/_default))

(deftest we-can-insert-into-the-db
	(db/set-korma-db)
	(insert users (values{:id 99, :email "domo@test.com", :username "arrigato"}))
	(is (= "arrigato" (:username (first (select users (where {:id 99}))))))
  )

(deftest we-can-update-in-the-db
	(db/set-korma-db)
       (insert users (values {:id 98, :email "domo2@test.com", :username "otakon"}))
	(update users (set-fields {:username "irrashaimase"}) (where {:id 98}))
	(is (= "irrashaimase" (:username (first (select users (where {:id 98}))))))
)

(deftest we-can-delete-from-the-db
	(db/set-kroma-db)
	(delete users (where{:id 99}))
	(is (= nil (first (select users (where {:id 99})))))
)



