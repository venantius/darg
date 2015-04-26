(ns darg.controller.user.email-confirmation-test
  (:require [clojure.test :refer :all]
            [darg.controller.user.email-confirmation :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.user :as user]))

(with-db-fixtures)

(deftest we-can-confirm-a-users-email
  (with-redefs [darg.services.mailgun/send-message (constantly true)]
    (let [user (user/fetch-one-user {:id 4})]
      (is (false? (:confirmed_email user)))
      (let [request {:user {:id 4}
                     :params {:token "KJGWF37QJ3A7FRTMVGFGHC7Y3X4CCLOANY6QCJYVVDIWDF4TXF65LL52"}
                     :request-method :post}
            {:keys [status body]} (api/confirm! request)]
        (is (= 200 status))
        (is (= "Email address confirmed." body))
        (is (true? (:confirmed_email (user/fetch-one-user {:id 4}))))))))
