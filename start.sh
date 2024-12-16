#!/bin/bash

# Build the project
mvn clean package -DskipTests || { echo "Build failed! Exiting."; exit 1; }

# Stop all running containers
docker compose down --volumes --remove-orphans

# Start production services
echo "Starting production environment..."
docker compose up --build -d postgres flyway email-server-app

# Start test services
echo "Setting up test environment..."
docker compose up --build -d postgres_test flyway_test

# Run tests
echo "Running tests..."
SPRING_PROFILES_ACTIVE=test \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/email_server_test \
SPRING_DATASOURCE_USERNAME=admin \
SPRING_DATASOURCE_PASSWORD=secret \
mvn test || { echo "Tests failed! Cleaning up..."; docker compose down; exit 1; }

# Stop test services
echo "Stopping test environment..."
docker compose stop postgres_test flyway_test

echo "Production environment is still running."