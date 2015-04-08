(ns darg.model.email.template
  "Code and templates for e-mailing users"
  (:require [clojure.java.io :as io]))

(def daily-email
  (slurp (io/file (io/resource "email/templates/daily.html"))))
