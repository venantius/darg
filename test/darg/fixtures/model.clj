(ns darg.fixtures.model
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c :only [to-sql-date]]
            [darg.db-util :as db-util]
            [darg.model :refer :all]
            [darg.model.users :refer [encrypt-password]]
            [korma.core :refer :all]))

(def test-user-1
  {:email "savelago@gmail.com"
   :name "yawn"
   :active true
   :admin true
   :password (encrypt-password "butts")})

(def test-user-2
  {:email "domo@darg.io"
   :name "domodomo"
   :active true
   :admin false
   :password (encrypt-password "cigarettes")})

(def test-user-3
  {:email "arrigato@darg.io"
   :name "arrigato"
   :active true
   :admin false
   :password (encrypt-password "nihon")})

(def test-user-4
  {:email "test-user2@darg.io"
   :name "Finn the Human"
   :active true
   :admin false
   :password (encrypt-password "samurai")})

(def test-user-5
  {:email "test@darg.io"
   :name "LSP"
   :surname "Smith"
   :active true
   :admin false
   :password (encrypt-password "ohmyglob")})

(def test-user-6
  {:email "david@ursacorp.io"
   :name "David Jarvis"
   :active true
   :admin true
   :password (encrypt-password "bloodthirst")})

(def test-users
  [test-user-1
   test-user-2
   test-user-3
   test-user-4
   test-user-5
   test-user-6])

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
  {:date (c/to-sql-time (t/local-date 2012 2 16))
   :users_id 4
   :teams_id 1
   :task "Do a good deed everyday"})

(def test-task-2
  {:date (c/to-sql-time (t/local-date 2012 2 16))
   :users_id 2
   :teams_id 2
   :task "Destroy all humans"})

(def test-task-3
  {:date (c/to-sql-time (t/local-date 2012 5 17))
   :users_id 4
   :teams_id 1
   :task "Salute the shorts"})

(def test-task-4
 {:date (c/to-sql-time (t/local-date 2012 3 19))
  :users_id 2
  :teams_id 3
  :task "Once more into the breach"})

(def test-task-5
  {:date (c/to-sql-time (t/local-date 2012 5 17))
   :users_id 4
   :teams_id 1
   :task "Some folks call it a kaiser blade, me I call it a sling blade"})

(def test-task-6
  {:date (c/to-sql-time (t/local-date 2012 5 18))
   :users_id 4
   :teams_id 2
   :task "Got a banking charter!"})

(def test-tasks
  [test-task-1
   test-task-2
   test-task-3
   test-task-4
   test-task-5
   test-task-6
   ])

(def test-team-user-pair-1
  {:users_id 1
   :teams_id 1
   :admin true})

(def test-team-user-pair-2
  {:users_id 3
   :teams_id 1})

(def test-team-user-pair-3
  {:users_id 1
   :teams_id 2})

(def test-team-user-pair-4
 {:users_id 4
  :teams_id 2
  :admin true})

(def test-team-user-pair-5
  {:users_id 4
   :teams_id 1
   :admin true})

(def test-team-user-pair-6
  {:users_id 2
   :teams_id 3
   :admin false})

(def test-team-user-pair-7
  {:users_id 3
   :teams_id 3
   :admin false})

(def test-team-user-pairs
  [test-team-user-pair-1
   test-team-user-pair-2
   test-team-user-pair-3
   test-team-user-pair-4
   test-team-user-pair-5
   test-team-user-pair-6
   test-team-user-pair-7])

(def test-password-reset-token-1
  {:token "XBT6XI7WAHPX4NQDHBWGXPP2YCJSXS7Q"
   :users_id 1
   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))
   })

(def test-password-reset-token-2
  ;; already expired
  {:token "T3HLQG5QEPDF6K26Y2OQTFJGNOD2WYI7"
   :users_id 2
   :expires_at (c/to-sql-time (t/now))})

(def test-password-reset-tokens
  [test-password-reset-token-1
   test-password-reset-token-2])
