#!/bin/bash -ex

export APPLICATION_VERSION="$(date +%s)-${CIRCLE_SHA1:0:8}"

docker build -t $CIRCLE_PROJECT_REPONAME:$APPLICATION_VERSION

sed "s#<BUCKET>#$EB_BUCKET#;s#<TAG>#$DOCKER_TAG#;s#<PORT>#$DOCKER_PORT#" < ./Dockerrun.aws.json.template > Dockerrun.aws.json
