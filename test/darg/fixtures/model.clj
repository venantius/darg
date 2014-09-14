(ns darg.fixtures.model
  (:use [korma.core]
        [clj-time.coerce :as c :only [to-sql-date]]
        [darg.model])
  (:require [clj-time.core :as t]
            [darg.db-util :as db-util]))

(def test-user-1
  {:email "savelago@gmail.com"
   :first_name "yawn"
   :admin true })

(def test-user-2
  {:email "domo@darg.io"
   :first_name "domodomo"
   :admin false})

(def test-user-3
  {:email "arrigato@darg.io"
   :first_name "arrigato"
   :admin false})

(def test-users
  [test-user-1
   test-user-2
   test-user-3])

(def test-team-1
  {:name "darg"
   :email "test.api@darg.io"})

(def test-team-2
  {:name "Robtocorp"
   :email "rcorp@darg.io"})

(def test-team-3
  {:name "Jake n Cake"
   :email "jncake@darg.io"})

(def test-teams
  [test-team-1
   test-team-2
   test-team-3])

(def test-task-1
  {:date (c/to-sql-date (t/date-time 2012 2 16))
   :users_id 1
   :teams_id 1
   :task "Do a good deed everyday"})

(def test-task-2
  {:date (c/to-sql-date (t/date-time 2012 2 16))
   :users_id 2
   :teams_id 2
   :task "Destroy all humans"})

(def test-task-3
  {:date (c/to-sql-date (t/date-time 2012 5 17))
   :users_id 3
   :teams_id 1
   :task "Salute the shorts"})

(def test-task-4
 {:date (c/to-sql-date (t/date-time 2012 3 19))
  :users_id 2
  :teams_id 3
  :task "Once more into the breach"})

(def test-tasks
  [test-task-1
   test-task-2
   test-task-3
   test-task-4])

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
 {  :users_id 2
  :teams_id 2
  :admin true})

(def test-team-user-pairs
  [test-team-user-pair-1
   test-team-user-pair-2
   test-team-user-pair-3
   test-team-user-pair-4])
