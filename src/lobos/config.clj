(ns lobos.config
  (:require [darg.db])
  (:refer-clojure
    :exclude [alter drop bigint boolean char double float time])
  (use (lobos connectivity)))

(open-global darg.db/dargdb)
