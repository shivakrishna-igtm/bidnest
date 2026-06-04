# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "bidnest.BidnestApplicationTests"

# Run a single test method
./gradlew test --tests "bidnest.BidnestApplicationTests.contextLoads"

# Clean build
./gradlew clean build
```

## Stack

- **Java 21**, Spring Boot 3.5.14
- **Spring Data JPA** + **Flyway** for database migrations
- **Spring Web** (REST/MVC)
- **Lombok** for boilerplate reduction (`@Data`, `@Builder`, etc.)
- **H2** in development/test, **PostgreSQL** in production
- **JUnit 5** for tests

## Architecture

This project is in its early stages — only the application bootstrap exists. The intended architecture is a standard layered Spring Boot REST API:

- `src/main/java/bidnest/` — all application source code (package `bidnest`)
- `src/main/resources/application.yaml` — app config (currently minimal)
- Flyway migrations will live under `src/main/resources/db/migration/` as `V{n}__{description}.sql`

### Database

The app defaults to H2 at runtime. To connect to PostgreSQL, add datasource config to `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bidnest
    username: ...
    password: ...
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### Lombok

Lombok annotation processing is wired for both `main` and `test` source sets. Use `@Data`, `@Builder`, `@RequiredArgsConstructor`, etc. freely; no extra setup needed.