# Stage 1: Build the application
FROM gradle:8.10-jdk17 AS build
WORKDIR /app
COPY . .
# Build the jar file without running tests to speed up deployment
RUN ./gradlew bootJar -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Standard port for Spring Boot
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
