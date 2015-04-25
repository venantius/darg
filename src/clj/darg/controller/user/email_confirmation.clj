(ns darg.controller.user.email-confirmation
  "API endpoints for email confirmation"
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [not-found unauthorized]]
            [darg.model.user :as user]
            [darg.model.user.email-confirmation :as conf]))

(defn confirm!
  "Confirm a target user's email address"
  [{:keys [params session user] :as request}]
  (log/info params)
  (let [current-user (user/fetch-one-user 
                      {:email
                       (-> user :email clojure.string/lower-case)})
        token (conf/fetch-one-user-email-confirmation {:token (:token params)})]
    (cond 
      (not token)
      (not-found "Token not found")
      (not= (:id current-user) (:user_id token))
      (unauthorized "This confirmation token is for another user.")
      :else
      (do
        (log/warn token)
        (user/update-user! (:id user) {:confirmed_email true})
        {:status 200
         :body "Email address confirmed."}))))
