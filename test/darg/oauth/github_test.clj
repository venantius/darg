(ns darg.oauth.github-test
  (:require [cheshire.core :refer [generate-string]]
            [clojure.test :refer :all]
            [clojure.tools.logging :as logging]
            [darg.api.responses :as responses]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.github-users :as gh-users]
            [darg.model.github-tokens :as gh-tokens]
            [darg.model.users :as users]
            [darg.oauth.github :as oauth-github]
            [tentacles.users :as t-users]))

; Note: We cannot test the full web oauth flow, so we will generate an oAuth token using the authroizations API. Production environments should use the web authorization flow and callback function.

(with-db-fixtures)

(def test-username (str (System/getenv "DARG_GH_USERNAME"))) ;Github username
(def test-password (str (System/getenv "DARG_GH_PASSWORD"))) ;Github password
(def test-github-oauth (oauth-github/create-authorization test-username test-password "Temp Token For Testing22"))
(def access-token (:access_token (oauth-github/parse-oauth-response test-github-oauth)))

(defn test-setup
  [f]
  (logging/info "Build Up")
  (f)
  (logging/info "Tear Down")
  (oauth-github/delete-all-authorizations (str (System/getenv "DARG_GH_USERNAME")) 
                                          (str (System/getenv "DARG_GH_PASSWORD"))))

(use-fixtures :once test-setup)

(deftest we-can-successfully-get-an-oauth-token
  (is access-token))

(deftest we-can-insert-and-link-a-github-user
  (let [userid 3]
    (oauth-github/insert-and-link-github-user userid test-github-oauth)
    ; Check that all links are created
    ;; Github user is linked to user, and matches access-token's user id
    (is (= (:id (gh-users/fetch-one-github-user {:gh_login test-username}))
           (:github_users_id (users/fetch-user-by-id 3))
           (:id (tentacles.users/me {:oauth_token access-token}))))
    ;; Github token is linked to github user
    (is (= (:github_tokens_id (gh-users/fetch-one-github-user {:gh_login test-username})) 
           (gh-tokens/fetch-github-token-id {:gh_token access-token})))))

