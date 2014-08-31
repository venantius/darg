(ns darg.db
  (:use korma.db
        clj-bonecp-url.core))

(def dburi (or (System/getenv "DATABASE_URL")
               "postgres://user:pass@localhost:5432/darg"))

(def datasource
  (datasource-from-url dburi))

;; This is used for Lobos only.
(def dargdb
  (println dburi)
  (assoc (parse-url dburi)
         :subprotocol "postgresql"
         :subname "darg"))

(when (nil? @korma.db/_default)
  (korma.db/default-connection {:pool {:datasource datasource}}))
