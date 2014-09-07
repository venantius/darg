(ns darg.services.stormpath
  "Stormpath integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]
            [ring.util.codec :as codec]))

;; IDs and stuff
(def -directory-id "16nGL4oWAhrMW7sSkVAmex")
(def -application-id "16n7gyE9SsuwZ52C0ZZ7Dn")

;; API endpoints
(def -base-url "https://api.stormpath.com/v1")
(def -account-endpoint
  (clojure.string/join [-base-url "/accounts"]))
(def -application-endpoint
  (clojure.string/join [-base-url "/applications/" -application-id]))
(def -application-login-endpoint
  (clojure.string/join [-base-url "/applications/" -application-id "/loginAttempts"]))
(def -application-password-reset-endpoint
  (clojure.string/join [-base-url "/applications/"
                        -application-id "/passwordResetTokens"]))
(def -directory-account-endpoint
  (clojure.string/join [-base-url "/directories/" -directory-id "/accounts"]))

(defn user->account
  "Takes a Darg user and converts it into a Stormpath account"
  [user]
  (-> user
      (assoc :givenName (:first_name user) :surname (:last_name user))
      (dissoc :first_name :last_name)))

(defn account->user
  "Takes a Stormpath account and converts it to a Darg user"
  [account]
  (-> account
      (assoc :first_name (:givenName account) :last_name (:surname account))
      (dissoc :givenName :surname)))

(defn get-search-results
  "Parses a Stormpath search map to just return the results. If nothing was found,
  returns nil"
  [response]
  (-> response :body :items first))

(defn format-stormpath-response
  [response]
  "Stormpath responds with a JSON-formatted body. This function takes the entire
  response, and swaps the JSON-formatted body out with a parsed Clojure map of
  the same"
  (update-in response [:body] json/parse-string true))

(defn create-account
  "Create a new user account on Stormpath.

  required fields: email, password, givenName, surname,

  not implemented:
  optional fields: username, middleName, status, customData
  status is either 'ENABLED' or 'DISABLED'"
  [{:keys [email password givenName surname]}]
  (format-stormpath-response
    (let [{:keys [api-key secret-key]} settings/stormpath-credentials
          body (json/encode {:email email
                             :password password
                             :givenName givenName
                             :surname surname})]
      (client/post -directory-account-endpoint {:basic-auth [api-key secret-key]
                                                :content-type :json
                                                :accept :json
                                                :body body}))))

(defn search-for-account-by-email
  "Search for an account on Stormpath by their e-mail address.

  Note that the server response is status 200 even if the user doesn't exist;
  Use stormpath/get-search-results to check if the search actually returned
  an account"
  [email]
  (format-stormpath-response
    (let [{:keys [api-key secret-key]} settings/stormpath-credentials]
      (client/get -directory-account-endpoint {:basic-auth [api-key secret-key]
                                               :accept :json
                                               :query-params {:email email}}))))

(defn authenticate
  "Authenticate an account with Stormpath"
  [email password]
  (let [{:keys [api-key secret-key]} settings/stormpath-credentials
        value (-> (clojure.string/join [email ":" password])
                  .getBytes
                  codec/base64-encode)
        body (json/encode {:value value
                           :type "basic"})]
    (client/post -application-login-endpoint {:basic-auth [api-key secret-key]
                                              :body body
                                              :content-type :json})))

(defn delete-account-by-email
  "Delete an account with a given e-mail address."
  [email]
  (let [{:keys [api-key secret-key]} settings/stormpath-credentials
        user-info (search-for-account-by-email email)
        user-address (-> user-info :body :items first :href)]
    (client/delete user-address {:basic-auth [api-key secret-key]})))

(defn update-account-by-email
  "Given an e-mail, update an account"
  [email updated-fields-map]
  (format-stormpath-response
    (let [{:keys [api-key secret-key]} settings/stormpath-credentials
          user-info (search-for-account-by-email email)
          user-address (-> user-info :body :items first :href)
          body (json/encode updated-fields-map)]
      (client/post user-address {:basic-auth [api-key secret-key]
                                 :body body
                                 :accept :json
                                 :content-type :json}))))

(defn reset-account-password
  "Initiate the account password reset flow"
  [email]
  (let [{:keys [api-key secret-key]} settings/stormpath-credentials]
    (client/post
      -application-password-reset-endpoint
      {:basic-auth [api-key secret-key]
       :body (json/encode {:email email})
       :accept :json
       :content-type :json})))
