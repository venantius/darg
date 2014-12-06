all: init

init:
	createdb darg
	createdb darg_test

rebuild:
	dropdb darg
	dropdb darg_test
	make init
