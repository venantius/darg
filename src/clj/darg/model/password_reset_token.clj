(ns darg.model.password-reset-token
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [crypto.random :as random]
            [darg.db.entities :refer [password-reset-token]]
            [korma.core :refer [insert select sqlfn values where]]))

(defn generate-token
  "Generate a unique token."
  []
  (random/base32 35))

(defn create!
  "Create a password reset token. Takes a map of fields, including the
  following required key:

    :user_id - the id for the user whose password is being reset"
  [params]
  (insert password-reset-token
          (values
            (assoc params
                   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))
                   :token (generate-token)))))

(defn fetch-one-valid
  "Fetch a single valid (non-expired) password reset token"
  [params]
  (first (select password-reset-token
                 (where params)
                 (where {:expires_at [> (sqlfn now)]}))))

(defn valid-token?
  "Is this token still valid?"
  [token]
  (t/after? (c/from-sql-time (:expires_at token)) (t/now)))
