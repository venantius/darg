(ns darg.model.github-tokens
  (:require [darg.model :as db]
            [korma.core :refer :all]))

(defn create-github-token
  "Insert a github-token into the database
  Takes a map of fields to insert into the db.
  Requires:
  :gh_token - the actual authorization token
  
  Note: This function does not create a remote token. It should only be used to insert an existing token into the github-tokens table"
  [params]
  (insert db/github-token (values params)))

(defn fetch-github-token
  [params]
  (select db/github-token (where params)))

(defn fetch-one-github-token
  [params]
  (first (select db/github-token (where params))))

(defn fetch-github-token-id
  [params]
  (:id (first (select db/github-token (fields :id) (where params)))))
