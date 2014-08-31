(ns darg.db
  (:require [clojurewerkz.urly.core :as urly])
  (:use korma.db
        clj-bonecp-url.core))

(def dburi (or (System/getenv "DATABASE_URL")
               "postgres://user:pass@localhost:5432/darg"))

(def datasource
  (datasource-from-url dburi))

(defn build-subname
  "I hate everything"
  [dburi]
  (let [uri-map (urly/as-map dburi)
        host (:host uri-map)
        port (:port uri-map)
        path (:path uri-map)]
    (clojure.string/join ["//" host ":" port path])))

;; This is used for Lobos only.
(def dargdb
  (assoc (parse-url dburi)
         :subprotocol "postgresql"
         :subname (build-subname dburi)))

(when (nil? @korma.db/_default)
  (korma.db/default-connection {:pool {:datasource datasource}}))
