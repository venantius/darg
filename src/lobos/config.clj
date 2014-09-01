(ns lobos.config
  (:require [darg.db]
            [lobos.connectivity :as lobos])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time]))

(def db darg.db/dargdb)

(defn init []
  (lobos/open-global db))
