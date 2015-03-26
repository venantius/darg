(ns darg.fixtures.model
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c :refer [to-sql-date]]
            [darg.db-util :as db-util]
            [darg.db.entities :refer :all]
            [darg.model.users :refer [encrypt-password]]
            [korma.core :refer :all]))

(def test-user-1
  {:email "savelago@gmail.com"
   :password (encrypt-password "butts")
   :name "John Lago"
   :timezone "UTC"
   :email_hour "1pm"
   :admin true
   :bot false
   :active true})

(def test-user-2
  {:email "domo@darg.io"
   :password (encrypt-password "cigarettes")
   :name "Domo the Robot"
   :timezone "UTC"
   :email_hour "5pm"
   :admin false
   :bot false
   :active true})

(def test-user-3
  {:email "arrigato@darg.io"
   :password (encrypt-password "nihon")
   :name "The Couch"
   :timezone "UTC"
   :email_hour "2pm"
   :admin false
   :bot false
   :active true})

(def test-user-4
  {:email "test-user2@darg.io"
   :password (encrypt-password "samurai")
   :name "Finn the Human"
   :timezone "America/Los_Angeles"
   :email_hour "7pm"
   :admin false
   :bot false
   :active true})

(def test-user-5
  {:email "test@darg.io"
   :password (encrypt-password "ohmyglob")
   :name "LSP"
   :timezone "America/New_York"
   :email_hour "8pm"
   :admin false
   :bot false
   :active true})

(def test-user-6
  {:email "david@ursacorp.io"
   :password (encrypt-password "bloodthirst")
   :name "David Jarvis"
   :timezone "UTC"
   :email_hour "4pm"
   :admin true
   :bot false
   :active true})

(def test-users
  [test-user-1
   test-user-2
   test-user-3
   test-user-4
   test-user-5
   test-user-6])

(def test-github-user-1
  {:gh_login "dargtester1"
   :id 10094188
   :gh_avatar_url "https://avatars.githubusercontent.com/u/10094188?v=3"
   :github_token_id nil
   :darg_user_id 4})

(def test-github-users
  [test-github-user-1])

(def test-team-1
  {:name "Darg"
   :email "test.api@darg.io"})

(def test-team-2
  {:name "Standard Treasury"
   :email "st@darg.io"})

(def test-team-3
  {:name "Jake n Cake"
   :email "jncake@darg.io"})

(def test-teams
  [test-team-1
   test-team-2
   test-team-3])

(def test-task-1
  {:date (c/to-sql-date (t/today))
   :user_id 4
   :team_id 1
   :task "Do a good deed everyday"})

(def test-task-2
  {:date (c/to-sql-date (t/today))
   :user_id 2
   :team_id 2
   :task "Destroy all humans"})

(def test-task-3
  {:date (c/to-sql-date (t/minus (t/today) (t/days 1)))
   :user_id 4
   :team_id 1
   :task "Salute the shorts"})

(def test-task-4
  {:date (c/to-sql-date (t/today))
   :user_id 2
   :team_id 3
   :task "Once more into the breach"})

(def test-task-5
  {:date (c/to-sql-date (t/minus (t/today) (t/days 1)))
   :user_id 4
   :team_id 1
   :task "Some folks call it a kaiser blade, me I call it a sling blade"})

(def test-task-6
  {:date (c/to-sql-date (t/today))
   :user_id 4
   :team_id 2
   :task "Defeated the ice king."})

(def test-task-7
  {:date (c/to-sql-date (t/today))
   :user_id 6
   :team_id 2
   :task "Got a banking charter!"})

(def test-tasks
  [test-task-1
   test-task-2
   test-task-3
   test-task-4
   test-task-5
   test-task-6
   test-task-7])

(def test-team-user-pair-1
  {:user_id 1
   :team_id 1
   :admin true})

(def test-team-user-pair-2
  {:user_id 3
   :team_id 1})

(def test-team-user-pair-3
  {:user_id 1
   :team_id 2})

(def test-team-user-pair-4
  {:user_id 4
   :team_id 2
   :admin true})

(def test-team-user-pair-5
  {:user_id 4
   :team_id 1
   :admin true})

(def test-team-user-pair-6
  {:user_id 2
   :team_id 3
   :admin false})

(def test-team-user-pair-7
  {:user_id 3
   :team_id 3
   :admin false})

(def test-team-user-pair-8
  {:user_id 6
   :team_id 1
   :admin true})

(def test-team-user-pair-9
  {:user_id 6
   :team_id 2
   :admin true})

(def test-team-user-pairs
  [test-team-user-pair-1
   test-team-user-pair-2
   test-team-user-pair-3
   test-team-user-pair-4
   test-team-user-pair-5
   test-team-user-pair-6
   test-team-user-pair-7
   test-team-user-pair-8
   test-team-user-pair-9])

(def test-password-reset-token-1
  {:token "XBT6XI7WAHPX4NQDHBWGXPP2YCJSXS7Q"
   :user_id 1
   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))})

(def test-password-reset-token-2
  ;; already expired
  {:token "T3HLQG5QEPDF6K26Y2OQTFJGNOD2WYI7"
   :user_id 2
   :expires_at (c/to-sql-time (t/now))})

(def test-password-reset-tokens
  [test-password-reset-token-1
   test-password-reset-token-2])
