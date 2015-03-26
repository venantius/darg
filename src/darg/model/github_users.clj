(ns darg.model.github-users
  (:require [cheshire.core :refer [parse-string]]
            [clojure.set :refer [rename-keys]]
            [darg.db.entities :as db]
            [korma.core :refer :all]
            [org.httpkit.client :as http]
            [tentacles.users :as t-users]
            [tentacles.repos :as t-repos]))

(defn create-github-user
  "Insert a user into the database.
  
  Required fields:
  :id - github user's userid
  :login - github username
  :avatar_url - URL that points to the GH user's6 gravatar
  :email - email address associated with the user's github account
  :github_token_id - Foreign Key for the associated github token"
  [params]
  (insert db/github-user (values params)))

(defn update-github-user
  "Updates the fields for a github-user.
  Takes a github-user-id as an integer and a map of fields + values to update."
  [id params]
  (update db/github-user (where {:id id}) (set-fields params)))

(defn fetch-github-user
  "returns a github-user map from the db
  Takes a map of fields for use in db lookup"
  [params]
  (select db/github-user (where params)))

(defn fetch-one-github-user
  "Returns the first github-user from fetch-github-user"
  [params]
  (first (fetch-github-user params)))

(defn fetch-github-user-id
  "Returns a github-user-id (integer)
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/github-user (fields :id) (where params)))))

(defn fetch-github-user-by-id
  "Returns the first github-user from fetch-github-user"
  [id]
  (first (select db/github-user (where {:id id}))))

(defn delete-github-user
  "Deletes a github-user from the database
  Takes a github-user-id as an integer"  
  [id]
  (delete db/github-user (where {:id id})))

;; Link Github Token

(defn link-github-token
  "Associates a github oAuth token with a github user
  Takes a github_users.id as the first value, and a github_tokens.id as the second"
  [github-users-id github-tokens-id]
  (update-github-user github-users-id {:github_token_id github-tokens-id}))

(defn fetch-github-user-token
  "Returns the access token for a given user"
  [github-user-id]
  (:gh_token (first (select db/github-user (where {:id github-user-id}) 
                            (with db/github-token (fields :gh_token))))))

;; Github API - User

(defn gh-api-user->github_user
  "Renames github user api response for inclusion in database"
  [api-response]
  (let [gh-user-fields-rename-map {:login :gh_login :avatar_url :gh_avatar_url}
        gh-user-fields `[:login :id :avatar_url]]
    (clojure.set/rename-keys (select-keys api-response gh-user-fields) gh-user-fields-rename-map)))

(defn github-api-get-current-user
  [access-token]
  (gh-api-user->github_user (t-users/me {:oauth_token access-token})))

(defn github-api-get-user
  [github-username]
  (gh-api-user->github_user (t-users/user github-username)))

;; Github API - Repos

(defn github-api-get-current-user-repos
  [github-userid]
  (t-repos/repos {:oauth_token (fetch-github-user-token github-userid)}))
