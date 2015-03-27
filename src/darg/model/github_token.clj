(ns darg.model.github-token
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]))

(defmodel db/github-token)

(defn fetch-github-token-id
  [params]
  (:id (first (select db/github-token (fields :id) (where params)))))
