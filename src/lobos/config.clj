(ns lobos.config
  (:use darg.logging)
  (:require [darg.db]
            [lobos.connectivity :as lobos])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time]))

(defn init []
  (lobos/open-global darg.db/dargdb))
