(ns lobos.config
  (:require [darg.db]
            [lobos.connectivity :as lobos])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time]))

(println darg.db/dargdb)
(println (System/getenv "DATABASE_URL"))
(lobos/open-global darg.db/dargdb)
