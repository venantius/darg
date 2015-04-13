(ns darg.controller.team.role-test
  (:require [clojure.test :refer :all]
            [darg.controller.team.role :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.role :as role]
            [darg.model.team :as team]))

(with-db-fixtures)

(deftest create!-makes-a-role-if-the-user-exists
  (with-redefs [darg.services.mailgun/send-message (constantly nil)]
    (is (nil? (role/fetch-one-role {:team_id 2 :user_id 7})))
    (let [request {:request-method :post
                   :params {:team_id "2" :email "venantius@gmail.com"}
                   :user {:id 4 :email "test-user2@darg.io"}}
          {:keys [status body]} (api/create! request)]
      (is (= status 200))
      (is (= body
             {:message "Invitation sent."}))
      (is (some? (role/fetch-one-role {:team_id 2
                                       :user_id 7}))))))

(deftest create!-returns-conflict-if-the-role-already-exists
  (is (some? (role/fetch-one-role {:team_id 1 :user_id 7})))
  (let [request {:request-method :post
                 :params {:team_id "1" :email "venantius@gmail.com"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [status body]} (api/create! request)]
    (is (= status 409))
    (is (= body
           {:message "A user with that email address is already a member of this team."}))))

(deftest fetch-one-works
  (let [request {:request-method :get
                 :params {:team_id "1" :user_id "4"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [status body]} (api/fetch-one request)]
    (is (= status 200))
    (is (= body
           (role/fetch-one-role {:team_id 1 :user_id 4})))))

(deftest fetch-one-doesnt-work-if-were-not-on-the-team
   (let [request {:request-method :get
                 :params {:team_id "2" :user_id "4"}
                 :user {:id 7 :email "venantius@gmail.com"}}
        {:keys [status body]} (api/fetch-one request)]
    (is (= status 401))
     (is (= body 
            {:message "You are not a member of this team."}))))

(deftest fetch-all-works
  (let [request {:request-method :get
                 :params {:team_id "1"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/fetch-all request)]
    (is (= status 200))
    (is (= body
           (team/fetch-team-roles {:id 1})))))

(deftest we-cant-fetch-if-were-not-on-that-team
  (let [request {:request-method :get
                 :params {:team_id "1"}
                 :user {:id 5 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/fetch-all request)]
    (is (= status 401))
    (is (= body {:message "You are not a member of this team."}))))

(deftest we-can-delete-ourselves
  (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))
  (let [request {:request-method :delete
                 :params {:team_id "1" :user_id "3"}
                 :user {:id 3}}
        {:keys [status body]} (api/delete! request)]
    (is (= status 200))
    (is (nil? (role/fetch-one-role {:user_id 3 :team_id 1})))))

(deftest we-can-delete-if-were-an-admin
  (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))
  (let [request {:request-method :delete
                 :params {:team_id "1" :user_id "3"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [status body]} (api/delete! request)]
    (is (= status 200))
    (is (nil? (role/fetch-one-role {:user_id 3 :team_id 1})))))

(deftest we-cant-delete-if-were-not-on-the-team
  (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))
  (let [request {:request-method :delete
                 :params {:team_id "1" :user_id "3"}
                 :user {:id 2}}
        {:keys [status body]} (api/delete! request)]
    (is (= status 401))
    (is (= body {:message "You are not a member of this team."}))
    (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))))

(deftest we-cant-delete-if-were-not-an-admin-or-the-user
  (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))
  (let [request {:request-method :delete
                 :params {:team_id "1" :user_id "3"}
                 :user {:id 7}}
        {:keys [status body]} (api/delete! request)]
    (is (= status 401))
    (is (= body {:message "You do not have deletion permissions for this role."}))
    (is (some? (role/fetch-one-role {:user_id 3 :team_id 1})))))
