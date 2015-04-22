(ns darg.model.team.invitation
  "Invitations to join a particular team"
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [darg.db.entities :refer [team-invitation]]
            [darg.util.token :as token]
            [korma.core :refer [insert select sqlfn values where]]))

(defn create-team-invitation!
  [params]
  (insert team-invitation
          (values
            (assoc params
                   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))
                   :token (token/generate-token)))))
