(ns darg.controller.oauth.github
  (:require [clojure.tools.logging :as log]
            [darg.model.github.access-token :as access-token]
            [darg.oauth.github :as oauth]
            [darg.util.token :as token]))

(defn redirect
  "This handler is called to redirect the user to the GitHub OAuth login and
   authentication page."
  [{:keys [user params]}]
  (let [response (oauth/github-redirect-url (token/generate-token))]
    (log/warn response)
    response))

(defn callback
  "This handler is called after a user is redirected back to Darg from 
   https://github.com/login/oauth/authorize. 
   
   It takes the github request as an input, parses out the access code from 
   the params, and then makes a call to 
   https://github.com/login/oauth/access_token to generate an OAuth token."
  [{:keys [user params] :as request}]
  (let [{:keys [code state]} params
        at (oauth/access-token-exchange code)
        access-token (access-token/map->github-access-token
                       {:darg_user_id (:id user)
                        :token (:access_token at)
                        :scope (:scope at)})
        maybe-existing-token (access-token/fetch-one-github-access-token
                               {:darg_user_id (:id user)})]
    (if maybe-existing-token
      (access-token/update-github-access-token!
        (:id maybe-existing-token)
        access-token)
      (access-token/create-github-access-token! access-token))
    (log/warn request)
    (log/warn access-token)
    {:status 200
     :body "okay!"}))
