(ns darg.fixtures.model
	(:require [clj-time.core :as t]
		   [darg.db-util :as db-util])
	(:use [korma.core]
	         [darg.model])
	)


(def users-test-1
	[  { :email "savelago@gmail.com"
	     :username "yawn"
	     :admin true } 
	   { :email "domo@darg.io"
	     :username "domodomo"
	     :admin false}
	   { :email "arrigato@darg.io"
	     :username "arrigato"
	     :admin false 
	   }]
)

(def team-test-1
	[ {  :name "darg"}
	  {  :name "Robtocorp"}
	  {  :name "Jake n Cake"} ]
)

(def task-test-1
	[{ :date (t/today) 
               :user-id 1
               :team-id 1
               :task "Do a good deed everyday"
             }
             { :date (t/today)
                :user-id 2
                :team-id 2
                :task "Destroy all humans"
            }
             ]
)

