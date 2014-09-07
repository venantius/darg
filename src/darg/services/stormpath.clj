(ns darg.services.stormpath
  "Stormpath integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]))

;; IDs and stuff
(def -directory-id "16nGL4oWAhrMW7sSkVAmex")
(def -application-id "16n7gyE9SsuwZ52C0ZZ7Dn")

;; API endpoints
(def -base-url "https://api.stormpath.com/v1")
(def -directory-account-endpoint
  (clojure.string/join [-base-url "/directories/" -directory-id "/accounts"]))
(def -account-endpoint
  (clojure.string/join [-base-url "/accounts"]))

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

;; Account functions
(defn create-account
  "Create a new user account on Stormpath.

  required fields: email, password, givenName, surname,
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

(defn delete-account-by-email
  "Delete an account with a given e-mail address"
  [email]
  (let [{:keys [api-key secret-key]} settings/stormpath-credentials
        user-info (search-for-account-by-email email)
        user-address (-> user-info :body :items first :href)]
    (client/delete user-address {:basic-auth [api-key secret-key]})))
