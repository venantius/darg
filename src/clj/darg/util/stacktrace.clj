(ns darg.util.stacktrace
  (:require [clojure.repl :refer [pst]]))

(defmacro with-err-str
  "Evaluates exprs in a context in which *err* is bound to a fresh
  StringWriter.  Returns the string created by any nested printing
  calls."
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn print-stacktrace
  [e]
  (let [s (with-err-str (pst e))]
    (println s)))
