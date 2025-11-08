# Agilemorph Provider Platform

Agilemorph is a U.S.-based health-tech company that makes it easy to get clean, verified healthcare provider data into every workflow. This backend application exposes APIs to ingest provider records, normalize and verify provider attributes (NPI, licenses, specialties, affiliations, locations), de-duplicate records, and serve the unified provider graph to downstream systems.

## Features

- **Provider Management**: CRUD operations for healthcare providers
- **Data Normalization**: Automatic normalization of provider data
- **Duplicate Detection**: Identify potential duplicate providers
- **License Verification**: Track and validate provider licenses
- **Rule Engine**: Drools-based business rules for data quality
- **Audit Trail**: Complete audit logging for all provider changes
- **REST API**: Comprehensive REST endpoints with OpenAPI documentation

## Tech Stack

- **Java 17** with Quarkus framework
- **PostgreSQL** database
- **Drools** rule engine for business logic
- **Hibernate ORM** with Panache
- **Flyway** for database migrations
- **Docker** for containerization

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL (if running locally)

### Docker Development (Complete Stack) (Recommended)

1. **Build the application first**:

   ```bash
   cd backend
   mvn clean package -DskipTests
   cd ..
   ```

2. **Build and start all services**:

   ```bash
   docker-compose up --build -d
   ```

   Or to start with fresh database:

   ```bash
   docker-compose down -v
   docker-compose up --build -d
   ```

3. **View logs**:

   ```bash
   docker-compose logs -f backend
   ```

4. **Access the application**:

   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui
   - Health Check: http://localhost:8080/q/health

5. **Stop services**:

   ```bash
   docker-compose down
   ```

   To also remove volumes:

   ```bash
   docker-compose down -v
   ```

### Local Development (with Docker for PostgreSQL)

1. **Clone and setup**:

   ```bash
   git clone <repository-url>
   cd agilemorph-java-drl
   ```

2. **Start PostgreSQL database**:

   ```bash
   docker-compose up postgres -d
   ```

3. **Start the application**:

   ```bash
   cd backend
   mvn quarkus:dev
   ```

   Flyway migrations will run automatically on startup and seed the database with sample data.

4. **Access the application**:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/q/swagger-ui
   - Health Check: http://localhost:8080/q/health

### Local Development (without Docker - using H2)

For development without Docker, the application can use an in-memory H2 database:

1. **Start the application with dev profile**:

   ```bash
   cd backend
   mvn quarkus:dev -Dquarkus.profile=dev
   ```

   This will use H2 in-memory database and automatically create the schema.

2. **Seed sample data**:

   ```bash
   curl -X POST http://localhost:8080/api/seed/providers
   ```

## API Endpoints

### Provider Management

- `POST /api/providers` - Create a new provider
- `POST /api/providers/bulk` - Create multiple providers
- `GET /api/providers` - Get all providers
- `GET /api/providers/{id}` - Get provider by ID
- `GET /api/providers/npi/{npi}` - Get provider by NPI
- `PUT /api/providers/{id}` - Update provider
- `DELETE /api/providers/{id}` - Delete provider
- `GET /api/providers/status/{status}` - Get providers by verification status

### Rule Engine

- `POST /api/rules/evaluate` - Evaluate rules against provider
- `POST /api/rules/evaluate/{providerId}` - Evaluate rules for specific provider
- `GET /api/rules` - Get loaded rules
- `GET /api/rules/status` - Get rule engine status

### Data Management

- `POST /api/seed/providers` - Seed sample data

## Testing

### Run Tests

```bash
cd backend
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

The test suite includes:

- Unit tests for services
- Integration tests for REST endpoints
- Rule engine evaluation tests
- Database migration tests

## Database Schema

The application uses the following main entities:

- **Providers**: Core provider information
- **Licenses**: Provider license records
- **Practice Locations**: Provider practice locations
- **Audit Logs**: Change tracking
- **Rule Evaluations**: Rule execution results

## Business Rules

The application includes several Drools rules:

- **Data Validation Rules**: Validates provider data and flags issues
- **Business Logic Rules**: Implements various business logic requirements
- **Data Quality Rules**: Ensures data quality and consistency

## Configuration

The application supports multiple profiles:

### Default Profile (Production)

- Uses PostgreSQL database
- Requires database connection configuration
- Flyway migrations enabled
- Used in Docker containers

Configuration: `src/main/resources/application.properties`

### Dev Profile (Development without Docker)

- Uses H2 in-memory database
- Auto-creates schema on startup
- Flyway migrations disabled
- Perfect for quick local development

Configuration: `src/main/resources/application-dev.properties`

Start with: `mvn quarkus:dev -Dquarkus.profile=dev`

### Test Profile

- Uses H2 in-memory database
- Drop and create schema for each test run
- Flyway disabled
- Automatically used when running tests

Configuration: `src/main/resources/application-test.properties`

### Environment Variables

Key configuration properties:

```properties
# Database
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=agilemorph
quarkus.datasource.password=agilemorph123
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/agilemorph_db

# Server
quarkus.http.port=8080
quarkus.http.cors=true

# Flyway
quarkus.flyway.migrate-at-start=true
quarkus.flyway.baseline-on-migrate=true
```

## Development

### Project Structure

```
backend/
├── src/main/java/com/agilemorph/
│   ├── model/          # JPA entities
│   ├── dto/            # Data transfer objects
│   ├── service/        # Business logic
│   ├── resource/       # REST endpoints
│   └── rules/          # Drools rule files
├── src/main/resources/
│   ├── db/migration/   # Flyway migrations
│   └── rules/          # DRL rule files
└── src/test/java/      # Test classes
```

### Adding New Rules

1. Create a new `.drl` file in `src/main/resources/rules/`
2. Define your rule logic using Drools syntax
3. The rule will be automatically loaded by the rule engine

### Database Migrations

1. Create new migration files in `src/main/resources/db/migration/`
2. Follow the naming convention: `V{version}__{description}.sql`
3. Run migrations with `mvn flyway:migrate`

## Evaluation

### What to Submit

**Important**: Do not edit this repository directly. Instead, fork this repository to your own GitHub account and make all your changes there.

1. **Forked Repository**: Fork this repository and make all your changes in your fork
2. **Fixed Code**: Submit your code changes as a PR from your fork to this repository, or share the link to your forked repository
3. **Updated Tests**: Ensure all tests pass in your fork
4. **Short Writeup**: 1-2 paragraphs describing:
   - Root cause of each issue
   - How you fixed it
   - Any additional improvements made
5. **AI Chat exports**: Export your AI chats that you used to solve this challenge. Add them under this path- `/ai_chats` in your fork

### Challenge Requirements

This challenge requires you to:

- **Fix failing tests**: Identify and resolve test failures in the codebase
- **Fix buggy logic**: Correct bugs in the business logic implementation
- **Fix broken CI action**: The GitHub Actions CI workflow is currently broken
- **Ensure all tests pass**: After fixes, all tests should pass successfully

### Evaluation Criteria

- **Correctness of fixes & tests pass**: 50%
- **Code quality & test coverage improvements**: 20%
- **AI Usage Proficiency**: 15%
- **Explanation clarity of root cause & fix**: 15%

## Troubleshooting

### Common Issues

1. **Database Connection**:

   - Ensure PostgreSQL is running: `docker-compose ps`
   - Check PostgreSQL logs: `docker-compose logs postgres`
   - For Docker, ensure database is healthy before backend starts

2. **Port Conflicts**:

   - Ensure ports 8080 (API) and 5432 (PostgreSQL) are available
   - Check with: `lsof -i :8080` or `lsof -i :5432`
   - Stop conflicting services or change ports in configuration

3. **Build Artifacts Missing**:

   - If Docker build fails with "target/quarkus-app not found"
   - Build the application first: `cd backend && mvn clean package -DskipTests`

4. **Migration Conflicts**:

   - If Flyway reports duplicate key violations
   - Clear database and restart: `docker-compose down -v && docker-compose up -d`

5. **Rule Engine Issues**:

   - Check that DRL files are in `src/main/resources/rules/`
   - Verify rule syntax in `.drl` files
   - Check logs for rule compilation errors

6. **Test Failures**:
   - Tests use H2 database (configured in `application-test.properties`)
   - Run with: `mvn test`
   - Skip tests during build: `mvn package -DskipTests`

### Logs

Check application logs for detailed error information:

```bash
# For Docker
docker-compose logs -f backend
docker-compose logs -f postgres

# For local development
# Logs appear in console where mvn quarkus:dev is running
```

### Database Access

```bash
# PostgreSQL in Docker
docker exec -it agilemorph-postgres psql -U agilemorph -d agilemorph_db

# Common queries
\dt                           # List tables
SELECT * FROM providers;      # View providers
SELECT * FROM flyway_schema_history;  # Check migrations
```

## License

This project is for evaluation purposes only.
