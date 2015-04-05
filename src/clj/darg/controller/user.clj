(ns darg.controller.user
  (:refer-clojure :exclude [get])
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :as responses]
            [darg.model.user :as user]))

;; TODO: for both get-user and get-user-profile (which should probably be
;; renamed to get-current-user and get-any-user) we should add in a list of
;; teams that they're on (as well as some thinking around which of those teams
;; they can actually see.
;; https://github.com/ursacorp/darg/issues/178

(defn create!
  "/api/v1/user

  Method: POST

  Signs a user up. This creates an account in our db and authenticates
  the user."
  [request-map]
  (let [params (:params request-map)
        email (:email params)]
    (cond
      (some? (user/fetch-one-user {:email email}))
      (responses/conflict "A user with that e-mail already exists.")
      (not (every? params [:email :name :password]))
      (responses/bad-request
       "The signup form needs an e-mail, a name, and a password.")
      :else
      (let [user (user/create-user-from-signup-form params)]
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
  (let [email (:email user)
        current-user (user/fetch-one-user {:email email})
        target-user-id (-> params :id read-string)]
    (if (or (= email (:email params))
            (nil? (user/fetch-one-user {:email (:email params)})))
      (let [updated-user (user/update-user! 
                           target-user-id 
                           (assoc params :id target-user-id))]
        {:status 200
         :session (assoc session :email (:email params))
         :body updated-user})
      (responses/conflict "User with that e-mail already exists"))))
