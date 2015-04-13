(ns darg.controller.team
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [conflict ok unauthorized]]
            [darg.model.role :as role]
            [darg.model.team :as team]
            [darg.model.user :as user]
            [korma.sql.fns :refer [pred-not=]]))

(defn create!
  "/api/v1/team

  Method: POST

  Create a team."
  [{:keys [params user]}]
  (log/info "Creating team:" params)
  (let [email (team/email-from-name (:name params))
        params (-> params
                   (select-keys [:name])
                   (assoc :email email))
        maybe-team (team/fetch-one-team {:email email})]
    (cond
      (some? maybe-team)
      (conflict "A team by that name already exists.")
      :else
      (let [team (team/create-team! params)]
        (role/create-role! {:admin true
                            :user_id (:id user)
                            :team_id (:id team)})
        (ok team)))))

(defn fetch
  "/api/v1/team/:id
  
  Method: GET
   
  Fetch a team."
  [{:keys [params user]}]
  (log/info "Fetching team:" params)
  (let [user-id (:id user)
        team-id (-> params :id read-string)]
    (cond
      (not (user/user-in-team? user-id team-id))
      (unauthorized "You are not a member of this team.")
      :else
      (ok (team/fetch-one-team {:id team-id})))))

(defn update!
  "/api/v1/team/:id

  Method: POST

  Update a team."
  [{:keys [params user]}]
  (log/info "Updating team:" params)
  (let [params (-> params
                   (select-keys [:id :name])
                   (update-in [:id] read-string))
        users-role (role/fetch-one-role {:user_id (:id user)
                                         :team_id (:id params)})]
    (cond
      (not= (:admin users-role) true)
      (unauthorized "You are not an admin for this team.")
      :else
      (ok (team/update-team! (:id params) params)))))
