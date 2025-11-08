# Testing Guide

This document provides guidance on testing the application and understanding the test suite.

## Current Issues

The application currently has some issues that need to be identified and fixed. When you run the test suite, you will notice that certain tests are failing, indicating problems in the codebase.

### What's Currently Not Working

1. **Test Failures**: Several tests are currently failing and need to be fixed
2. **Business Logic Bugs**: There are bugs in the business logic that need to be corrected
3. **Test Isolation**: Some tests may have issues with data isolation and cleanup
4. **CI Workflow Issues**: The GitHub Actions CI workflow is broken

### Expected Behavior After Fixes

Once the issues are resolved, the following should work correctly:

1. **Business Logic**:

   - All business logic should work correctly
   - Calculations and validations should be accurate
   - All service layer functionality should work as expected

2. **Test Suite**:

   - All unit tests should pass
   - All integration tests should pass
   - All rule engine tests should pass
   - No test failures or errors

3. **CI/CD Pipeline**:
   - GitHub Actions workflow should run successfully
   - All CI checks should pass
   - No deprecated action warnings or errors

## Running Tests

To run the test suite:

1. **Run all tests**:

   ```bash
   cd backend
   mvn test
   ```

2. **Run specific test classes**:
   ```bash
   mvn test -Dtest=ProviderServiceTest
   mvn test -Dtest=RuleEngineServiceTest
   ```

## Test Structure

The test suite includes:

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **Rule Engine Tests**: Test business rule evaluation

## Test Categories

### Service Tests

- Tests for business logic validation
- Tests for data processing and calculations
- Tests for service layer functionality

### Provider Management Tests

- Tests for provider data handling
- Tests for data normalization
- Tests for provider operations

### Rule Engine Tests

- Tests for business rule evaluation
- Tests for rule execution results
- Tests for rule performance

## Debugging Tests

When tests fail:

1. **Check the test output** for error messages
2. **Review the test code** to understand expected behavior
3. **Examine the application logs** for additional context
4. **Use debugging tools** to step through the code

## Test Data

The test suite uses:

- Sample provider data
- Mock license information
- Test database configurations
- Isolated test environments

## Best Practices

- Run tests frequently during development
- Write tests for new functionality
- Keep tests focused and independent
- Use meaningful test names and descriptions
