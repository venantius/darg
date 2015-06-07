(ns darg.controller.auth
  (:require [clojure.tools.logging :as log]
            [darg.api.responses :refer [bad-request ok]]
            [darg.cookies :refer [authed-cookies]]
            [darg.model.user :as user]
            [darg.model.password-reset-token :as token]))

(defn login
  "/api/v1/login

  Method: POST

  Authentication endpoint. if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [{:keys [params] :as request}]
  (let [{:keys [email password]} params
        email (clojure.string/lower-case email)]
    (cond
      (not (user/authenticate email password))
      {:body "Failed to authenticate"
       :session {:authenticated false}
       :status 401}
      :else
      (let [user (user/fetch-one-user {:email email})]
        (merge
          {:body "Successfully authenticated"
           :status 200}
          (authed-cookies user))))))

(defn logout
  "/api/v1/logout

  Method: GET

  The other half of the authentication endpoint pair. This one clears
  your session cookie and your plaintext cookie, logging you out both
  in practice and appearance"
  [request]
  {:body ""
   :status 200
   :session nil
   :cookies {"logged-in" {:value false :max-age 0 :path "/"}
             "id" {:value "" :max-age 0 :path "/"}
             "github" {:value false :max-age 0 :path "/"}}})

(defn password-reset
  "/api/v1/password_reset

  Method: POST

  Initiates the password reset workflow. This will send an e-mail to the user
  with a special link that they can use to reset their password. The link will
  only remain valid for a finite amount of time."
  [request]
  (let [email (-> request :params :email clojure.string/lower-case)
        user (user/fetch-one-user {:email email})]
    (cond
      (not user)
        (bad-request "User with that e-mail could not be found.")
      :else
      (do
        (user/send-password-reset-email user)
        (ok "Success!")))))

(defn set-new-password
  "/api/v1/new_password
   
   Method: POST
   
   Finalize the password reset workflow."
  [{:keys [params] :as request}]
  (log/info params)
  (let [{:keys [password confirm_password token]} params
        token (token/fetch-one-valid {:token token})]
    (cond
      (not= password confirm_password)
      (bad-request "Password fields do not match.")
      (nil? token)
      (bad-request "Invalid token.")
      :else
      (let [user (user/fetch-one-user {:id (:user_id token)})]
        (user/update-user! 
          (:id user)
                    {:password (user/encrypt-password password)})
        (merge
          {:status 200
           :body "Okay!"}
          (authed-cookies user))))))
