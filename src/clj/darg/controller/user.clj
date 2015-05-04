(ns darg.controller.user
  (:refer-clojure :exclude [get])
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :as responses]
            [darg.model.email :as email]
            [darg.model.role :as role]
            [darg.model.user :as user]
            [darg.model.user.email-confirmation :as email-conf]))

(defn create!
  "/api/v1/user

  Method: POST

  Signs a user up. This creates an account in our db and authenticates
  the user."
  [{:keys [params] :as request-map}]
  (log/info params)
  (let [{:keys [email token]} params]
    (cond
      (some? (user/fetch-one-user {:email email}))
      (responses/conflict "A user with that e-mail already exists.")
      (not (every? params [:email :name :password]))
      (responses/bad-request
       "The signup form needs an e-mail, a name, and a password.")
      :else
      (let [user (user/create-user-from-signup-form params)
            conf (email-conf/create-user-email-confirmation! {:user_id (:id user)})]
        (log/info token)
        (if (some? token)
          (role/create-role-from-token! user token))
        (email-conf/send-email-confirmation user conf)
        {:body {:message "Account successfully created"}
         :cookies {"logged-in" {:value true :path "/"}
                   "id" {:value (:id user) :path "/"}}
         :session {:authenticated true :email (:email params) :id (:id user)}
         :status 200}))))

(defn get
  "/api/v1/user/:id

  Method: GET

  Retrieve info on the targeted user."
  [{:keys [params user]}]
  (let [current-user-id (:id user)
        target-user-id (-> params :id read-string)
        team-ids (mapv :id (user/team-overlap current-user-id target-user-id))]
    (if (and (not= current-user-id target-user-id)
             (empty? team-ids))
      (responses/unauthorized "Not authorized.")
      (responses/ok
       (user/profile
        {:id target-user-id}
        team-ids)))))

(defn update!
  "/api/v1/user/:id

  Method: POST

  Update the user."
  [{:keys [user params session]}]
  (log/info params)
  (let [session-email (-> user :email clojure.string/lower-case)
        current-user (user/fetch-one-user {:email session-email})
        params (-> params
                   (update-in [:id] read-string)
                   (update-in [:send_digest_email] boolean)
                   (update-in [:send_daily_email] boolean)
                   (dissoc :confirmed_email)
                   (select-keys [:email :timezone :name :send_daily_email
                                 :confirmed_email :id :send_digest_email
                                 :digest_hour :email_hour]))]
    (if (or (= session-email (:email params))
            (nil? (user/fetch-one-user {:email (:email params)})))
      (let [updated-user (user/update-user! 
                           (:id params)
                           params)]
        {:status 200
         :session (assoc session :email (:email params))
         :body updated-user})
      (responses/conflict "User with that e-mail already exists"))))
