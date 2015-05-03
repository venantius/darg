(ns darg.controller.task-test
  (:require [clojure.test :refer :all]
            [darg.controller.task :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.task :as task]
            [darg.process.server :as server]))

(with-db-fixtures)

(deftest task-creation-endpoint-works
  (let [request {:request-method :post 
                 :params {:task "I created this task through the API"
                          :timestamp "2015-05-03T23:05:31.487Z" 
                          :date "2015-03-15"
                          :team_id "2"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/create! request)]
    (is (= status 200))
    (is (some? (task/fetch-one-task {:id (:id body)})))))

(deftest we-cant-create-a-task-if-were-not-on-that-team
  (let [request {:request-method :post 
                 :params {:task "I created this task through the API"
                          :timestamp "2015-05-03T23:05:31.487Z"
                          :date "2015-03-15"
                          :team_id "6"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        response (api/create! request)]
    (is (= (:status response) 401))))

(deftest we-can-update-an-existing-task
  (let [request {:request-method :post
                 :params {:task "This isn't what it used to be."
                          :team_id "1"
                          :id "1"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/update! request)]
    (is (= status 200))
    (is (= (:task (task/fetch-one-task {:id 1}))
           "This isn't what it used to be."))))

(deftest we-cant-update-a-task-we-dont-own
  (let [request {:request-method :post
                 :params {:task "This isn't what it used to be."
                          :team_id "1"
                          :id "1"}
                 :user {:id 3 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/update! request)]
    (is (= status 401))))
