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

## Usage

`lein run` to start a server running on localhost:8080

## Deployment

We're deploying to Heroku. At the moment CircleCI is configured to automatically deploy to Heroku on a successful build.

App URL: `http://darg.herokuapp.com/`

Git URL: `git@heroku.com:darg.git`

Do the following to set up the app for local development: `git remote add heroku git@heroku.com:darg.git`

## License

Copyright © 2014 UrsaCorp. All rights reserved.
