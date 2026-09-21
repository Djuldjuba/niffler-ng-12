#!/bin/bash
source ./docker.properties

export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)
export BROWSER="${1:-chrome}"

echo "### Quick start (без пересборки) ###"
echo "### Browser: $BROWSER ###"

docker compose up -d --no-build

echo "### Контейнеры запущены ###"
docker ps -a