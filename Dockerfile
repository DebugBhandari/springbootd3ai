# Use official Maven image (includes JDK and Maven)
FROM maven:3.8.6-openjdk-22-slim AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Build the Java application
RUN mvn clean install

# Use Node.js base image for building the Node.js application
FROM node:20 AS node-builder

WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the app source code
COPY . .

# Build the Node.js app
RUN npm run build

# Use a clean base image (Java + Node.js)
FROM openjdk:22-jdk-slim

WORKDIR /app

# Copy both Java and Node.js build artifacts
COPY --from=build /app/target /app/target
COPY --from=node-builder /app /app

# Expose the port
EXPOSE 8080

# Start the Java application (adjust the path to your JAR file)
CMD ["java", "-jar", "/app/target/your-app.jar"]
