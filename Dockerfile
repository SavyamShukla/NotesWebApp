# --- Stage 1: Build the application ---
# Use a Maven and Java 17 base image to build the project
FROM maven:3.8.5-openjdk-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to leverage Docker's layer caching
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping the tests
RUN mvn package -DskipTests


# --- Stage 2: Create the final, smaller image ---
# Use a minimal Java 17 runtime image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the built .jar file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on (Spring Boot's default)
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]