#!/bin/bash -ex

lein uberjar

sed "s#<QUAY_AUTH>#$QUAY_AUTH#" < .dockercfg.template > .dockercfg
docker build -t quay.io/venantius/darg .
docker push quay.io/mynamespace/myreponame
