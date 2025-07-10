#!/bin/zsh

# reset residual changes
git reset --hard

# Pull the latest code from the Git repository
git pull

# Navigate to the frontend app directory
cd apps/gebit-app/

# Install npm dependencies and build the project
npm install && npm run build

# Return to the root project directory
cd ../../

# Build the backend Java application, skipping tests
mvn clean install -DskipTests

# Copy the built JAR file to the deployment location
cp srv/target/gebit-exec.jar /opt/gebit/app.jar

# Restart the systemd service for the application
sudo systemctl restart gebit.app.service
