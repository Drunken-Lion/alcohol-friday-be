#!/bin/sh

### Check Argument
if [ $# -ne 5 ]; then
	echo 'Check arguments'
	exit -1
fi

IMAGE_NAME="alcohol-friday"

docker login -u $1 -p $2 $3

docker pull ngnix
docker pull $3/IMAGE_NAME:$4
docker tag $3/IMAGE_NAME:$4 IMAGE_NAME:$5

docker compose -p IMAGE_NAME -d

docker image prune -a -f