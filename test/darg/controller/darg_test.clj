(ns darg.controller.darg-test
  (:require [clj-time.format :as f]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixture-data]
            [darg.controller.darg :as api]
            [darg.model.darg :as darg]))

(with-db-fixtures)

(deftest get-team-darg-by-date-works
  (let [request {:user {:id 4 :email "test-user2@darg.io"}
                        :params {:team_id "1" :date "2015-04-30"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg-by-date request)]
    (is (= 200 status))
    (is (= body (darg/team-timeline 
                  fixture-data/test-user-4 
                  1 
                  (f/parse "2015-04-30"))))))

(deftest get-team-darg-by-date-returns-401-on-unauthorized
  (let [request {:user {:id 5 :email "test@darg.io"}
                        :params {:team_id "1" :date "2015-04-30"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg-by-date request)]
    (is (= 401 status))
    (is (= body 
           {:message "You are not a member of this team."}))))
