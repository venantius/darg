# Darg

A Clojure clone of iDoneThis, designed to be better (and cheaper!) in all regards.

## Development Setup

### Postgres
1. `brew install postgres; initdb; createdb darg` ;; this probably doesn't work like this 
2. `createuser dev -s` ;; dev superuser for local development

### Leiningen
2  `brew install leiningen`
3. `git clone git@github.com:ursacorp/darg.git`
4. `lein deps`

### Grunt
1. `npm install`

## Development Flow
This is just my (@venantius) flow, but I think it's informative. I have many windows open at once, but the important windows are the following:
1. `lein run` - the app
2. `lein repl :connect` - a REPL, connected to the app
3. `lein with-profile test test-refresh :growl` - watch the backend file system, running tests when things change, and send me [growl](http://growl.info/) notifications. If you don't have growl installed then just leave off that keyword.
4. `grunt watch` - watch the frontend less files, and recompile them into css when they change.

## Usage

`lein run` to start a server running on localhost:8080

## Deployment

We're deploying to Heroku. At the moment CircleCI is configured to automatically deploy to Heroku on a successful build.

App URL: `http://darg.herokuapp.com/`

Git URL: `git@heroku.com:darg.git`

Do the following to set up the app for local development: `git remote add heroku git@heroku.com:darg.git`

## License

Copyright © 2014 UrsaCorp. All rights reserved.
