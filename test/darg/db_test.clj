(ns darg.db-test
  (:require [clojure.test :refer :all]
            [korma.db :as korma]
            [korma.core :refer :all]
            [darg.db :as db]
            [darg.fixtures :refer [with-db-fixtures]]
            [lobos.core :as lobos]
            [darg.db-util :as dbutil]
            [lobos.config :as lconfig]))

(with-db-fixtures)

(deftest darg-db-is-assigned
  (is korma/_default))