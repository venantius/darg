(ns darg.controller.oauth.github
  (:require [clojure.tools.logging :as log]
            [darg.oauth.github :as oauth]))

(defn callback
  "This handler is called after a user is redirected back to Darg from 
   https://github.com/login/oauth/authorize. 
   
   It takes the github request as an input, parses out the access code from 
   the params, and then makes a call to 
   https://github.com/login/oauth/access_token to generate an OAuth token."
  [{:keys [user params] :as request}]
  (let [{:keys [code state]} params
        access-token (oauth/access-token-exchange code)]
    (log/warn request)
    (log/warn access-token)
    {:status 200
     :body "okay!"}))
