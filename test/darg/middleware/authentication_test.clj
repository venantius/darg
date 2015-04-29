(ns darg.middleware.authentication-test
  (:require [clojure.test :refer :all]
            [darg.controller.task :as task-api]
            [darg.controller.user :as user-api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.middleware.authentication :as authn]
            [darg.model.user :as user]
            [darg.model.task :as task]))

(with-db-fixtures)

(deftest unauthenticated-user-returns-401
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:task "I created a new task!"
                                 :team-id 2
                                 :timestamp "Mar 10 2014"}}
        response ((authn/wrap-authentication
                    task-api/create!
                    authn/darg-auth-fn)
                  sample-request)
        test-user-id (:id (user/fetch-one-user {:email "test-user2@darg.io"}))]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "User not authenticated."}))
    (is (= (count (task/fetch-tasks-by-user-id test-user-id)) 5))))

(deftest user-cant-view-a-darg-without-an-email
  (let [sample-request {:session {:authenticated true :id 4}
                        :params {:id "4"}
                        :request-method :get}
        response ((authn/wrap-authentication
                    user-api/get
                    authn/darg-auth-fn)
                  sample-request)]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "User not authenticated."}))))
