# Use official Maven image (includes JDK and Maven)
FROM maven:3.9-eclipse-temurin-20-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Build the Java application
RUN mvn clean install

# Use a clean base image (Java only)
FROM openjdk:22-jdk-slim

WORKDIR /app

# Copy the Java build artifacts from the build stage
COPY --from=build /app/target /app/target

# Expose the port your application will run on
EXPOSE 8080

# Start the Java application (adjust the path to your JAR file)
CMD ["java", "-jar", "/app/target/your-app.jar"]
