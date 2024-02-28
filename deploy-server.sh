#!/bin/sh
# GitHub에 저장하기 위한 용도, 스크립트는 서버에 따로 올려둔다.

### Check Argument
if [ $# -ne 4 ]; then
	echo 'Check arguments'
	exit -1
fi

URL=alcohol-friday-docker-image.kr.ncr.ntruss.com
IMAGE_NAME="alcohol-friday"

docker login -u $1 -p $2 $URL
docker pull ngnix
docker pull $URL/$IMAGE_NAME:$3
docker tag $URL/$IMAGE_NAME:$3 $IMAGE_NAME:$4

docker compose down
docker compose -p $IMAGE_NAME up -d
docker compose ps

docker image prune -a -f
