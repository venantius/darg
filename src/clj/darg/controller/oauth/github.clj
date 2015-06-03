(ns darg.controller.oauth.github
  (:require [clojure.tools.logging :as log]
            [darg.model.github.access-token :as access-token]
            [darg.model.github.oauth-state :as oauth-state]
            [darg.oauth.github :as oauth]
            [darg.util.token :as token]
            [ring.util.response :as response]))

(defn redirect
  "This handler is called to redirect the user to the GitHub OAuth login and
   authentication page."
  [{:keys [user params]}]
  (let [state (oauth-state/create-github-oauth-state!
               (oauth-state/map->github-oauth-state
                {:darg_team_id (:team_id params)
                 :darg_user_id (:id user)
                 :state (token/generate-token)}))]
    (oauth/github-redirect-url (:state state))))

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
        state (oauth-state/fetch-one-github-oauth-state {:state state})
        maybe-existing-token (access-token/fetch-one-github-access-token
                              {:darg_user_id (:id user)})]
    (if maybe-existing-token
      (access-token/update-github-access-token!
       (:id maybe-existing-token)
       access-token)
      (access-token/create-github-access-token! access-token))
    (assoc 
      (response/redirect 
        (str "/team/" (:darg_team_id state) "/services/github?"))
      :cookies {"github" {:value true :path "/"}})))
