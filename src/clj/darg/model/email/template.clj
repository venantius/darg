(ns darg.model.email.template
  "Code and templates for e-mailing users"
  (:require [clojure.java.io :as io]
            [selmer.parser :refer [render]]))

(def email-header
  (slurp (io/file (io/resource "email/templates/header.html"))))

(def email-footer
  (slurp (io/file (io/resource "email/templates/footer.html"))))

(defn construct-email 
  "Add a header and footer to the template."
  [template]
  (str email-header template email-footer))

(def daily-email
  (construct-email 
    (slurp (io/file (io/resource "email/templates/daily.html")))))

(def team-invite
  (construct-email 
    (slurp (io/file (io/resource "email/templates/team_invite.html")))))

(defn render-team-invite
  [{:keys [name] :as team}]
  (render team-invite {:team_name name}))
