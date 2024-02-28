#!/bin/sh

if [ $# -ne 4 ]; then
        echo 'Check arguments'
        exit -1
fi

URL="alcohol-friday-docker-image.kr.ncr.ntruss.com"
IMAGE_NAME="alcohol-friday"

docker pull nginx

docker login -u $1 -p $2 $URL
docker pull $URL/$IMAGE_NAME:$3
docker tag $URL/$IMAGE_NAME:$3 $IMAGE_NAME:$4

docker compose down

docker compose -p $IMAGE_NAME up -d

docker compose ps

docker image prune -a -f
