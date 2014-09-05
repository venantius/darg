(ns darg.db
  (:require [uri.core :as uri]
            [darg.util :as util]
            [environ.core :refer [env]]
            )
  (:use darg.logging
        korma.db))

(def dburi (env :database-url))

(def database-map
  (assoc (util/parse-url dburi)
         :subprotocol "postgresql"
         :subname (util/build-db-subname dburi)))

(defdb korma-db (postgres database-map))
