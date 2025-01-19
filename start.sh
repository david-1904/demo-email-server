#!/bin/bash

echo "Stopping and removing all running containers..."
# Get IDs of all running Docker containers
running_containers=$(docker ps -q)

# Check if there are any running containers
if [ -n "$running_containers" ]; then
  # Stop all running containers using their IDs
  docker stop $running_containers
fi

# Get IDs of all existing Docker containers (both running and stopped)
all_containers=$(docker ps -aq)

# Check if there are any containers (running or stopped)
if [ -n "$all_containers" ]; then
  # Remove all containers using their IDs
  docker rm $all_containers
fi

# Remove all unused Docker networks
if ! docker network prune -f; then
  echo "Failed to prune Docker networks. Either there are no unused networks or Docker is not running."
fi

# Build the project
mvn clean package -DskipTests || { echo "Build failed! Exiting."; exit 1; }

# Start production services
echo "Starting production environment..."
docker compose up --build -d postgres flyway email-server-app

# Run tests
echo "Running tests..."
mvn test || { echo "Tests failed! Cleaning up..."; docker compose down; exit 1; }

# Jacoco Report
mvn jacoco:report

echo "Production environment is running."