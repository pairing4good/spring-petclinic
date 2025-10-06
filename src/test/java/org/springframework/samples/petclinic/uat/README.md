# User Acceptance Tests (UATs)

This directory contains automated User Acceptance Tests that verify key user flows in story format.
Tests are designed to be fast, reliable, and readable by non-technical stakeholders.

## Test Structure

UATs are organized by feature areas:

- **OwnerRegistrationUAT** - Tests for pet owner registration functionality (service-level)
- **PetManagementUAT** - Tests for pet registration, appointment booking, and visit management (service-level)
- **VeterinarianInformationUAT** - Tests for viewing veterinarian information (HTTP API)

## Running UATs

### Run All UATs
```bash
mvn test -Dtest="*UAT"
```

### Run Specific UAT Suite
```bash
mvn test -Dtest=OwnerRegistrationUAT
mvn test -Dtest=PetManagementUAT
mvn test -Dtest=VeterinarianInformationUAT
```

## Test Characteristics

### Speed and Reliability
- Service/repository layer testing for maximum speed and reliability
- HTTP-level testing for GET endpoints only
- All tests are idempotent and can be run multiple times
- No external dependencies or complex setup required

### Story-Driven Format
Tests follow the "As a..., I want..., so that..." format:

```java
@Test
@DisplayName("As a pet owner, I want to register myself in the system, so that I can manage my pet's information")
void petOwnerCanRegisterInTheSystem() {
    // Given I am a new pet owner who wants to register
    // When I register in the system  
    // Then my registration should be successful
}
```

## Key User Stories Covered

1. **As a pet owner, I want to register myself in the system, so that I can manage my pet's information**
2. **As a pet owner, I want to add my pet to the system, so that I can track their medical records**
3. **As a pet owner, I want to book a veterinary appointment, so that I can bring my pet for medical care**
4. **As a clinic staff member, I want to view veterinarian information, so that I can assist pet owners**
5. **As a pet owner, I want to view my pet's visit history, so that I can track their health**

## Notes

- Tests use H2 in-memory database with test data loaded from `data.sql`
- Each test runs in a transaction that is rolled back after completion
- No external dependencies required - tests are completely self-contained
- Service-level testing provides maximum speed while maintaining story-driven UAT format
- Tests pass reliably on repeated runs and can be executed in any order