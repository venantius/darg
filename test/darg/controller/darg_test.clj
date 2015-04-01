(ns darg.controller.darg-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.controller.darg :as api]))

(with-db-fixtures)

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:team_id "1"}}
        response (api/get-darg sample-request)]
    (is (= (:status response) 200))))
