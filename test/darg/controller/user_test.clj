(ns darg.controller.user-test
  (:require [clojure.test :refer :all]
            [darg.controller.user :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.user :as user]))

(with-db-fixtures)

(deftest update-user-works
  (let [params {:email "test-user5@darg.io"
                :name "Fiona the Human"
                :id "4"}
        sample-request {:user {:email "test-user2@darg.io"}
                        :request-method :post
                        :params params}
        response (api/update! sample-request)]
    (is (= (:status response) 200))
    (is (some? (user/fetch-one-user {:email "test-user5@darg.io"})))))

(deftest we-cant-update-a-user-to-have-an-email-of-an-existing-user
  (let [params {:email "david@ursacorp.io"
                :name "David Jarvis"
                :id "4"}
        sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params params}
        response (api/update! sample-request)]
    (is (= (:status response) 409))
    (is (= 1 (count (user/fetch-user {:email "david@ursacorp.io"}))))))
