(ns darg.migrations.initial
  (:require [clojure.java.jdbc :as sql]))

(sql/create-table-ddl
  :users [:email :text
          :
          ])
