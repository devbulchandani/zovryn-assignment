# Finance Data Processing and Access Control Backend

A robust Spring Boot backend application for managing financial records with role-based access control (RBAC).

## 🚀 Live Demo

**Deployed Application**: [https://finance-kzfl.onrender.com](https://finance-kzfl.onrender.com)

**Swagger UI**: [https://finance-kzfl.onrender.com/swagger-ui.html](https://finance-kzfl.onrender.com/swagger-ui.html)

**Test Credentials**:
- Admin: `admin` / `admin`
- Analyst: `analyst` / `analyst`
- Viewer: `viewer` / `viewer`

**Quick Test**:
```bash
curl -u viewer:viewer https://finance-kzfl.onrender.com/api/v1/dashboard/summary
```

## Table of Contents
- [Live Demo](#-live-demo)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Security & Access Control](#security--access-control)
- [Test Credentials](#test-credentials)
- [Architectural Decisions](#architectural-decisions)

## Features

- **User Management**: Create and manage users with different roles
- **Financial Record Management**: CRUD operations for income and expense tracking
- **Dashboard Analytics**: Real-time financial summaries with database-level aggregation
- **Role-Based Access Control**: Three-tier permission system (VIEWER, ANALYST, ADMIN)
- **Input Validation**: Comprehensive validation for all API inputs
- **Error Handling**: Standardized error responses with detailed field-level validation errors
- **API Documentation**: Interactive Swagger UI for API exploration

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (In-memory)
- **Security**: Spring Security with HTTP Basic Authentication
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Documentation**: Springdoc OpenAPI (Swagger)
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/finance/backend/
├── config/
│   ├── DataInitializer.java       # Database seeding
│   ├── OpenApiConfig.java         # Swagger configuration
│   └── SecurityConfig.java        # Security configuration
├── controller/
│   ├── DashboardController.java   # Dashboard endpoints
│   ├── FinancialRecordController.java
│   └── UserController.java
├── dto/
│   ├── DashboardSummaryResponse.java
│   ├── ErrorResponse.java
│   ├── FinancialRecordRequest.java
│   ├── FinancialRecordResponse.java
│   ├── UserRequest.java
│   └── UserResponse.java
├── entity/
│   ├── FinancialRecord.java       # Financial record entity
│   └── User.java                  # User entity
├── enums/
│   ├── TransactionType.java       # INCOME, EXPENSE
│   └── UserRole.java              # VIEWER, ANALYST, ADMIN
├── exception/
│   ├── DuplicateResourceException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── FinancialRecordRepository.java
│   └── UserRepository.java
├── security/
│   └── CustomUserDetailsService.java
├── service/
│   ├── DashboardService.java
│   ├── FinancialRecordService.java
│   └── UserService.java
└── FinanceBackendApplication.java
```

## Getting Started

### Option 1: Use the Live Demo

The application is already deployed and ready to use:
- **Base URL**: `https://finance-kzfl.onrender.com`
- **Swagger UI**: `https://finance-kzfl.onrender.com/swagger-ui.html`

No setup required! Just use the test credentials to start exploring.

### Option 2: Run Locally

#### Prerequisites

- Java 21 (or Java 17)
- Maven 3.6+

#### Running the Application

1. **Clone the repository** (or extract the project)

2. **Navigate to the project directory**:
   ```bash
   cd finance-backend
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   Or build and run the JAR:
   ```bash
   mvn clean package
   java -jar target/backend-1.0.0.jar
   ```

4. **Access the application**:
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:financedb`
     - Username: `sa`
     - Password: (leave empty)

## API Documentation

### Interactive Documentation
- **Live Demo**: [https://finance-kzfl.onrender.com/swagger-ui.html](https://finance-kzfl.onrender.com/swagger-ui.html)
- **Local**: `http://localhost:8080/swagger-ui.html`

Use the interactive Swagger UI to test endpoints directly with the provided test credentials.

### API Endpoints

#### User Management (`/api/v1/users`) - ADMIN Only
- `POST /api/v1/users` - Create a new user
- `GET /api/v1/users` - List all users
- `PATCH /api/v1/users/{id}/status` - Toggle user active status

#### Financial Records (`/api/v1/records`)
- `POST /api/v1/records` - Create record (ADMIN)
- `GET /api/v1/records` - List records with filters (ANALYST, ADMIN)
  - Query params: `type`, `category`, `startDate`, `endDate`
- `PUT /api/v1/records/{id}` - Update record (ADMIN)
- `DELETE /api/v1/records/{id}` - Delete record (ADMIN)

#### Dashboard (`/api/v1/dashboard`) - All Roles
- `GET /api/v1/dashboard/summary` - Get financial summary (VIEWER, ANALYST, ADMIN)

### Example Requests

#### Create a Financial Record (ADMIN)
```bash
# Live Demo
curl -X POST https://finance-kzfl.onrender.com/api/v1/records \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1500.00,
    "type": "INCOME",
    "category": "Salary",
    "transactionDate": "2026-04-01",
    "notes": "Monthly salary"
  }'

# Local
curl -X POST http://localhost:8080/api/v1/records \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1500.00,
    "type": "INCOME",
    "category": "Salary",
    "transactionDate": "2026-04-01",
    "notes": "Monthly salary"
  }'
```

#### Get Dashboard Summary (Any Role)
```bash
# Live Demo
curl -X GET https://finance-kzfl.onrender.com/api/v1/dashboard/summary \
  -u viewer:viewer

# Local
curl -X GET http://localhost:8080/api/v1/dashboard/summary \
  -u viewer:viewer
```

#### Filter Records (ANALYST/ADMIN)
```bash
# Live Demo
curl -X GET "https://finance-kzfl.onrender.com/api/v1/records?type=EXPENSE&startDate=2026-03-01&endDate=2026-03-31" \
  -u analyst:analyst

# Local
curl -X GET "http://localhost:8080/api/v1/records?type=EXPENSE&startDate=2026-03-01&endDate=2026-03-31" \
  -u analyst:analyst
```

## Security & Access Control

### Authentication
- HTTP Basic Authentication
- Passwords hashed using BCrypt
- Stateless session management

### Authorization Matrix

| Role    | Dashboard (GET) | Records (GET) | Records (POST/PUT/DELETE) | Users (All) |
|---------|----------------|---------------|---------------------------|-------------|
| VIEWER  | ✅             | ❌            | ❌                        | ❌          |
| ANALYST | ✅             | ✅            | ❌                        | ❌          |
| ADMIN   | ✅             | ✅            | ✅                        | ✅          |

### Security Features
- Method-level security with `@PreAuthorize`
- Inactive users cannot authenticate
- Role-based endpoint protection
- CSRF disabled (stateless API)

## Test Credentials

The application is pre-seeded with three users and 12 financial records for immediate testing:

| Username | Password | Role    | Description                           |
|----------|----------|---------|---------------------------------------|
| admin    | admin    | ADMIN   | Full access to all endpoints          |
| analyst  | analyst  | ANALYST | Read access to records and dashboard  |
| viewer   | viewer   | VIEWER  | Read access to dashboard only         |

## Architectural Decisions

### 1. Controller-Service-Repository Pattern
- **Controllers**: Handle HTTP requests, validation, and response formatting
- **Services**: Contain business logic and transaction management
- **Repositories**: Data access layer with custom queries

**Rationale**: Clear separation of concerns, easier testing, and maintainability.

### 2. DTO Pattern
- Never expose entities directly in API responses
- Separate request and response DTOs
- Prevents over-fetching and security issues

**Rationale**: Decouples internal data model from API contract, provides flexibility for API evolution.

### 3. BigDecimal for Financial Amounts
- Used `BigDecimal` instead of `Double` or `Float`
- Precision set to 19 digits with 2 decimal places

**Rationale**: Avoids floating-point precision errors critical in financial calculations.

### 4. Database-Level Aggregation
- Dashboard summary uses JPQL queries with `SUM()` function
- Calculations performed in database, not in application memory

**Rationale**: Efficient for large datasets, reduces memory usage, leverages database optimization.

### 5. Method-Level Security
- `@PreAuthorize` annotations on controller methods
- Role-based access control at method level

**Rationale**: Fine-grained control, clear security requirements visible in code.

### 6. Global Exception Handling
- `@ControllerAdvice` for centralized error handling
- Standardized error response format
- Field-level validation error details

**Rationale**: Consistent error responses, better client experience, easier debugging.

### 7. H2 In-Memory Database
- Automatic schema creation
- Pre-seeded with test data
- No external database setup required

**Rationale**: Easy for reviewers to test, zero configuration, perfect for assignments/demos.

### 8. Lombok
- Reduces boilerplate code
- `@Data`, `@Builder`, `@RequiredArgsConstructor` annotations

**Rationale**: Cleaner code, faster development, less maintenance.

### 9. Validation at Multiple Layers
- Bean Validation annotations on DTOs
- Business logic validation in services
- Database constraints on entities

**Rationale**: Defense in depth, catches errors early, provides clear feedback.

### 10. Logging
- SLF4J with Logback
- Structured logging at service layer
- Debug level for security events

**Rationale**: Easier debugging, audit trail, production monitoring.

## Deployment

### Live Environment
The application is deployed on Render at: [https://finance-kzfl.onrender.com](https://finance-kzfl.onrender.com)

**Note**: The deployed instance uses an in-memory H2 database that resets periodically. This is intentional for demo purposes to ensure a clean state for reviewers.

### Docker Deployment
A `Dockerfile` is included for containerized deployment:

```bash
# Build the image
docker build -t finance-backend .

# Run the container
docker run -p 8080:8080 finance-backend
```

## Additional Notes

### Data Persistence
- H2 database is in-memory and resets on application restart
- Data is re-seeded automatically on startup
- For production, switch to PostgreSQL/MySQL by updating `application.properties`

### Extending the Application
- Add pagination: Implement `Pageable` in repository methods
- Add sorting: Use `Sort` parameter in queries
- Add more filters: Extend `findByFilters` method
- Add audit logging: Use Spring Data JPA auditing
- Add JWT authentication: Replace Basic Auth with JWT tokens

### Testing
The application includes comprehensive seed data:
- 3 users (one per role)
- 12 financial records spanning 30 days
- Mix of income and expense transactions
- Various categories (Salary, Rent, Groceries, etc.)

## License
This project is created for educational/assignment purposes.
