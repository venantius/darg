(ns darg.model
  (:require [clojure.tools.logging :as log]
            [korma.core :refer :all]
            [schema.coerce :as coerce]))

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

(defn convert-to-keyword
  [k]
  (if (= (type k) schema.core.OptionalKey)
    (first (vals k))
    k))

(defn- schema-keys
  "Grab the keys for this schema. Convert optional keys to ordinary keywords."
  [schema]
  (map convert-to-keyword (keys schema)))

(defn coerce
  [schema]
  (fn [data]
    (let [schema-keys (schema-keys schema)
          data (select-keys data schema-keys)]
      ((coerce/coercer 
         schema 
         coerce/string-coercion-matcher)
       data))))

(defn intern-fns
  [entity schema]
  (let [n (:name entity)]
    (intern *ns* (symbol (str "create-" n "!")) (create! entity))
    (intern *ns* (symbol (str "fetch-" n)) (fetch entity))
    (intern *ns* (symbol (str "fetch-one-" n)) (fetch-one entity))
    (intern *ns* (symbol (str "update-" n "!")) (update! entity))
    (intern *ns* (symbol (str "delete-" n "!")) (delete! entity))
    (intern *ns* (symbol (str "map->" n)) (coerce schema))))

(defmacro defmodel
  "Define basic database methods for the target entity. Also does
   schema coersion.
   
    * create-X!
    * fetch-X
    * fetch-one-X
    * update-X!
    * delete-X!"
  [entity schema]
  `(intern-fns ~entity ~schema))


