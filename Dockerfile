# Multi-stage build for Spring Boot 3 Java 21 Application

# Stage 1: Build JAR using Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy project files
COPY . .

# Grant execute permissions to maven wrapper & build application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal Java Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled JAR from build stage
COPY --from=build /app/target/rapidaid-1.0.0.jar app.jar

# Expose port (overridden dynamically by cloud provider via PORT env variable)
EXPOSE 8080

# Run Spring Boot Application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
