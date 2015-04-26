(ns darg.controller.user.email-confirmation
  "API endpoints for email confirmation"
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [not-found ok unauthorized]]
            [darg.model.user :as user]
            [darg.model.user.email-confirmation :as conf]))

(defn create!
  "Create a new email confirmation token and e-mail it to the user."
  [{:keys [session]}]
  (let [user (user/fetch-one-user {:id (:id session)})
        conf (conf/create-user-email-confirmation! 
               {:user_id (:id user)})]
    (conf/send-email-confirmation user conf)
    (log/info conf)
    {:status 200
     :message "E-mail confirmation sent!"}))

(defn confirm!
  "Confirm a target user's email address"
  [{:keys [params user] :as request}]
  (log/info params)
  (let [current-user (user/fetch-one-user {:id (:id user)})
        token (conf/fetch-one-user-email-confirmation {:token (:token params)})]
    (cond 
      (not token)
      (not-found "Token not found")
      (not= (:id current-user) (:user_id token))
      (unauthorized "This confirmation token is for another user.")
      :else
      (do
        (user/update-user! (:id user) {:confirmed_email true})
        (ok "Email address confirmed.")))))
