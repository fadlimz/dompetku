# Stage 1: Build
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Hanya copy file config dulu agar download library bisa di-cache oleh Docker
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
RUN ./gradlew --no-daemon dependencies

# Baru copy source code dan build
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Optimasi RAM saat menjalankan Java di server kecil
ENTRYPOINT ["java", "-Xmx256m", "-Xss512k", "-jar", "app.jar"]
