# syntax=docker/dockerfile:1

FROM maven:3.9.4-eclipse-temurin-21 as build-stage

ENV NPM_CONFIG_PREFIX=/home/node/.npm
ENV PATH=$NPM_CONFIG_PREFIX/bin:$PATH

# Install curl and Node.js
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    node --version && npm --version

RUN npm install -g @sap/cds-dk
    # Set working directory
WORKDIR /app

# Copy project files
COPY . .

# RUN npm --version  

# RUN node --version 

# RUN cd apps/gebit-app && \ 
#     npm install && \ 
#     npm run build

# Run Maven build

RUN npm install
RUN cds build
RUN mvn clean install

# Set working directory
# WORKDIR /app

# Copy built artifacts from build stage
COPY srv/target/gebit-exec.jar /app

# Command to run the application (modify as needed)
# CMD ["java", "-jar", "gebit-exec.jar"]
# ENTRYPOINT ["java","-Dspring.profiles.active=prod" "-jar","gebit-exec.jar"]