(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
            [darg.db-util :as dbutil]
            [darg.model.dargs :as dargs]
            [darg.model.tasks :as tasks]
            [darg.model.teams :as teams]
            [darg.model.users :as users]
            [darg.services.stormpath :as stormpath]
            [korma.core :refer :all]
            [pandect.algo.md5 :refer :all]
            [ring.middleware.session.cookie :as cookie-store]
            [ring.middleware.session.store :as session-store]
            [slingshot.slingshot :refer [try+]]))

;; Authentication

(defn login
  "/v1/api/login

  Authentication endpoint. Routes input parameters to Stormpath for
  authentication; if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [request-map]
  (let [email (-> request-map :params :email)
        password (-> request-map :params :password)]
    (try+
      (stormpath/authenticate email password)
      (logging/info "Successfully authenticated with email" email)
      (let [id (:id (first (users/get-user {:email email})))]
        {:body "Successfully authenticated"
         :cookies {"logged-in" {:value true :path "/"}}
         :session {:authenticated true :id id :email email}
         :status 200})
      ;; Stormpath will return a 400 status code on failed auth
      (catch [:status 400] response
        (logging/info "Failed to authenticate with email " email)
        {:body "Failed to authenticate"
         :session {:authenticated false}
         :status 401}))))

(defn logout
  "/api/v1/logout

  The other half of the authentication endpoint pair. This one clears
  your session cookie and your plaintext cookie, logging you out both
  in practice and appearance"
  [request-map]
  {:body ""
   :status 200
   :session nil
   :cookies {"logged-in" {:value false :max-age 0 :path "/"}}})

(defn signup
  "/api/v1/signup

  Signs a user up. This creates an account in Stormpath, creates a user
  record in our database, and authenticates the user."
  [request-map]
  (let [request (-> request-map :params)]
    (try+
      (stormpath/create-account request)
      (logging/info "Successfully created account" (:email request))
      (let [user (users/create-user-from-signup-form request)]
        {:body "Account successfully created"
         :cookies {"logged-in" {:value true :path "/"}}
         :session {:authenticated true :email (:email request) :id (:id user)}
         :status 200})
      (catch [:status 400] response
        (logging/info "Failed to create Stormpath account with response" response)
        {:body "Failed to create account"
         :status 400})
      (catch [:status 409] response
        (logging/info "Account already exists")
        {:body "Account already exists"
         :status 409}))))

;; utils

(defn gravatar
  "Get a given user's gravatar image URL"
  [request-map]
  (let [email (-> request-map :session :email)]
    (if email
      {:body (clojure.string/join "" ["http://www.gravatar.com/avatar/"
                                      (md5 email)
                                      "?s=40"])
       :status 200}
      {:body "http://www.gravatar.com/avatar/?s=40"
       :status 200})))

;; /v1/darg

(defn get-darg
  [request-map]
  (logging/info request-map)
  (let [id (-> request-map :session :id)]
    {:body {:dargs (dargs/timeline id)}
     :status 200}))

(defn post-darg
  [request-map]
  (let [task-list (-> request-map :params :darg)
        user-id (-> request-map :session :id)
        team-id (-> request-map :params :team-id)
        date (-> request-map
               :params
               :date
               dbutil/sql-date-from-subject)
        metadata {:users_id user-id
                  :teams_id team-id
                  :date date}]
    (if (users/user-in-team? user-id team-id)
      (do (tasks/create-task-list task-list metadata)
        {:body "Tasks Created Successfully"
         :status 200})
      {:body "User is not a registered member of this team"
       :status 403})))

(defn darg
  "Takes a request, identifies the request method, and routes to the appropriate function.

  GET /api/v1/darg/
  Returns a user's darg. Expects the following
  :email - taken from session cookie

  POST /api/v1/darg/
  Adds dargs for the user. Expects the following:
  :email - taken from session cookie
  :team-id - specified by user in the body of the request, takes only one team and applies to the full darg
  :date - specified by user in the body of the request, takes only one date and applies to the full darg
  :darg-list - specified by user in the body of the request, expects an array of task strings

  DELETE /api/v1/darg/
  Deletes items from a user's darg. Will only delete tasks related to the user set in the session cookie.
  :email - taken from session cookie
  :task-ids - passed as an array in the body of the request."
  [request-map]
  (let [request-method (-> request-map :request-method)
        email (-> request-map :session :email)
        id (-> request-map :session :id)
        authenticated (-> request-map :session :authenticated)]
    (if (not (and id email authenticated))
      {:body "User not authenticated"
       :cookies {"logged-in" {:value false :max-age 0 :path"/"}}
       :status 403}
      (cond
        (= request-method :get) (get-darg request-map)
        (= request-method :post) (post-darg request-map)
        :else {:body "Method not allowed"
               :status 405}))))

;; v1/users

(defn get-user-profile
  "Allows a user to view the user profile of someone else on their team.
  Profile returns the user's name, email address, and admin status"
  [request-map]
  (let [requestor-id (-> request-map :session :id)
        target-id (-> request-map :params :user-id read-string)
        email (-> request-map :session :email)
        authenticated (-> request-map :session :authenticated)]
  (if (not (and requestor-id email authenticated))
      {:body "User not authenticated"
       :cookies {"logged-in" {:value false :max-age 0 :path"/"}}
       :status 403}
       (if (users/users-on-same-team? requestor-id target-id)
          {:body (users/get-user-by-id target-id)
           :status 200}
          {:body "You do not have access to this user"
           :status 403}))))

(defn get-user-darg
  "Allows a user to view the user profile of someone else on their team.
  Profile returns the user's name, email address, and admin status"
  [request-map]
  {:body request-map
   :status 200})

(defn get-user-teams
  "Allows a user to view the user profile of someone else on their team.
  Profile returns the user's name, email address, and admin status"
  [request-map]
  {:body request-map
   :status 200})
     
;; our logging problem is very similar to https://github.com/iphoting/heroku-buildpack-php-tyler/issues/17
(defn parse-forwarded-email
  "Parse an e-mail that has been forwarded by Mailgun"
  [body]
  (let [params (:params body)
        {:keys [recipient sender From subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map]} params]
    (logging/info "Mailgun Params: " params)
    (logging/info "Full Mailgun POST: " body)
    (str params)))

(defn parse-email
  "/api/v1/parse-email

  Recieves a darg email from a user, parses tasklist, and inserts the tasks into
  the database.

  Email mapping to task metadata is:
    - From -> uses email address to lookup :users_id
    - Recipient -> uses email address to lookup :teams_id
    - Subject -> parses out date in format 'MMM dd YYYY' and converts to sqldate for :date
    - Body -> Each newline in the body is parsed as a separate :task"
  [email]
  (let [task-list (-> email
                    (get :body-plain)
                    (str/split #"\n")
                    (->> (map str/trim)))
        email-metadata {:users_id (users/get-user-id {:email (:from email)})
                        :teams_id (teams/get-team-id {:email (:recipient email)})
                        :date (dbutil/sql-date-from-subject (:subject email))}]
    (tasks/create-task-list task-list email-metadata)))
