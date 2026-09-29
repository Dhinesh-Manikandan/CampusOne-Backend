# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache dependencies by copying pom.xml first
COPY pom.xml .

# Download dependencies in offline mode to optimize build caching
RUN mvn dependency:go-offline -B || true

# Copy source files
COPY src ./src

# Build the application and run automated tests
RUN mvn clean package -DskipTests=false

# Stage 2: Lightweight production runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root group and user for secure container execution
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled JAR artifact from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Set file ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose Spring Boot default application port
EXPOSE 8080

# Configure JVM container resource flags
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Application entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
