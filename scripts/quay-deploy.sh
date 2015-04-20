#!/bin/bash -ex

lein uberjar

sed "s#<QUAY_AUTH>#$QUAY_AUTH#" < .dockercfg.template > $HOME/.dockercfg
docker build -t quay.io/venantius/darg .
docker push quay.io/venantius/darg
