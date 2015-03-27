(ns darg.middleware.authentication-test
  (:require [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.middleware.authentication :as authn]
            [darg.model.user :as user]
            [darg.model.task :as task]
            ))

(with-db-fixtures)

(deftest unauthenticated-user-returns-401
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params {:email "test-user2@darg.io"
                                 :team-id 2
                                 :date "Mar 10 2014"
                                 :darg ["Cardio" "Double Tap" "Beware of Bathrooms"]}}
        response ((authn/wrap-authentication
                    api/post-darg
                    authn/darg-auth-fn)
                  sample-request)
        test-user-id (user/fetch-user-id {:email "test-user2@darg.io"})]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "User not authenticated."}))
    (is (= (count (task/fetch-tasks-by-user-id test-user-id)) 4))))

(deftest user-cant-view-a-darg-without-an-email
  (let [sample-request {:session {:authenticated true}
                        :request-method :get}
        response ((authn/wrap-authentication
                    api/get-darg
                    authn/darg-auth-fn)
                  sample-request)]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "User not authenticated."}))))
