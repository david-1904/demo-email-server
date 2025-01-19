#!/bin/bash

# Build the project
mvn clean package -DskipTests || { echo "Build failed! Exiting."; exit 1; }

# Stop all running containers
docker compose down --volumes --remove-orphans

# Start production services
echo "Starting production environment..."
docker compose up --build -d postgres flyway email-server-app

# Run tests
echo "Running tests..."
mvn test || { echo "Tests failed! Cleaning up..."; docker compose down; exit 1; }

# Jacoco Report
mvn jacoco:report

echo "Production environment is running."