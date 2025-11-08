#!/bin/bash

echo "=========================================="
echo "Running Agilemorph Provider Platform Tests"
echo "=========================================="

cd backend

echo ""
echo "1. Compiling the application..."
mvn clean compile

echo ""
echo "2. Running unit tests..."
mvn test

echo ""
echo "3. Running integration tests..."
mvn verify

echo ""
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo "Expected: Some tests should fail due to intentional bugs"
echo "Look for:"
echo "  - License expiry calculation failures"
echo "  - Duplicate detection test failures"
echo "  - Rule engine evaluation issues"
echo ""
echo "The bugs are in:"
echo "  - License.isExpired() method (using wrong date field)"
echo "  - ProviderService.findPotentialDuplicates() method (missing trim on DB values)"
echo "=========================================="
