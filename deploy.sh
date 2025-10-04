#!/bin/bash

set -e  # Прерывать выполнение при ошибке любой команды

docker compose stop team-selection
docker rm team-selection || echo "Контейнер team-selection не существует или уже удален"
docker image rm team-selection-team-selection || echo "Образ team-selection-team-selection не существует или уже удален"
docker compose build --no-cache team-selection
docker compose up -d team-selection

echo "Скрипт завершен успешно"