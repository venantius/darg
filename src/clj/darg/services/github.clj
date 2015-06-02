(ns darg.services.github
  "All sorts of non-model GitHub functions.
   
   Most functions take a Darg user that should have a :github_access_token
   key."
  (:require [darg.model.user :as user]
            [tentacles.core :as t]))

(defn- auth-map
  [user]
  {:pre [(some? (:github_access_token user))]}
  "Format the access token the way Tentacles expects"
  {:oauth-token (:github_access_token user)})

(defn user-repos
  "Retrieve this user's repositories."
  [user]
  (tentacles.repos/repos (auth-map user)))

(defn repos
  "Retrieve all repos this user is involved with, including org repos they
   have access to."
  [user]
  (tentacles.repos/repos
    (merge (auth-map user)
           {:accept "application/vnd.github.moondragon+json"})))
