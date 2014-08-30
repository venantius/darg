(ns darg.model.user
  (:use korma.core))

(declare users)
(defentity users
  (pk :id)
  (table :users)
  (database db)
  )
