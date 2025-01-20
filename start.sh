#!/bin/bash

echo "Stopping and removing all Docker containers and volumes..."

# Stop and remove all containers
docker rm -f $(docker ps -aq) 2>/dev/null

echo "All containers and volumes have been removed."

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