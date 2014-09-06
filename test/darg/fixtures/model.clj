(ns darg.fixtures.model
	(:require [clj-time.core :as t]
		   [darg.db-util :as db-util])
	(:use [korma.core]
		[clj-time.coerce :as c :only [to-sql-date]]
	         [darg.model])
	)


(def users-test-1
	[  { :id 1
	     :email "savelago@gmail.com"
	     :username "yawn"
	     :admin true} 
	   { :id 2
	     :email "domo@darg.io"
	     :username "domodomo"
	     :admin false}
	   { :id 3
	     :email "arrigato@darg.io"
	     :username "arrigato"
	     :admin false}
	   { :id 4
	     :email "butts@darg.io"
	     :username "Mr. Butts"
	     :admin false }]
)

(def team-test-1
	[ {  :id 1
	     :name "darg"}
	  {  :id 2
	     :name "Robtocorp"}
	  {  :id 3
	     :name "Jake n Cake"} ]
)

(def task-test-1
	[{ :id 1
	   :date (c/to-sql-date (t/date-time 2012 2 16))
               :user-id 1
               :team-id 1
               :task "Do a good deed everyday"
             }
             {  :id 2
                :date (c/to-sql-date (t/date-time 2012 2 16))
                :user-id 2
                :team-id 2
                :task "Destroy all humans"
            }
             ]
)

