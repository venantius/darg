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
  (let [{:keys [email token]} params]
    (cond
      (some? (user/fetch-one-user {:email email}))
      (responses/conflict "A user with that e-mail already exists.")
      (not (every? params [:email :name :password]))
      (responses/bad-request
       "The signup form needs an e-mail, a name, and a password.")
      :else
      (let [user (user/create-user-from-signup-form params)]
        (log/info token)
        (if (some? token)
          (role/create-role-from-token! user token))
        (email-conf/create-and-send-email-confirmation user)
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
        maybe-existing-user (user/fetch-one-user {:email (:email params)})
        params (-> params
                   user/map->user
                   (dissoc :created_at :confirmed_email))]
    (if (and (some? maybe-existing-user)
             (not= maybe-existing-user current-user))
      (responses/conflict "User with that e-mail already exists")
      (do
        (when (not= session-email
                    (:email params))
          (user/update-user!
            (:id user)
            {:confirmed_email false})
          (email-conf/create-and-send-email-confirmation user))
        (let [updated-user (user/update-user! 
                             (:id user)
                             params)]
          {:status 200
           :session (assoc session :email (:email params))
           :body updated-user})))))
