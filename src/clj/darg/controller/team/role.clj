(ns darg.controller.team.role
  (:require [clojure.tools.logging :as log]
            [darg.model.user :as user]
            [darg.model.role :as role]
            [darg.model.team :as team]
            [darg.api.responses :refer [ok unauthorized]]))

(defn fetch-all
  "/api/v1/team/:id/role
   
  Method: GET
   
  Retrieve all of this team's roles."
  [{:keys [params user]}]
  (log/info "Fetching team roles:" params)
  (let [user-id (:id user)
        team-id (-> params :id read-string)]
    (cond
      (not (user/user-in-team? user-id team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok (team/fetch-team-roles {:id team-id})))))

(defn delete!
  "/api/v1/team/:id/role
   
  Method: DELETE
   
  Remove a user from a team."
  [{:keys [params user]}]
  (log/info "Deleting team role:" params)
  (let [user-id (:id user)
        team-id (-> params :id read-string)
        role-id (-> params :role_id read-string)]
    (let [target-role (role/fetch-one-role-with-user {:id role-id})
          current-users-role (role/fetch-one-role {:user_id user-id
                                                   :team_id team-id})]
      (log/warn target-role)
      (log/warn (:user_id target-role) user-id)
      (cond
        (not (user/user-in-team? user-id team-id))
        (unauthorized "You are not a member of this team.")
        (and (not= (:user_id target-role) user-id)
             (not= (:admin current-users-role) true))
        (unauthorized "You do not have deletion permissions for this role.")
        :else
        (do 
          (role/delete-role! {:id role-id})
          (ok "Deleted"))))))
