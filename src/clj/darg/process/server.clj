(ns darg.process.server
  "The Darg web server process"
  (:require [clojure.tools.logging :as logging]
            [compojure.handler :as handler]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [darg.middleware.authentication :as authn]
            [darg.routes :refer [routes]]
            [environ.core :as env]
            [org.httpkit.server :as server]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.session.cookie :as cookie]))

(def app
  (-> routes
      (authn/wrap-authentication
        authn/darg-auth-fn
        :whitelist authn/route-whitelist-fn)
      (handler/site
        {:session {:store (cookie/cookie-store {:key (env/env :session-key)})}})
      middleware/ignore-trailing-slash
      wrap-json-response))

(defn run-web-server
  "Run the Darg.io web server process."
  [& args]
  (init/configure)
  (let [port (Integer. (env/env :port))]
    (logging/info "Starting Darg server on port" port)
    (server/run-server app {:port port :join? false})))
