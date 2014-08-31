(ns darg.model
	(:use korma.core))

(declare teams tasks users)

(defentity tasks
	(belongs-to teams)
	(belongs-to users)
	)

(defentity teams
	(has-many tasks)
	(many-to-many users :team_users)
	)

(defentity users
	(many-to-many teams :team_users)
	(has-many tasks)
	)
