(ns darg.controller.team.role
  (:require [clojure.tools.logging :as log]
            [darg.model.email :as email]
            [darg.model.user :as user]
            [darg.model.role :as role]
            [darg.model.team :as team]
            [darg.api.responses :refer [conflict ok unauthorized]]))

(defn create!
  "/api/v1/team/:id/role
   
  Method: POST
   
  Create a new role.
   
  Check to see if a user with that email exists; if so, create the role, then
  send them an email. If not, then just send them the email."
  [{:keys [params user]}]
  (log/info "Creating team role:" params)
  (let [team-id (-> params :team_id read-string)
        email (:email params)
        maybe-user (user/fetch-one-user {:email email})
        maybe-existing-role (role/fetch-one-role {:user_id (:id maybe-user)
                                                  :team_id team-id})]
    (cond
      (some? maybe-existing-role)
      (conflict 
        "A user with that email address is already a member of this team.")
      :else
      (do
        (when (some? (user/fetch-one-user {:email email}))
          (role/create-role! {:user_id (:id maybe-user)
                              :team_id team-id}))
        (email/send-team-invitation email team-id)
        (ok {:message "Invitation sent."})))))

(defn fetch-all
  "/api/v1/team/:id/role
   
  Method: GET
   
  Retrieve all of this team's roles."
  [{:keys [params user]}]
  (log/info "Fetching team roles:" params)
  (let [user-id (:id user)
        team-id (-> params :team_id read-string)]
    (cond
      (not (user/user-in-team? user-id team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok (team/fetch-team-roles {:id team-id})))))

(defn fetch-one
  "/api/v1/team/:team_id/user/:user_id
   
  Method: GET
  
  Retrieve a specific user's role on a team."
  [{:keys [params user]}]
  (log/info "Fetching role:" params)
  (let [current-user-id (:id user)
        target-user-id (-> params :user_id read-string)
        team-id (-> params :team_id read-string)]
    (cond
      (not (user/user-in-team? current-user-id team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok (role/fetch-one-role {:user_id target-user-id
                                :team_id team-id})))))

(defn delete!
  "/api/v1/team/:id/role
   
  Method: DELETE
   
  Remove a user from a team."
  [{:keys [params user]}]
  (log/info "Deleting team role:" params)
  (let [user-id (:id user)
        team-id (-> params :team_id read-string)
        target-user-id (-> params :user_id read-string)]
    (let [target-role (role/fetch-one-role-with-user {:user_id target-user-id
                                                      :team_id team-id})
          current-users-role (role/fetch-one-role {:user_id user-id
                                                   :team_id team-id})]
      (cond
        (not (user/user-in-team? user-id team-id))
        (unauthorized "You are not a member of this team.")
        (and (not= (:user_id target-role) user-id)
             (not= (:admin current-users-role) true))
        (unauthorized "You do not have deletion permissions for this role.")
        :else
        (do 
          (role/delete-role! {:id (:id target-role)})
          (ok "Deleted"))))))
