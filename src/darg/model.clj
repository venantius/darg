(ns darg.model
  (:use korma.core))

(declare users teams tasks)

(defentity users
	(many-to-many teams :team_users)
	(has-many tasks)
	)

(defentity teams
	(many-to-many users :team_users)
	(has-many tasks)
	)

(defentity tasks
	(belongs-to users)
	(belongs-to teams)
	)

  
