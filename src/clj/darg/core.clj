(ns darg.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [darg.init :as init]
            [darg.process.email :as email]
            [darg.process.server :as server]))

(defn -main [& args]
  (let [{:keys [options arguments errrors summary]} (parse-opts args []
                                                                :in-order true)]
    (case (first arguments)
      "server" (server/run-web-server (rest arguments))
      (println "ERROR: `lein run` requires one of the following arguments: `server`"))))
