# CampusOne Backend - DevOps & CI/CD Documentation

This repository contains the Spring Boot REST backend for **CampusOne**, configured for containerized deployment with Docker, Docker Hub, and Jenkins CI/CD.

---

## 1. Project Specifications

* **Language**: Java 21 (LTS)
* **Framework**: Spring Boot 4.1.0 (with Spring Data JPA, Spring Security, JWT)
* **Build Tool**: Apache Maven 3.9+
* **Default Port**: `8080` (Configurable via `PORT` environment variable)
* **Output JAR**: `target/campusone-0.0.1-SNAPSHOT.jar`

---

## 2. Database Requirements

* **Engine**: PostgreSQL 15+ (Production/Container) / H2 In-Memory (Automated Tests)
* **Default Database**: `campusone`
* **Default Username**: `campusone_user`
* **Default Password**: `Campus@123`
* **Default Host & Port**: `localhost:5432`

> **Note on Testing**: The automated test suite (`CampusoneApplicationTests`) runs completely self-contained against an embedded H2 database (`jdbc:h2:mem:campusone_test`) defined in `src/test/resources/application.properties`. No external database is needed during `mvn test` or the Jenkins test stage.

To run PostgreSQL locally via Docker:
```bash
docker run -d \
  --name campusone-postgres \
  -e POSTGRES_DB=campusone \
  -e POSTGRES_USER=campusone_user \
  -e POSTGRES_PASSWORD=Campus@123 \
  -p 5432:5432 \
  postgres:16-alpine
```

---

## 3. Environment Variables Reference

All runtime and environment-specific settings are parameterized with fallback defaults matching local development:

| Variable Name | Description | Default / Fallback Value | Sensitive |
| :--- | :--- | :--- | :---: |
| `PORT` | HTTP port on which the application listens | `8080` | No |
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL connection | `jdbc:postgresql://localhost:5432/campusone` | No |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL database user | `campusone_user` | No |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL database password | `Campus@123` | **Yes** |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema management mode | `update` | No |
| `SPRING_JPA_SHOW_SQL` | Log SQL queries to console | `false` | No |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 32 bytes) | `change-me-change-me-change-me-change-me` | **Yes** |
| `JWT_ISSUER` | JWT token issuer claim | `campusone` | No |
| `JWT_ACCESS_TOKEN_TTL` | Access token time-to-live (ISO-8601 duration) | `PT15M` (15 minutes) | No |
| `JWT_REFRESH_TOKEN_TTL` | Refresh token time-to-live (ISO-8601 duration) | `P7D` (7 days) | No |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | `http://localhost:3000,http://localhost:5173` | No |
| `BOOTSTRAP_ADMIN_ENABLED` | Automatically create bootstrap admin account | `false` | No |
| `BOOTSTRAP_ADMIN_EMAIL` | Bootstrap admin email address | `admin@student.annauniv.edu` | No |
| `BOOTSTRAP_ADMIN_PASSWORD` | Bootstrap admin initial password | `Admin@12345` | **Yes** |

---

## 4. Local Development & Maven Commands

Execute using either Maven CLI or the included Maven Wrapper:

### Build & Package
```bash
# Windows
.\mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

### Run Automated Tests
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### Run Locally
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

---

## 5. Docker Guide

### Multi-Stage Build Architecture
The production `Dockerfile` uses a two-stage build:
1. **Stage 1 (Builder)**: Uses `maven:3.9.9-eclipse-temurin-21-alpine` to compile code, execute tests, and package the executable JAR.
2. **Stage 2 (Runtime)**: Uses `eclipse-temurin:21-jre-alpine` (~150MB), runs as non-root user `appuser:appgroup`, and executes the JAR without Maven, source code, or compilers in the image.

### Build Docker Image
```bash
docker build -t dhineshmanikandan2006/campusone-backend:latest .
```

### Run Backend Container (Standalone with Host PostgreSQL)
```bash
docker run -d \
  --name campusone-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/campusone" \
  -e SPRING_DATASOURCE_USERNAME="campusone_user" \
  -e SPRING_DATASOURCE_PASSWORD="Campus@123" \
  -e JWT_SECRET="your-secure-32-byte-secret-key-goes-here-123" \
  -e CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173" \
  dhineshmanikandan2006/campusone-backend:latest
```

### Run with Docker Network (Connected to Postgres Container)
```bash
# 1. Create bridge network
docker network create campusone-net

# 2. Run PostgreSQL
docker run -d \
  --name campusone-db \
  --network campusone-net \
  -e POSTGRES_DB=campusone \
  -e POSTGRES_USER=campusone_user \
  -e POSTGRES_PASSWORD=Campus@123 \
  -p 5432:5432 \
  postgres:16-alpine

# 3. Run Spring Boot Backend
docker run -d \
  --name campusone-backend \
  --network campusone-net \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://campusone-db:5432/campusone" \
  -e SPRING_DATASOURCE_USERNAME="campusone_user" \
  -e SPRING_DATASOURCE_PASSWORD="Campus@123" \
  -e JWT_SECRET="your-secure-32-byte-secret-key-goes-here-123" \
  -e CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173" \
  dhineshmanikandan2006/campusone-backend:latest
```

---

## 6. React Frontend Communication & CORS

The React frontend communicates with the backend via REST endpoints under `/api/**`.
* **CORS Bean**: Spring Security uses `CorsConfigurationSource` to authorize cross-origin requests.
* **Allowed Origins**: Configured via `CORS_ALLOWED_ORIGINS`.
* **Credentials**: Enabled (`allowCredentials = true`) using `allowedOriginPatterns` to support authorization headers.
* **Preflight**: HTTP `OPTIONS` requests are handled and cached with a 1-hour max-age.

When running the React frontend in Docker or on port 3000 / 5173:
```bash
# Example: Adding production React frontend origin
-e CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173,http://frontend.campusone.com"
```

---

## 7. Jenkins CI/CD Pipeline

The `Jenkinsfile` defines a 7-stage declarative pipeline designed for automated testing and container deployment:

```
Checkout ──▶ Maven Clean ──▶ Run Tests ──▶ Maven Package ──▶ Docker Build ──▶ Docker Login ──▶ Docker Push
```

### Pipeline Stages
1. **Checkout**: Pulls the Git repository and verifies working directories.
2. **Maven Clean**: Removes old build artifacts (`mvn clean`).
3. **Run Tests**: Executes the JUnit 5 test suite (`mvn test`). Fails pipeline if any test fails.
4. **Maven Package**: Packages the application into an executable JAR (`mvn package -DskipTests`).
5. **Docker Build**: Builds the Docker container tagging both `${BUILD_NUMBER}` and `latest`.
6. **Docker Login**: Authenticates securely to Docker Hub using Jenkins credentials without console leaks.
7. **Docker Push**: Pushes `${BUILD_NUMBER}` and `latest` tagged images to Docker Hub.

### Required Jenkins Credentials

Before triggering the pipeline, configure the following in Jenkins:

1. Navigate to: **Jenkins Dashboard** ➔ **Manage Jenkins** ➔ **Credentials** ➔ **System** ➔ **Global credentials** ➔ **Add Credentials**.
2. Fill in:
   * **Kind**: `Username with password`
   * **Scope**: `Global (Jenkins, nodes, items, all child items, etc)`
   * **Username**: `dhineshmanikandan2006`
   * **Password / Token**: `<Your Docker Hub Personal Access Token or Password>`
   * **ID**: `docker-hub-credentials` *(must match exactly)*
   * **Description**: `Docker Hub Credentials for CampusOne`
