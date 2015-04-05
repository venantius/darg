(ns darg.controller.auth
  (:require [darg.api.responses :refer [bad-request ok]]
            [darg.model.user :as user]))

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
      (let [id (:id (user/fetch-one-user {:email email}))]
        {:body "Successfully authenticated"
         :cookies {"logged-in" {:value true :path "/"}
                   "id" {:value id :path "/"}}
         :session {:authenticated true :id id :email email}
         :status 200}))))

(defn logout
  "/api/v1/logout

  Method: GET

  The other half of the authentication endpoint pair. This one clears
  your session cookie and your plaintext cookie, logging you out both
  in practice and appearance"
  [request-map]
  {:body ""
   :status 200
   :session nil
   :cookies {"logged-in" {:value false :max-age 0 :path "/"}
             "id" {:value "" :max-age 0 :path "/"}}})

(defn password-reset
  "/api/v1/password_reset

  Method: POST

  Initiates the password reset workflow. This will send an e-mail to the user
  with a special link that they can use to reset their password. The link will
  only remain valid for a finite amount of time."
  [request-map]
  (let [email (-> request-map :params :email clojure.string/lower-case)]
    (try
      (user/send-password-reset-email email)
      (ok "Success!")
      (catch Exception e
        (bad-request "Password reset failed.")))))
