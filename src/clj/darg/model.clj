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

(defn- update!
  [entity]
  (fn [id params]
    (update entity (where {:id id}) (set-fields params))
    (first (select entity (where {:id id})))))

(defn- delete!
  [entity]
  (fn [params]
    (delete entity (where params))))

(defn intern-fns
  [entity]
  (let [n (:name entity)]
    (intern *ns* (symbol (str "create-" n "!")) (create! entity))
    (intern *ns* (symbol (str "fetch-" n)) (fetch entity))
    (intern *ns* (symbol (str "fetch-one-" n)) (fetch-one entity))
    (intern *ns* (symbol (str "update-" n "!")) (update! entity))
    (intern *ns* (symbol (str "delete-" n "!")) (delete! entity))))

(defmacro defmodel
  "Define basic database methods for the target entity.
   
    * create-X!
    * fetch-X
    * fetch-one-X
    * update-X!
    * delete-X!"
  [entity]
  `(intern-fns ~entity))
