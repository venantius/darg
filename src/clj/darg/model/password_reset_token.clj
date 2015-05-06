(ns darg.model.password-reset-token
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [darg.db.entities :refer [password-reset-token]]
            [darg.model :refer [defmodel]]
            [darg.util.token :as token]
            [korma.core :refer [insert select sqlfn values where]]))

(defmodel password-reset-token
  {})

(defn create!
  "Create a password reset token. Takes a map of fields, including the
  following required key:

    :user_id - the id for the user whose password is being reset"
  [params]
  (insert password-reset-token
          (values
            (assoc params
                   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))
                   :token (token/generate-token)))))

(defn fetch-one-valid
  "Fetch a single valid (non-expired) password reset token"
  [params]
  (first (select password-reset-token
                 (where params)
                 (where {:expires_at [> (sqlfn now)]}))))
