(ns darg.model.team.invitation
  "Invitations to join a particular team"
  (:require [darg.db.entities :refer [team-invitation]]
            [darg.model :refer [defmodel]]))

(defmodel team-invitation {})
