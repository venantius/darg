(ns darg.model.github-tokens
  (:require [darg.model :as db]
            [korma.core :refer :all]))

(defn insert-github-token
  "Insert a github-token into the database
  Takes a map of fields to insert into the db.
  Requires:
  :gh_token - the actual authorization token"
  [params]
  (insert db/github-tokens (values params)))

(defn fetch-github-token
  [params]
  (select db/github-tokens (where params)))

(defn fetch-github-token-id
  [params]
  (:gh_token_id (first (select db/github-tokens (fields :gh_token_id) (where params)))))	