# Agilemorph Provider Platform Challenge

## Overview

This is a backend-only challenge for Agilemorph, a U.S.-based health-tech company that makes it easy to get clean, verified healthcare provider data into every workflow. The backend exposes APIs to ingest provider records, normalize and verify provider attributes, de-duplicate records, and serve the unified provider graph to downstream systems.

## Challenge Details

### What You Need to Do

1. **Identify and Fix Issues**: The codebase contains some issues that need to be addressed
2. **Ensure All Tests Pass**: After fixing the issues, all tests should pass
3. **Submit Your Solution**: Provide fixed code, updated tests, and a short writeup

### The Issues

The codebase contains some issues that need to be identified and fixed:

1. **Test Failures**: Several tests are currently failing and need to be fixed
2. **Buggy Logic**: There are bugs in the business logic that need to be corrected
3. **Broken CI Action**: The GitHub Actions CI workflow is currently broken

### Getting Started

1. **Clone the repository** (if not already done)
2. **Read the documentation**:

   - `README.md` - Project overview and setup
   - `DESIGN_DECISIONS.md` - Architecture and design choices
   - `BUGS.md` - Testing guide

3. **Run the tests** to see the failing tests:

   ```bash
   ./run-tests.sh
   ```

4. **Identify the issues** by running tests and analyzing failures

5. **Fix the issues** and ensure all tests pass

6. **Submit your solution** with:
   - Fixed code
   - Updated tests (if needed)
   - Short writeup explaining the root cause and fix

## Project Structure

```
agilemorph-java-drl/
├── backend/                    # Quarkus application
│   ├── src/main/java/         # Java source code
│   ├── src/main/resources/     # Configuration and resources
│   ├── src/test/java/         # Test classes
│   └── pom.xml               # Maven configuration
├── docker-compose.yml         # Docker setup
├── README.md                  # Project documentation
├── DESIGN_DECISIONS.md        # Architecture decisions
├── BUGS.md                    # Testing guide
├── EVALUATION_RUBRIC.md       # Evaluation criteria
└── run-tests.sh              # Test runner script
```

## Key Features

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

## Running the Application

### Local Development

1. **Start the database**:

   ```bash
   docker-compose up postgres -d
   ```

2. **Run the application**:

   ```bash
   cd backend
   mvn quarkus:dev
   ```

3. **Access the application**:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui

### Docker Development

1. **Start all services**:
   ```bash
   docker-compose up --build
   ```

## Testing

### Run Tests

```bash
./run-tests.sh
```

### Expected Test Failures

The following tests may fail initially:

1. **Service Tests**: Various service layer tests may fail
2. **Rule Engine Tests**: Some rule evaluation tests may fail
3. **Integration Tests**: Some integration tests may fail
4. **CI Workflow**: The GitHub Actions workflow will fail

### After Fixing Issues

All tests should pass:

- Unit tests for services
- Integration tests for REST endpoints
- Rule engine evaluation tests
- Database migration tests

## Evaluation Criteria

### Scoring (100 points total)

1. **Correctness of Fixes & Tests Pass (50 points)**

   - Issue identification and fixing
   - All tests passing
   - No regression issues

2. **Code Quality & Test Coverage (20 points)**

   - Clean, readable code
   - Comprehensive test coverage
   - Following best practices

3. **Explanation Clarity (15 points)**

   - Clear root cause analysis
   - Detailed solution description
   - Technical understanding demonstration

4. **AI Usage & Prompt Quality (15 points)**
   - Quality of prompts used to understand the codebase
   - Effective use of AI to identify bugs and issues
   - Strategic AI assistance in debugging and fixing problems
   - Clear documentation of AI-assisted problem-solving process

## Submission Requirements

### What to Submit

**Important**: Do not edit this repository directly. Instead, fork this repository to your own GitHub account and make all your changes there.

1. **Forked Repository**: Fork this repository and make all your changes in your fork
2. **Fixed Code**: Submit your code changes as a PR from your fork to this repository, or share the link to your forked repository
3. **Updated Tests**: Ensure all tests pass in your fork
4. **AI Chat Exports**: Export your AI conversations as text files and place them in the `/ai_chats` folder in your fork
5. **Short Writeup**: 1-2 paragraphs describing:
   - Root cause of each issue
   - How you fixed it
   - Any additional improvements made

### Writeup Template

For each issue, provide:

1. **Root Cause**: What was causing the issue?
2. **Solution**: How did you fix it?
3. **Impact**: What was the impact of the issue?
4. **Prevention**: How could this issue have been prevented?

## Tips for Success

1. **Read the testing guide** in `BUGS.md`
2. **Run the tests** to see which ones fail
3. **Look at the test code** to understand expected behavior
4. **Check the entity classes** for potential issues
5. **Check the service classes** for business logic bugs
6. **Add your own tests** if needed
7. **Document your changes** clearly

## Getting Help

- Check the `README.md` for setup instructions
- Review the `DESIGN_DECISIONS.md` for architecture details
- Look at the test files to understand expected behavior
- Use the testing guide in `BUGS.md` to understand the test suite

## Good Luck!

This challenge tests your ability to:

- Debug complex business logic
- Work with Java/Quarkus applications
- Understand database operations
- Implement proper testing
- Communicate technical solutions clearly

Take your time, read the code carefully, and don't hesitate to add your own tests to verify your fixes work correctly.
