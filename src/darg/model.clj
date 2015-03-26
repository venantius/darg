(ns darg.model
  (:require [clojure.tools.logging :as log]
            [korma.core :refer :all]))

(defn- create!
  [entity]
  (fn [params] 
    (insert entity (values params))))

(defn- fetch
  [entity]
  (fn [params]
    (select entity (where params))))

(defn- fetch-one
  [entity]
  (fn [params]
    (first (select entity (where params)))))



(defn intern-fns
  [entity]
  (let [n (:name entity)]
    (intern *ns* (symbol (str "create-" n "!")) (create! entity))
    (intern *ns* (symbol (str "fetch-" n)) (fetch entity))
    (intern *ns* (symbol (str "fetch-one-" n)) (fetch-one entity))))

(defmacro defmodel
  "Define basic database methods:
   
   create-X!
   fetch-X
   fetch-one-X
   update-X!
   delete-X!"
  [entity]
  `(intern-fns ~entity))
