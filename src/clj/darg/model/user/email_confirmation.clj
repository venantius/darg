(ns darg.model.user.email-confirmation
  (:require [darg.db.entities :as db]
            [darg.email.template :as template]
            [darg.model :refer [defmodel]]
            [darg.model.email :refer [from-darg]]
            [darg.services.mailgun :as mailgun]
            [darg.util.token :as token]
            [korma.core :refer :all]))

(defmodel db/user-email-confirmation {})

(defn confirmation-link
  "Build a confirmation link from a token"
  [uec]
  (str "http://darg.io/settings/profile?confirmation_token=" (:token uec)))

(defn send-email-confirmation
  "Send an email to a new user asking them to confirm their e-mail address."
  [user uec]
  (let [link (confirmation-link uec)
        from (from-darg)
        to (:email user)
        subject "Welcome to Darg.io!"
        content (template/render-confirmation-email user link)]
    (mailgun/send-message {:from from
                           :to to
                           :subject subject
                           :html content})))

(defn create-and-send-email-confirmation
  "Create and then send an email confirmation message."
  [user]
  (let [conf (create-user-email-confirmation! {:user_id (:id user)})]
    (send-email-confirmation user conf)))
