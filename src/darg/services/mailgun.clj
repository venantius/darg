(ns darg.services.mailgun
  "Mailgun integration library"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [darg.settings :as settings]))

;; API endpoints
(def -base-url "https://api.mailgun.net/v2")
(def -post-message-endpoint
  (clojure.string/join [-base-url "/" settings/domain "/messages"]))

;; events API

;; Leaving this in here for future reference
(defn filter-events-for-test-messages
  "Filter our events for only messages from test@darg.io"
  [events-response]
  (let [events (:items events-response)]
    (filter #(= "test@darg.io"
                (-> % :message :headers :from))
            events)))

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
