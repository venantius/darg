(ns darg.model.user
  (:use korma.core))

(defentity users
  (pk :id)
  (table :users)
  (database db)
  )
