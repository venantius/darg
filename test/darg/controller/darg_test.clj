(ns darg.controller.darg-test
  (:require [clj-time.format :as f]
            [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.controller.darg :as api]
            [darg.model.darg :as darg]))

(with-db-fixtures)

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:team_id "1"}}
        response (api/get-darg sample-request)]
    (is (= (:status response) 200))))

(deftest user-can-view-team-darg
  (let [request {:user {:id 4 :email "test-user2@darg.io"}
                        :params {:team_id "1"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg request)]
    (is (= status 200))))

(deftest user-cant-view-team-darg-if-theyre-not-on-that-team
  (let [request {:user {:id 5 :email "test@darg.io"}
                        :params {:team_id "1"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg request)]
    (is (= 401 status))
    (is (= body 
           {:message "You are not a member of this team."}))))

(deftest get-team-darg-by-date-works
  (let [request {:user {:id 4 :email "test-user2@darg.io"}
                        :params {:team_id "1" :date "2015-04-30"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg-by-date request)]
    (is (= 200 status))
    (is (= body (darg/team-timeline 1 (f/parse "2015-04-30"))))))

(deftest get-team-darg-by-date-returns-401-on-unauthorized
  (let [request {:user {:id 5 :email "test@darg.io"}
                        :params {:team_id "1" :date "2015-04-30"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg-by-date request)]
    (is (= 401 status))
    (is (= body 
           {:message "You are not a member of this team."}))))
