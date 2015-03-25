(ns darg.model.github-users-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as logging]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as fixtures]
            [darg.model.github-users :as gh-users]
            [darg.model.users :as users]))

(with-db-fixtures)

(def dargtester1-github-id (:id fixtures/test-github-user-1))
(def dargtester2-github-id 31173233)
(def dargtester2-github-map {:gh_avatar_url "https://avatars.githubusercontent.com/u/10094188?v=3", :id 31173233, :gh_login "dargtester2" :gh_email nil :github_token_id nil})


; API test
(deftest we-can-get-a-user-from-the-github-api
  (is (not-empty (gh-users/github-api-get-user "dargtester1"))))

#_(deftest we-can-get-the-current-user
    (is (not-empty (gh-users/github-api-get-current-user authtoken))))

; DB tests

(deftest we-can-retrieve-github-users-from-db
  (is (not-empty (gh-users/fetch-github-user {:gh_login "dargtester1"}))))

(deftest we-can-fetch-github-userid
  (is (= dargtester1-github-id (gh-users/fetch-github-user-id {:gh_login "dargtester1"}))))

(deftest we-can-insert-github-user-into-the-db
  (gh-users/create-github-user dargtester2-github-map)
  (is (not-empty (gh-users/fetch-github-user {:id dargtester2-github-id}))))

(deftest we-can-update-a-github-user-in-the-db
  (gh-users/update-github-user dargtester1-github-id {:gh_email "dargtester1@darg.io"})
  (is (not-empty (gh-users/fetch-github-user {:gh_email "dargtester1@darg.io"}))))

(deftest we-can-link-a-user-to-a-github-account
  (gh-users/create-github-user dargtester2-github-map)
  (users/link-github-user 3 dargtester2-github-id)
  (is (= dargtester2-github-id 
         (:github_user_id (users/fetch-user-by-id 3)) 
         (gh-users/fetch-github-user-id {:gh_login (:gh_login dargtester2-github-map)}))))


