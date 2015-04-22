(ns darg.controller.team.invitation
  "API endpoints for a team invitation"
  (:require [clojure.tools.logging :as log]))

(defn fetch-one
  [params]
  (log/warn params)
  {:status 200
   :body "okay!"})
