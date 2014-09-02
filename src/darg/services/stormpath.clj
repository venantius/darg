(ns darg.services.stormpath
  "Stormpath integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]))

;; IDs and stuff
(def -directory-id "16nGL4oWAhrMW7sSkVAmex")

;; API endpoints
(def -base-url "https://api.stormpath.com/v1")
(def -account-creation-endpoint
  (clojure.string/join [-base-url "/directories/" -directory-id "/accounts"]))

;; Account functions
(defn create-user-account
  "Create a new user account on Stormpath.

  required fields: email, password, givenName, surname,
  optional fields: username, middleName, status, customData
  status is either 'ENABLED' or 'DISABLED'"
  [{:keys [email password givenName surname]}]
  (let [{:keys [api-key secret-key]} settings/stormpath-credentials
        body (json/encode {:email email
                           :password password
                           :givenName givenName
                           :surname surname})]
    (client/post -account-creation-endpoint {:basic-auth [api-key secret-key]
                                             :content-type :json
                                             :accept :json
                                             :body body})))


;; to get info on an account we GET from the accounts endpoint:
;; https://api.stormpath.com/v1/accounts/:accountId
