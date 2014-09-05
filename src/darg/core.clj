(ns darg.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [darg.api.v1 :as api]
            [darg.init :as init]
            [darg.middleware :as middleware]
            [org.httpkit.server :as server]
            ))

;; Pay attention to trailing slashes - right now the only thing that should end in a
;; slash is the root.
(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>")
  (POST "/api/v1/email" x (api/parse-forwarded-email x)))

(def app (-> routes
             handler/site
             middleware/ignore-trailing-slash))

(defn -main []
  (init/configure)
  (server/run-server #'app {:port (Integer. (or (System/getenv "PORT") "8080"))
                            :join? false}))
