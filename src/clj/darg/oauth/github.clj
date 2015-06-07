(ns darg.oauth.github
  "For details on the Github OAuth flow, refer to documentation here: 
   
   https://developer.github.com/v3/oauth/#web-application-flow. 
   
   This namespace includes the callback function and other utility functions 
   for creating github auth-tokens and attaching them to a darg user account"
  (:require [cemerick.url :as url]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [darg.model.github.access-token :as access-token]
            [darg.model.github.team-settings :as team-settings]
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
                     :scope "user,repo,read:org"
                     :state state})]
    (assoc-in
      (response/redirect (str url))
      [:headers "Access-Control-Allow-Origin"]
      "*")))

;; Parses Github OAuth response and updates tables

(defn access-token-exchange
  "POST to GitHub with the code from the callback."
  [code]
  (let [response (http/post "https://github.com/login/oauth/access_token"
                            {:headers  {"Accept" "application/json"}
                             :query-params {:code code
                                            :client_id client-id
                                            :client_secret client-secret}})]
    (-> response :body (json/parse-string true))))

;; Everything after this is unused / depracated

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
