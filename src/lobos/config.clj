(ns lobos.config
  (:require [darg.db]
            [lobos.connectivity :as lobos])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time]))

(def db darg.db/database-map)

(defn init []
  (if (empty? @lobos.connectivity/global-connections)
    (lobos/open-global db)))
