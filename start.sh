#!/bin/bash

# Build the project
mvn clean package

# Stop and remove all running containers
docker compose down --volumes --remove-orphans

# Build and start the containers
docker compose up --build