(ns darg.model.github-users-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as logging]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.github-users :as gh-users]))

(with-db-fixtures)

(def dargtester1-id 10094188)
(def dargtester2-id 31173233)
(def dargtester2-map {:gh_avatar_url "https://avatars.githubusercontent.com/u/10094188?v=3", :gh_user_id 31173233, :gh_login "dargtester2" :gh_email nil :github_tokens_id nil})

; API test
(deftest we-can-get-a-user-from-the-github-api
  (is (not-empty (gh-users/github-api-get-user "dargtester1"))))

; (deftest we-can-get-the-current-user
;   (is (not-empty (gh-users/github-api-get-current-user authtoken))))

; DB tests

(deftest we-can-retrieve-github-users-from-db
  (is (not-empty (gh-users/fetch-github-user {:gh_login "dargtester1"}))))

(deftest we-can-fetch-github-userid
  (is (= dargtester1-id (gh-users/fetch-github-user-id {:gh_login "dargtester1"}))))

(deftest we-can-insert-github-user-into-the-db
  (gh-users/create-github-user dargtester2-map)
  (is (not-empty (gh-users/fetch-github-user {:gh_user_id dargtester2-id}))))

(deftest we-can-update-a-github-user-in-the-db
  (gh-users/update-github-user dargtester1-id {:gh_email "dargtester1@darg.io"})
  (is (not-empty (gh-users/fetch-github-user {:gh_email "dargtester1@darg.io"}))))



