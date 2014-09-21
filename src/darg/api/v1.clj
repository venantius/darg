(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
            [darg.model.tasks :as tasks]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.db-util :as dbutil]
            [darg.services.stormpath :as stormpath]
            [korma.core :refer :all]
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
  (logging/info request-map)
  (let [email (-> request-map :params :email)
        password (-> request-map :params :password)]
    (try+
      (stormpath/authenticate email password)
      (logging/info "Successfully authenticated with email" email)
      {:body "Successfully authenticated"
       :cookies {"logged-in" {:value true :path "/"}}
       :session {:authenticated true :email email}
       :status 200}
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
  {:body (str request-map)
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
      (users/create-user-from-signup-form request)
      {:body "Account successfully created"
       :cookies {"logged-in" {:value true :path "/"}}
       :session {:authenticated true :email (:email request)}
       :status 200}
      (catch [:status 400] response
        (logging/info "Failed to create Stormpath account with response" response)
        {:body "Failed to create account"
         :status 401})
      (catch [:status 409] response
        (logging/info "Account already exists")
        {:body "Account already exists"
         :status 409}))))

(defn get-user-dargs
  "GET /api/v1/darg/

  Takes the email in the session cookie to return a user's darg

 API should eventually take the following queries:

  ?from='MMM dd yyy' - set a minimum date for the user's darg 
  ?to='MMM dd yyy' - set a maximum date for the user's darg
  ?limit=10 - reduce the number of results (for pagination)
  ?offset=10 - send sets of information
  ?teams={teamids} - filter set to a specific team or group of teams"

  [request-map]
  (let [email (-> request-map :session :email)
         authenticated (-> request-map :session :authenticated)]
    (if (and email authenticated)
      {:body (tasks/get-all-tasks-for-user-by-email email)
       :status 200}
      {:body "User not authenticated"
       :cookies {"logged-in" {:value false :max-age 0 :path"/"}}
       :session {:authenticated false}
       :status 403})))

(defn add-dargs-for-user
"POST /api/v1/darg/

Adds dargs for the user. Expects the following:
* email -> taken from session cookie
* team-id -> specified by user in the body of the request, takes only one team and applies to the full darg
* date -> specified by user in the body of the request, takes only one date and applies to the full darg
* darg-list -> specified by user in the body of the request, expects an array of task strings"

[request-map]
(let [task-list (-> request-map :params :darg)
       email (-> request-map :session :email)
       authenticated (-> request-map :session :authenticated)
       date (-> request-map 
                    :params 
                    :date
                    dbutil/sql-date-from-subject)
       team-name (-> request-map :params :team-name)
       metadata {:users_id (users/get-userid {:email email})
                        :teams_id (teams/get-teamid {:name team-name})
                        :date date}]
    (if (and email authenticated)
      (if (users/is-user-in-team (:users_id metadata) (:teams_id metadata))
        
        (do 
          (println "I am in the do loop")
          (tasks/create-task-list task-list metadata)
              {:body "Tasks Created Successfully" :status 200})

        {:body "User is not a registered member of this team"
         :status 403})

       {:body "User not authenticated"
       :cookies {"logged-in" {:value false :max-age 0 :path"/"}}
       :session {:authenticated false}
       :status 403})))




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
        email-metadata {:users_id (users/get-userid {:email (:from email)})
                        :teams_id (teams/get-teamid {:email (:recipient email)})
                        :date (dbutil/sql-date-from-subject (:subject email))}]
    (tasks/create-task-list task-list email-metadata)))
