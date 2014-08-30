(ns darg.services.mailgun
  "Mailgun integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]))

;; API endpoints
(def -base-url "https://api.mailgun.net/v2")
(def -get-events-endpoint
  (clojure.string/join [-base-url "/" settings/domain "/events"]))
(def -post-message-endpoint
  (clojure.string/join [-base-url "/" settings/domain "/messages"]))
(def -get-message-endpoint
  (clojure.string/join [-base-url "/domains/" settings/domain "/messages"]))
(def -delete-message-endpoint
  (clojure.string/join [-base-url "/domains/" settings/domain "/messages/"]))

;; events API

(defn get-events
  "Get all events"
  []
  (let [api-key (:api-key settings/mailgun-credentials)]
    (-> (client/get -get-events-endpoint {:basic-auth ["api" api-key]})
        :body
        (json/parse-string true))))

;; messages API

(defn send-message
  "Send an e-mail"
  [{:keys [from to subject text html]}]
  (let [form-params {:from from
                     :to to
                     :subject subject
                     :text text
                     :html html}
        api-key (:api-key settings/mailgun-credentials)]
    (-> (client/post -post-message-endpoint {:basic-auth ["api" api-key]
                                             :form-params form-params})
        :body
        (json/parse-string true))))

(defn get-message
  "Given a storage key, retrieve a message"
  [storage-key]
  (let [api-key (:api-key settings/mailgun-credentials)
        endpoint (clojure.string/join [-get-message-endpoint "/" storage-key])]
    (-> (client/get endpoint {:basic-auth ["api" api-key]})
        :body
        (json/parse-string true))))

(defn delete-message
  "Given a storage key, delete a message"
  [storage-key]
  (let [api-key (:api-key settings/mailgun-credentials)
        endpoint (clojure.string/join [-delete-message-endpoint "/" storage-key])]
    (-> (client/delete endpoint {:basic-auth ["api" api-key]})
        :body
        (json/parse-string true))))
