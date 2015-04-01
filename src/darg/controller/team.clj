(ns darg.controller.team
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [conflict ok unauthorized]]
            [darg.model.role :as role]
            [darg.model.team :as team]
            [korma.sql.fns :refer [pred-not=]]))

(defn create!
  "/api/v1/team

  Method: POST

  Create a team."
  [{:keys [params user]}]
  (log/info "Creating team:" params)
  (let [params (select-keys params [:name :email])]
    (if (team/fetch-one-team {:email (:email params)})
      (conflict "A team with that email already exists!")
      (let [team (team/create-team! params)]
        (role/create-role! {:admin true
                            :user_id (:id user)
                            :team_id (:id team)})
        (ok team)))))

(defn update!
  "/api/v1/team/:id

  Method: POST

  Update a team."
  [{:keys [params user]}]
  (log/info "Updating team:" params)
  (let [params (-> params
                   (select-keys [:id :name :email])
                   (update-in [:id] read-string))
        users-role (role/fetch-one-role {:user_id (:id user)
                                         :team_id (:id params)})
        maybe-existing-team (team/fetch-one-team {:id [pred-not= (:id params)]
                                                  :email (:email params)})]
    (cond
      (not= (:admin users-role) true)
      (unauthorized "You are not an admin for this team.")
      (some? maybe-existing-team)
      (conflict "A team with that email already exists!")
      :else
      (ok (team/update-team! (:id params) params)))))
