(ns darg.controller.task-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [ring.mock.request :as mock-request]))

(deftest task-creation-endpoint-works
  (is (= 0 1)))

(deftest we-can-update-an-existing-task
  (is (= 0 1)))
