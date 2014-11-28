(ns darg.services.mailgun
  "Mailgun integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]
            [pandect.algo.sha256 :refer [sha256-hmac]]))

;; API endpoints
(def -base-url "https://api.mailgun.net/v2")
(def -post-message-endpoint
  (clojure.string/join [-base-url "/" settings/domain "/messages"]))

;; messages API
(defn send-message
  "Send an e-mail"
  [{:keys [from to subject text html]}]
  (let [form-params {:from from
                     :to to
                     :subject subject
                     :text text
                     :html html
                     :o:dkim "yes"}
        api-key (:api-key settings/mailgun-credentials)]
    (-> (client/post -post-message-endpoint {:basic-auth ["api" api-key]
                                             :form-params form-params})
        :body
        (json/parse-string true))))

(defn authenticate
  "Verify that this message was sent from Mailgun"
  [{:keys [timestamp token signature]}]
  (let [api-key (:api-key settings/mailgun-credentials)
        computed-signature (-> (clojure.string/join [timestamp token])
                               (sha256-hmac api-key))]
    (if (= computed-signature signature) true false)))
