# Clean Auth API

Java 17 + Spring Boot 3 REST API demonstrating a clean architecture for authentication and basic client management.

## Overview
- **Clean Code / SOLID / DDD** layered structure
- JWT authentication (access + refresh tokens)
- Client CRUD endpoints
- Flyway migrations
- H2 for development/testing, PostgreSQL for production

## Running
```bash
./mvnw spring-boot:run
```

### Environment variables
- `JWT_SECRET` or `jwt.secret`
- `JWT_ACCESS_EXPIRATION_MINUTES` or `jwt.access-expiration-minutes`
- `JWT_REFRESH_EXPIRATION_DAYS` or `jwt.refresh-expiration-days`
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

## Sample requests
```
# Login
curl -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d '{"email":"admin@local","password":"admin123"}'

# Get current user
curl http://localhost:8080/api/v1/auth/me -H "Authorization: Bearer <ACCESS_TOKEN>"

# Create client
curl -X POST http://localhost:8080/api/v1/clients -H "Authorization: Bearer <ACCESS_TOKEN>" -H "Content-Type: application/json" -d '{"name":"John"}'
```

## Folder structure
```
com.devrbl.cleanauth.domain         // entities, repository interfaces
com.devrbl.cleanauth.application    // dtos, mappers, services
com.devrbl.cleanauth.infrastructure // security, configs
com.devrbl.cleanauth.presentation   // REST controllers
com.devrbl.cleanauth.shared         // exceptions, handler
```

## Tests
```bash
./mvnw test
```

## Roadmap
- Replace in-memory blacklist with Redis
- Dockerfile + docker-compose for PostgreSQL
