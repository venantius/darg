(ns darg.model.github.oauth-state
  "The 'state' field for the GitHub OAuth flow. Keeping track of this both
   lets us make sure that the OAuth flow isn't being spoofed, and lets us
   know which team's GitHub page to redirect the user to when the flow
   is complete."
  (:require [darg.db.entities :as db]
            [darg.model :refer [defmodel]]
            [schema.core :as s]))

(defmodel db/github-oauth-state
  {(s/optional-key :id) s/Int
   (s/optional-key :darg_user_id) s/Int
   (s/optional-key :darg_team_id) s/Int
   (s/optional-key :state) s/Str})
