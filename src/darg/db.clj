(ns darg.db
  (:require [uri.core :as uri])
  (:use korma.db
        clj-bonecp-url.core))

(def dburi (or (System/getenv "DATABASE_URL")
               "postgres://dev@localhost:5432/darg"))

(def datasource
  (datasource-from-url dburi))

(defn build-subname
  "I hate everything"
  [dburi]
  (let [uri-map (uri/uri->map (uri/make dburi))
        host (if (= "127.0.0.1" (:host uri-map))
                    "localhost"
                    (:host uri-map))
        port (:port uri-map)
        path (:path uri-map)]
    (clojure.string/join ["//" host ":" port path])))

;; This is used for Lobos only.
(def dargdb
  (let [parsed-uri (parse-url dburi)]
    (assoc parsed-uri
           :subprotocol "postgresql"
           :subname (build-subname dburi)
           :user (:username parsed-uri))))

(when (nil? @korma.db/_default)
  (korma.db/default-connection {:pool {:datasource datasource}}))
