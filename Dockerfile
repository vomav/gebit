# syntax=docker/dockerfile:1

### Stage 1: Node.js and Maven Build ###
FROM maven:3.9.4-eclipse-temurin-21 AS build-stage

# Environment variables
ENV NPM_CONFIG_PREFIX=/home/node/.npm
ENV PATH=$NPM_CONFIG_PREFIX/bin:$PATH

# Install Node.js and CDS CLI
RUN apt-get update && apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g @sap/cds-dk && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Install Node.js dependencies and build Node.js assets
RUN npm install && cds build

# Run Maven build
RUN mvn clean install

# Copy built artifacts for the final stage
RUN mv srv/target/gebit-exec.jar /app/app.jar

### Stage 2: Final Runtime Image ###
FROM eclipse-temurin:21-jre-jammy AS final

# Create a non-root user for security
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

USER appuser

# Set working directory
WORKDIR /app

# Copy built artifacts from the build stage
COPY --from=build-stage /app/app.jar .

# Expose the application's port
EXPOSE 80

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
