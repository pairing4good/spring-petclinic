#!/bin/bash

# Script to run Playwright tests for Spring PetClinic
# This script ensures the application is running and then executes the Playwright tests

set -e

echo "Spring PetClinic Playwright Test Runner"
echo "======================================"

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is required but not installed. Please install Node.js first."
    exit 1
fi

# Check if npx is available
if ! command -v npx &> /dev/null; then
    echo "❌ npx is required but not available. Please ensure Node.js is properly installed."
    exit 1
fi

echo "✅ Node.js and npx are available"

# Install Playwright browsers if needed
echo "🔧 Checking Playwright browser installation..."
if ! npx playwright install chromium --dry-run &> /dev/null; then
    echo "📥 Installing Playwright browsers..."
    npx playwright install chromium
    echo "✅ Playwright browsers installed"
else
    echo "✅ Playwright browsers already installed"
fi

# Check if application is running
echo "🔍 Checking if Spring Boot application is running on port 8080..."
if ! curl -s http://localhost:8080 > /dev/null; then
    echo "❌ Spring Boot application is not running on port 8080"
    echo "Please start the application first:"
    echo "  ./gradlew bootRun"
    echo "Then run this script again."
    exit 1
fi

echo "✅ Spring Boot application is running"

# Run Playwright tests
echo "🎭 Running Playwright tests..."
./gradlew playwrightTest

echo "✅ Playwright tests completed!"
echo ""
echo "📸 Screenshots are available in: target/playwright-screenshots/"
echo "📄 Test reports are available in: build/reports/tests/playwrightTest/"