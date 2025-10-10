#!/bin/bash

set -e  # Прерывать выполнение при ошибке любой команды

echo "Stopping team-selection container..."
docker compose stop team-selection
docker rm team-selection || echo "Container team-selection does not exist or already removed"
docker image rm team-selection-team-selection || echo "team-selection-team-selection image does not exist or already removed"
echo "Building new image..."
docker compose build --no-cache team-selection
echo "Starting container..."
docker compose up -d team-selection

echo "Script completed successfully"