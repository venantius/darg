(ns darg.model.github.access-token
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]
            [schema.core :as s]))

(defmodel db/github-access-token 
  {(s/optional-key :darg_user_id) s/Int
   (s/optional-key :token) s/Str
   (s/optional-key :scope) s/Str})

(defn create-or-update-github-access-token!
  "Like it says on the tin. This sort of function is a little sloppy but it's
   helpful for keeping our GitHub OAuth controller clean."
  [{:keys [darg_user_id] :as at}]
  (let [maybe-at (fetch-one-github-access-token {:darg_user_id darg_user_id})]
    (if maybe-at
      (update-github-access-token! 
       (:id maybe-at)
       at)
      (create-github-access-token! at))))
