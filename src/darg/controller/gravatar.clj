(ns darg.controller.gravatar
  (:require [darg.api.responses :refer [ok]]
            [pandect.algo.md5 :refer [md5]]))

(defn gravatar
  "/api/v1/gravatar

  Supports: POST

  Return a given user's gravatar image URL."
  [request]
  (let [email (-> request :session :email)
        size (-> request :params :size)]
    (if email
      (ok
       (clojure.string/join "" ["http://www.gravatar.com/avatar/"
                                (md5 email)
                                "?s="
                                size]))
      (ok
       (format "http://www.gravatar.com/avatar/?s=%s" size)))))
