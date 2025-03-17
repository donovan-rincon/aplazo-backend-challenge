# Stage 1: Build the JAR file
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the project files (use .dockerignore to exclude unnecessary files)
COPY . .

# Build the project using Maven
RUN mvn clean package -DskipTests

# Stage 2: Run the Java application
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (adjust as needed)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
