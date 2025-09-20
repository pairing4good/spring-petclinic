# User Acceptance Test (UAT) Suite

## Overview

The Spring PetClinic application includes a comprehensive User Acceptance Test (UAT) suite that validates the main user-facing features and workflows from an end-to-end perspective. These tests ensure that the core functionality works as expected from a user's point of view.

## Test Coverage

The UAT suite covers the following major user workflows:

### 1. Welcome Page Navigation
- **Test**: `testWelcomePageUserAcceptance`
- **Coverage**: User can successfully navigate to the application home page
- **Validates**: Application startup, welcome page rendering, basic navigation

### 2. Veterinarian Information
- **Test**: `testViewVeterinariansUserAcceptance`
- **Coverage**: User can view the list of veterinarians and their specialties
- **Validates**: Vet listing page, data display, veterinarian information

### 3. Owner Management
- **Test**: `testOwnerManagementCompleteWorkflow`
- **Coverage**: Complete owner management workflow from creation to viewing
- **Validates**: Owner creation, search functionality, owner details display

### 4. Pet Management
- **Test**: `testPetManagementUserAcceptance`
- **Coverage**: Adding pets to owners and viewing pet information
- **Validates**: Pet creation, pet details, owner-pet relationships

### 5. Visit Management
- **Test**: `testVisitManagementUserAcceptance`
- **Coverage**: Scheduling visits for pets and viewing visit history
- **Validates**: Visit scheduling, visit details, pet visit tracking

### 6. Search Functionality
- **Test**: `testSearchOwnersUserAcceptance`
- **Coverage**: Searching for owners by last name
- **Validates**: Search functionality, result handling, navigation

### 7. Owner Details View
- **Test**: `testOwnerDetailsUserAcceptance`
- **Coverage**: Comprehensive owner information display including pets and visits
- **Validates**: Owner information, associated pets, visit history

## Running the UAT Suite

### Prerequisites
- Java 17 or higher
- Maven 3.6+ or included Maven wrapper (./mvnw)
- No additional setup required (uses H2 in-memory database)

### Running All UATs
```bash
# Using Maven wrapper (recommended)
./mvnw test -Dtest=UserAcceptanceTests

# Using Maven directly
mvn test -Dtest=UserAcceptanceTests
```

### Running Individual UATs
```bash
# Run specific test
./mvnw test -Dtest=UserAcceptanceTests#testWelcomePageUserAcceptance

# Run multiple specific tests
./mvnw test -Dtest=UserAcceptanceTests#testWelcomePageUserAcceptance,testViewVeterinariansUserAcceptance
```

### Running with Different Profiles
```bash
# Run with default H2 database
./mvnw test -Dtest=UserAcceptanceTests

# Run with PostgreSQL (requires Docker)
./mvnw test -Dtest=UserAcceptanceTests -Dspring.profiles.active=postgres
```

## Test Structure

### Test Class Location
- **File**: `src/test/java/org/springframework/samples/petclinic/UserAcceptanceTests.java`
- **Package**: `org.springframework.samples.petclinic`

### Technical Implementation
- **Framework**: Spring Boot Test (@SpringBootTest)
- **Web Environment**: Random port for isolated testing
- **HTTP Client**: RestTemplate for actual HTTP requests
- **Database**: H2 in-memory with test data auto-loaded
- **Scope**: Full application context integration testing

### Test Data
The tests use the standard test data loaded from:
- **Location**: `src/main/resources/db/h2/data.sql`
- **Content**: Pre-loaded owners, pets, visits, vets, and specialties
- **Reset**: Database is reset between test classes via @DirtiesContext

## Expected Results

### Successful Test Run Output
```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
```

### Test Execution Time
- **Typical Runtime**: 15-30 seconds total
- **Individual Test**: 1-3 seconds each
- **Startup Time**: 5-10 seconds for application context

## Integration with CI/CD

### Maven Integration
The UAT suite integrates with the existing Maven build lifecycle:
- Runs during the `test` phase
- Follows existing test naming conventions
- Uses existing Spring profiles and configuration

### CI Pipeline Integration
The tests are designed to work in CI environments:
- No external dependencies (uses embedded H2)
- Self-contained test data
- Deterministic test execution
- Clear pass/fail indicators

### Example CI Configuration
```yaml
# GitHub Actions example
- name: Run User Acceptance Tests
  run: ./mvnw test -Dtest=UserAcceptanceTests
```

## Troubleshooting

### Common Issues

#### Port Conflicts
If tests fail with port binding errors:
```bash
# Tests use random ports, but ensure no services are blocking
lsof -i :8080
```

#### Memory Issues
For large test suites:
```bash
export MAVEN_OPTS="-Xmx1024m"
./mvnw test -Dtest=UserAcceptanceTests
```

#### Database Issues
Tests use H2 in-memory database:
- No cleanup required
- Data is reset between test classes
- No persistent state between runs

### Debug Mode
Run tests with debug output:
```bash
./mvnw test -Dtest=UserAcceptanceTests -X
```

## Maintenance

### Adding New UATs
1. Add new test methods to `UserAcceptanceTests.java`
2. Follow existing naming pattern: `test[Feature]UserAcceptance`
3. Use `@DisplayName` for clear test descriptions
4. Include comprehensive workflow validation

### Updating Existing UATs
1. Maintain test isolation and repeatability
2. Update test data expectations if schema changes
3. Verify all assertions are meaningful for user acceptance

### Best Practices
- Focus on user-visible functionality
- Test complete workflows, not just individual endpoints
- Include both positive and negative scenarios
- Maintain clear, descriptive test names and documentation