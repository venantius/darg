(ns darg.db
  (:use darg.logging)
  (:require [darg.util :as util]
            [environ.core :refer [env]]
            [korma.db :as korma]))

(defn get-dburi
  []
  (env :database-url))

(def database-map
  (assoc (util/parse-url (get-dburi))
         :subprotocol "postgresql"
         :subname (util/build-db-subname (get-dburi))))

(korma/defdb korma-db (korma/postgres database-map))
