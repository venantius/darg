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
   :user_id 1
   :team_id 1
   :task "Do a good deed everyday"})

(def test-task-2
  {:date (c/to-sql-date (t/date-time 2012 2 16))
   :user_id 2
   :team_id 2
   :task "Destroy all humans"})

(def test-tasks
  [test-task-1
   test-task-2])

(def test-user-team-pair-1
  {:user_id 1
   :team_id 2})

(def test-user-team-pair-2
  {:user_id 3
   :team_id 2})

(def test-user-team-pairs
  [test-user-team-pair-1
   test-user-team-pair-2])
