(ns darg.model.github-users
  (:require [darg.model :as db]
            [cheshire.core :refer [parse-string]]
            [clojure.set :refer [rename-keys]]
            [korma.core :refer :all]
            [org.httpkit.client :as http]
            [tentacles.users :as t-users]))

(def gh-user-fields `[:login :id :avatar_url])
(def gh-user-fields-rename-map {:login :gh_login 
                                :avatar_url :gh_avatar_url})

(defn create-github-user
  "Insert a user into the database.
  
  Required fields:
  :id - github user's userid
  :login - github username
  :avatar_url - URL that points to the GH user's6 gravatar
  :email - email address associated with the user's github account
  :github_tokens_id - Foreign Key for the associated github token"
  [params]
  (insert db/github-users (values params)))

(defn update-github-user
  "Updates the fields for a github-user.
  Takes a github-user-id as an integer and a map of fields + values to update."
  [id params]
  (update db/github-users (where {:id id}) (set-fields params)))

(defn fetch-github-user
  "returns a github-user map from the db
  Takes a map of fields for use in db lookup"
  [params]
  (select db/github-users (where params)))

(defn fetch-one-github-user
  "Returns the first github-user from fetch-github-user"
  [params]
  (first (fetch-github-user params)))

(defn fetch-github-user-id
  "Returns a github-user-id (integer)
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/github-users (fields :id) (where params)))))

(defn fetch-github-user-by-id
  "Returns the first github-user from fetch-github-user"
  [id]
  (first (select db/github-users (where {:id id}))))

(defn delete-user
  "Deletes a github-user from the database
  Takes a github-user-id as an integer"  
  [id]
  (delete db/github-users (where {:id id})))

;; Link Github Token

(defn link-github-token
  "Associates a github oAuth token with a github user
  Takes a github_users.id as the first value, and a github_tokens.id as the second"
  [github-users-id github-tokens-id]
  (update-github-user github-users-id {:github_tokens_id github-tokens-id}))

;; Github API - User

(defn github-api-get-current-user
  [access-token]
  (let [options {:oauth_token access-token}]
    (clojure.set/rename-keys (select-keys (t-users/me options) gh-user-fields) gh-user-fields-rename-map)))

(defn github-api-get-user
  [github-username]
  (clojure.set/rename-keys (select-keys (t-users/user github-username) gh-user-fields) gh-user-fields-rename-map))




