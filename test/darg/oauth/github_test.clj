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
            [environ.core :as env]
            [tentacles.users :as t-users]))

; Note: We cannot test the full web oauth flow, so we will generate an oAuth token using the authorizations API. Production environments should use the web authorization flow and callback function.

(with-db-fixtures)

(def test-username (str (env/env :darg-gh-username))) ;Github username
(def test-password (str (env/env :darg-gh-password))) ;Github password
(def test-github-oauth (oauth-github/create-auth-token test-username test-password "Temp Token For Testing22"))
(def access-token (:access_token (oauth-github/parse-oauth-response test-github-oauth)))

(defn cleanup-auth-tokens
  [f]
  (logging/info "Build Up")
  (f)
  (logging/info "Tear Down")
  (oauth-github/delete-all-auth-tokens (str (env/env :darg-gh-username)) 
                                       (str (env/env :darg-gh-password))))

(use-fixtures :once cleanup-auth-tokens)

(deftest we-can-successfully-get-an-oauth-token
  (is (some? access-token)))

(deftest we-can-insert-and-link-a-github-user
  (let [userid 3]
    (oauth-github/insert-and-link-github-user userid test-github-oauth)
    ;; Github token is linked to github user
    (is (= (:github_token_id (gh-users/fetch-one-github-user {:gh_login test-username})) 
           (gh-tokens/fetch-github-token-id {:gh_token access-token})))))

