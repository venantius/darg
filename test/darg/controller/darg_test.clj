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

(deftest user-can-view-team-darg
  (let [request {:user {:id 4 :email "test-user2@darg.io"}
                        :params {:team_id "1"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg request)]
    (is (= status 200))
    (println body)))

(deftest user-cant-view-team-darg-if-theyre-not-on-that-team
  (let [request {:user {:id 7 :email "test-user2@darg.io"}
                        :params {:team_id "1"}
                        :request-method :get}
        {:keys [status body]} (api/get-team-darg request)]
    (is (= status 401))
    (is (= body 
           {:message "You are not a member of this team."}))))
