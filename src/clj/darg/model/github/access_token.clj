(ns darg.model.github.access-token
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [korma.core :refer :all]
            [schema.core :as s]))

(defmodel db/github-access-token 
  {(s/optional-key :darg_user_id) s/Int
   (s/optional-key :token) s/Str
   (s/optional-key :scope) s/Str})
