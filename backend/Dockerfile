# Stage 1: Build
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Hanya copy file config dulu agar download library bisa di-cache oleh Docker
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

# Baru copy source code dan build
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 7860

# Environment variables untuk konfigurasi aplikasi
ENV SERVER_PORT=7860
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dompetku
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=password
ENV JWT_SECRET=your-secret-key-here

# Optimasi RAM saat menjalankan Java di server Hugging Face (16GB RAM available)
# Force IPv4 untuk avoid DNS resolution issue
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-Xmx1g", "-Xss512k", "-jar", "app.jar"]
