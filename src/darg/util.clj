(ns darg.util
  (:import java.net.URI)
  (:require [clojure.string :as str]))

(def default-subproto-map {"postgres" "postgresql"})
(def default-classname-map
  {"postgres" "org.postgresql.Driver"
   "mysql" "com.mysql.jdbc.Driver"
   "sqlite" "org.sqlite.JDBC"
   "h2" "org.h2.Driver"
   "oracle:thin" "oracle.jdbc.driver.OracleDriver"})

(defn parse-url
  "Given a String or a URI instance, and optional subproto-map and
  classname-map for conversion return a map of args suitable for use
  with `make-datasouce`."
  ([url subproto-map classname-map]
     {:pre [(map? subproto-map) (map? classname-map)]}
     (cond
      ;; URI
      (instance? URI url)
      (let [host (.getHost ^URI url)
            port (let [p (.getPort ^URI url)]
                   (and (pos? p) p))
            path (.getPath ^URI url)
            query (.getRawQuery ^URI url)
            scheme  (.getScheme ^URI url)
            adapter (subproto-map scheme scheme)
            classname (classname-map scheme)]
        (merge {:classname classname
                :adapter  (keyword adapter)
                :jdbc-url (str "jdbc:" adapter "://" host
                               (when port ":") (or port "") path
                               (when query "?") (or query ""))}
               (if-let [user-info (.getUserInfo ^URI url)]
                 (let [[un pw] (str/split user-info #":")]
                   {:username un
                    :password pw}))))
      ;; String
      (string? url)
      (parse-url (if (.startsWith ^String url "jdbc:")
                   (URI. (subs url 5))
                   (URI. url))
                 subproto-map classname-map)
      ;; default
      :otherwise
      (throw (IllegalArgumentException.
              (str "Expected `url` to be java.net.URI or String,"
                   " but found (" (pr-str (type url)) ") "
                   (pr-str url))))))
  ([url]
     (parse-url url default-subproto-map default-classname-map)))
