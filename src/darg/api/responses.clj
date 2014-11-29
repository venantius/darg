(ns darg.api.responses)

(defn ok
  [body]
  {:status 200
   :body body})

(defn bad-request
  [message]
  {:status 400
   :body {:message message}})

(defn unauthorized
  [message]
  {:status 401
   :body {:message message}})

(defn not-found
  [message]
  {:status 404
   :body {:message message}})

(defn method-not-allowed
  [message]
  {:status 405
   :body {:message message}})

(defn conflict
  [message]
  {:status 409
   :body {:message message}})
