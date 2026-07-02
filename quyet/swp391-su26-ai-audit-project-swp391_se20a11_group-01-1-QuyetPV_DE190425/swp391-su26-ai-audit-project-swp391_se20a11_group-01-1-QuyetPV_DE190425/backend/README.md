# Restaurant Management System - Backend

## Tech Stack
* Java 21
* Spring Boot 3.5.x
* Spring Security, Spring Data JPA, Validation
* Flyway (Database Migration)
* MySQL 8.x
* Lombok
* Springdoc OpenAPI (Swagger)

## Run Guide
To compile and run the application locally:
```bash
mvn clean compile
mvn spring-boot:run
```

## Environment Variables
The following environment variables can be configured:
* `SERVER_PORT`: Port to run the application (default: 8080)
* `DB_URL`: JDBC URL for MySQL
* `DB_USERNAME`: Database username
* `DB_PASSWORD`: Database password
* `LOG_LEVEL`: Logging level (default: DEBUG)

## Project Structure
* `config`: Global configurations (Swagger, CORS, etc.)
* `controller`: REST API endpoints
* `dto`: Data Transfer Objects for API requests/responses
* `entity`: JPA Entities representing database tables
* `exception`: Global exception handling
* `mapper`: Object mapping (MapStruct or manual)
* `repository`: Spring Data JPA interfaces
* `security`: JWT and Spring Security configuration
* `service`: Business logic interfaces
* `service/impl`: Business logic implementation
* `util`: Utility classes
* `validation`: Custom validation logic
