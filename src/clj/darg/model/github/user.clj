(ns darg.model.github.user
  "Functions for working with GitHub users"
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [darg.model.github.access-token :as access-token]
            [darg.service.github :as github]
            [schema.core :as s]))

(defmodel db/github-user
  {(s/optional-key :id) s/Int
   (s/optional-key :darg_user_id) s/Int
   (s/optional-key :access_token_id) s/Int
   (s/optional-key :login) s/Str})

(defn create-from-user!
  "Given a user with a valid GitHub access token, create a GitHub user
   record in our database."
  [{:keys [id github_access_token] :as user}]
  {:pre [(some? github_access_token)]}
  (let [access-token (access-token/fetch-one-github-access-token 
                       {:token github_access_token})
        me (github/me user)]
    (create-github-user! {:darg_user_id id
                          :access_token_id (:id access-token)
                          :login (:login me)})))
