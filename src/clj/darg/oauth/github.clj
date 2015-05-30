(ns darg.oauth.github
  "For details on the Github OAuth flow, refer to documentation here: 
   
   https://developer.github.com/v3/oauth/#web-application-flow. 
   
   This namespace includes the callback function and other utility functions 
   for creating github auth-tokens and attaching them to a darg user account"
  (:require [cemerick.url :as url]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [darg.model.github-user :as gh-user]
            [darg.model.github-token :as gh-token]
            [darg.api.responses :as responses]
            [environ.core :as env]
            [clj-http.client :as http]
            [ring.util.response :as response]
            [slingshot.slingshot :refer [try+]]
            [tentacles.oauth :as t-oauth]))


(def client-id (env/env :gh-basic-client-id))
(def client-secret (env/env :gh-basic-secret-id))

(def github-login-url "https://github.com/login/oauth/authorize")

(defn github-redirect-url
  "Returns a redirect response to the GitHub access page."
  [state]
  (let [url (assoc 
             (url/url github-login-url)
             :query {:client_id client-id
                     :redirect-uri "http://localhost:8080/oauth/github"
                     :scope "user"
                     :state state})]
    (response/redirect (str url))))

;; Parses Github OAuth response and updates tables

(defn insert-and-link-github-user
  "This function is usually called from darg.oauth.github/callback. 
   
   It takes a Github OAuth response and darg userid, and updates the database 
   with the access-token and related Github user information. The Github user 
   and Github token are then linked to the provided userid."
  [userid body]
  (let [access-token (:access_token 5)]
    (gh-token/create-github-token! {:gh_token access-token})
    ;Link token to github user
    (let [github-user (assoc-in (gh-user/github-api-get-current-user access-token)
                                [:github_token_id]
                                (gh-token/fetch-github-token-id {:gh_token access-token}))
          github-user-id (:id github-user)]
      ;if a github user already exists, update it if not, create it
      (if (empty? (gh-user/fetch-github-user-by-id github-user-id))
        (gh-user/create-github-user! github-user)
        (gh-user/update-github-user! github-user-id github-user)))))

(defn access-token-exchange
  "POST to GitHub with the code from the callback."
  [code]
  (let [response (http/post "https://github.com/login/oauth/access_token"
                            {:headers  {"Accept" "application/json"}
                             :query-params {:code code
                                            :client_id client-id
                                            :client_secret client-secret}})]
    (-> response :body (json/parse-string true))))

;; Authorizations API, Used to create and manage OAuth authorization tokens for test cases

(defn create-auth-token
  [username password note]
  (let [options {:auth (str username ":" password)
                 :client_id client-id
                 :client_secret client-secret
                 :note note
                 :scopes "user:email"}]
    ; Simulate web-flow OAuth response
    {:body (-> (t-oauth/create-auth options)
               (select-keys [:scopes :token :id])
               (clojure.set/rename-keys {:token :access_token})
               (json/generate-string))}))

(defn delete-auth-token!
  [username password id]
  (let [options {:auth (str username ":" password)}]
    (t-oauth/delete-auth id options)))

(defn list-auth-tokens
  [username password]
  (let [options {:auth (str username ":" password)}]
    (t-oauth/authorizations options)))

(defn list-auth-token-ids
  [username password]
  (let [options {:auth (str username ":" password)}]
    (map :id (t-oauth/authorizations options))))

(defn delete-all-auth-tokens
  [username password]
  (let [token-ids (list-auth-token-ids username password)]
    (for [x token-ids] 
      (delete-auth-token! username password x))))
