(ns darg.email.template
  "Code and templates for e-mailing users"
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [selmer.parser :refer [render]]))

(def email-header
  (slurp (io/resource "email/templates/header.html")))

(def email-footer
  (slurp (io/resource "email/templates/footer.html")))

(defn construct-email 
  "Add a header and footer to the template."
  [template]
  (str email-header template email-footer))

(def daily-email
  (construct-email
    (slurp (io/resource "email/templates/daily.html"))))

(def team-invite
  (construct-email 
    (slurp (io/resource "email/templates/team_invite.html"))))

(def welcome-email
  (construct-email
    (slurp (io/resource "email/templates/welcome.html"))))

(def email-confirmation-email
  (construct-email
    (slurp (io/resource "email/templates/email_confirmation.html"))))

(def digest-email
  (construct-email
    (slurp (io/resource "email/templates/digest.html"))))

(defn render-team-invite
  [{:keys [name] :as team} invite]
  (render team-invite {:team_name name 
                       :token (:token invite)}))

(defn render-welcome-email
  [{:keys [name] :as user}]
  (render welcome-email {:name name}))

(defn render-confirmation-email
  [{:keys [name] :as user} link]
  (render email-confirmation-email {:name name :link link}))

(defn render-digest-email
  [darg]
  (render digest-email {:timeline darg}))
