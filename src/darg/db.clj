(ns darg.db
  (:use korma.db
        clj-bonecp-url.core))

(def datasource
  (datasource-from-url
    (or (System/getenv "DATABASE_URL")
        "postgres://user:pass@localhost:5432/darg")))

(when (nil? @korma.db/_default)
  (korma.db/default-connection {:pool {:datasource datasource}}))
