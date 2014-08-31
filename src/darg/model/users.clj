(ns darg.model.users
  (:use korma.core))

(declare users)
(defentity users
  (pk :id)
  (table :users)
  )
