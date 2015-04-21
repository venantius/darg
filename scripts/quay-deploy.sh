#!/bin/bash -ex

lein uberjar

export NOW=$(date +%s)

sed "s#<QUAY_AUTH>#$QUAY_AUTH#" < .dockercfg.template > $HOME/.dockercfg
docker build -t quay.io/venantius/darg .
docker tag -f quay.io/venantius/darg:$NOW-${CIRCLE_SHA1:0:8} quay.io/venantius/darg:latest
docker push quay.io/venantius/darg:$NOW-${CIRCLE_SHA1:0:8}
docker push quay.io/venantius/darg:latest
