# Spring PetClinic Sample Application [![Build Status](https://github.com/spring-projects/spring-petclinic/actions/workflows/maven-build.yml/badge.svg)](https://github.com/spring-projects/spring-petclinic/actions/workflows/maven-build.yml)[![Build Status](https://github.com/spring-projects/spring-petclinic/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/spring-projects/spring-petclinic/actions/workflows/gradle-build.yml)

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/spring-projects/spring-petclinic) [![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=main&repo=7517918)

## Understanding the Spring Petclinic application with a few diagrams

[See the presentation here](https://speakerdeck.com/michaelisvy/spring-petclinic-sample-application)

## Run Petclinic locally

Spring Petclinic is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/) or [Gradle](https://spring.io/guides/gs/gradle/). You can build a jar file and run it from the command line (it should work just as well with Java 17 or newer):

```bash
git clone https://github.com/spring-projects/spring-petclinic.git
cd spring-petclinic
./mvnw package
java -jar target/*.jar
```

(On Windows, or if your shell doesn't expand the glob, you might need to specify the JAR file name explicitly on the command line at the end there.)

You can then access the Petclinic at <http://localhost:8080/>.

<img width="1042" alt="petclinic-screenshot" src="https://cloud.githubusercontent.com/assets/838318/19727082/2aee6d6c-9b8e-11e6-81fe-e889a5ddfded.png">

Or you can run it from Maven directly using the Spring Boot Maven plugin. If you do this, it will pick up changes that you make in the project immediately (changes to Java source files require a compile as well - most people use an IDE for this):

```bash
./mvnw spring-boot:run
```

> NOTE: If you prefer to use Gradle, you can build the app using `./gradlew build` and look for the jar file in `build/libs`.

## Building a Container

There is no `Dockerfile` in this project. You can build a container image (if you have a docker daemon) using the Spring Boot build plugin:

```bash
./mvnw spring-boot:build-image
```

## In case you find a bug/suggested improvement for Spring Petclinic

Our issue tracker is available [here](https://github.com/spring-projects/spring-petclinic/issues).

## Database configuration

In its default configuration, Petclinic uses an in-memory database (H2) which
gets populated at startup with data. The h2 console is exposed at `http://localhost:8080/h2-console`,
and it is possible to inspect the content of the database using the `jdbc:h2:mem:<uuid>` URL. The UUID is printed at startup to the console.

A similar setup is provided for MySQL and PostgreSQL if a persistent database configuration is needed. Note that whenever the database type changes, the app needs to run with a different profile: `spring.profiles.active=mysql` for MySQL or `spring.profiles.active=postgres` for PostgreSQL. See the [Spring Boot documentation](https://docs.spring.io/spring-boot/how-to/properties-and-configuration.html#howto.properties-and-configuration.set-active-spring-profiles) for more detail on how to set the active profile.

You can start MySQL or PostgreSQL locally with whatever installer works for your OS or use docker:

```bash
docker run -e MYSQL_USER=petclinic -e MYSQL_PASSWORD=petclinic -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=petclinic -p 3306:3306 mysql:9.2
```

or

```bash
docker run -e POSTGRES_USER=petclinic -e POSTGRES_PASSWORD=petclinic -e POSTGRES_DB=petclinic -p 5432:5432 postgres:17.5
```

Further documentation is provided for [MySQL](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources/db/mysql/petclinic_db_setup_mysql.txt)
and [PostgreSQL](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources/db/postgres/petclinic_db_setup_postgres.txt).

Instead of vanilla `docker` you can also use the provided `docker-compose.yml` file to start the database containers. Each one has a service named after the Spring profile:

```bash
docker compose up mysql
```

or

```bash
docker compose up postgres
```

## Test Applications

At development time we recommend you use the test applications set up as `main()` methods in `PetClinicIntegrationTests` (using the default H2 database and also adding Spring Boot Devtools), `MySqlTestApplication` and `PostgresIntegrationTests`. These are set up so that you can run the apps in your IDE to get fast feedback and also run the same classes as integration tests against the respective database. The MySql integration tests use Testcontainers to start the database in a Docker container, and the Postgres tests use Docker Compose to do the same thing.

## End-to-End Testing with Playwright

The Spring PetClinic includes comprehensive end-to-end tests using Microsoft Playwright for Java. These tests cover all user workflows, error scenarios, and cross-browser compatibility.

### Running E2E Tests

#### Prerequisites
- Java 17 or later
- Maven 3.6+ or Gradle 8.0+
- No additional browser installation required (Playwright manages browsers automatically)

#### Maven Commands
```bash
# Run all E2E tests
./mvnw test -Pe2e

# Run E2E tests in headless mode (suitable for CI)
./mvnw test -Pe2e -Dplaywright.headless=true

# Run specific E2E test class
./mvnw test -Pe2e -Dtest="BasicNavigationE2ETests"

# Run specific test method
./mvnw test -Pe2e -Dtest="BasicNavigationE2ETests#testHomepageLoading"
```

#### Gradle Commands
```bash
# Run all E2E tests
./gradlew playwrightTest

# Run E2E tests in headless mode (suitable for CI)
./gradlew playwrightTest -Dplaywright.headless=true

# Run specific E2E test class
./gradlew playwrightTest --tests="BasicNavigationE2ETests"

# Run specific test method
./gradlew playwrightTest --tests="BasicNavigationE2ETests.testHomepageLoading"
```

### E2E Test Coverage

The Playwright test suite provides comprehensive coverage of:

#### Core Navigation and User Interface
- Homepage loading and layout verification
- Navigation menu functionality across all pages
- Browser back/forward button behavior
- Responsive design on mobile, tablet, and desktop viewports
- Keyboard navigation and accessibility features

#### Owner Management (CRUD Operations)
- Searching for owners (with and without search criteria)
- Adding new owners with form validation
- Editing existing owner information
- Viewing owner details and associated pets
- Form validation and error handling

#### Pet and Visit Management
- Adding new pets to owner accounts
- Editing pet information
- Adding visit records for medical history
- Viewing pet and visit information

#### Veterinarian Information
- Viewing veterinarian listings
- Pagination through multiple pages of vets
- Specialty information display

#### Error Handling and Edge Cases
- 404 page handling for invalid URLs
- 500 error page functionality
- Form validation error messages
- Empty state handling (no search results)
- Network timeout and error scenarios

#### Cross-Browser and Device Testing
- Chrome/Chromium compatibility (default)
- Firefox compatibility (via configuration)
- WebKit/Safari compatibility (via configuration)
- Mobile and tablet responsive design

### Browser Configuration

Tests run in Chromium by default. To test with different browsers:

```bash
# Firefox
./mvnw test -Pe2e -Dplaywright.browser=firefox

# WebKit (Safari engine)
./mvnw test -Pe2e -Dplaywright.browser=webkit

# Gradle equivalent
./gradlew playwrightTest -Dplaywright.browser=firefox
```

### Debugging Failed Tests

#### Local Development
```bash
# Run tests with browser visible (non-headless)
./mvnw test -Pe2e -Dplaywright.headless=false

# Slow down test execution for observation
./mvnw test -Pe2e -Dplaywright.headless=false -Dplaywright.slowMo=1000
```

#### Test Failure Troubleshooting

1. **Element Not Found Errors**
   - Check if the page has fully loaded
   - Verify element selectors are correct
   - Ensure dynamic content has time to render

2. **Timing Issues**
   - Tests include proper wait strategies
   - Network timeouts are configured appropriately
   - Loading states are handled correctly

3. **Browser Dependencies**
   - Playwright automatically downloads required browsers
   - In CI environments, ensure sufficient disk space and network access

#### Common Test Patterns

The tests follow robust patterns to prevent flaky behavior:

```java
// Proper navigation with loading verification
page.navigate("http://localhost:" + port + "/path");
assertThat(page.locator("h2:has-text('Expected Heading')")).isVisible();

// Form submission with response waiting
page.locator("input[name='field']").fill("value");
page.locator("button:has-text('Submit')").click();
assertThat(page.locator("h2:has-text('Success Page')")).isVisible();

// Dynamic content verification
assertThat(page.locator("table")).isVisible();
assertThat(page.locator("table tr")).toHaveCountGreaterThan(0);
```

### CI/CD Integration

The E2E tests are integrated into GitHub Actions workflows:

- **Maven workflow**: Runs E2E tests after successful build
- **Gradle workflow**: Runs E2E tests after successful build
- Tests run in headless mode in CI environments
- Browser dependencies are automatically installed

### Test Organization

E2E tests are organized into logical modules:

- `BasicNavigationE2ETests`: Core navigation and UI functionality
- `ComprehensiveE2ETests`: Advanced features, CRUD operations, and error handling

Each test follows the naming convention: "As a [user], I want [action], so that [outcome]"

### Performance Considerations

- Tests are designed to run efficiently with proper wait strategies
- Database uses in-memory H2 for fast test execution
- Tests are isolated and can run in parallel
- Average test execution time: 30-60 seconds per test class

### Contributing to E2E Tests

When adding new features to Spring PetClinic:

1. Add corresponding E2E tests for new user workflows
2. Follow existing test patterns for consistency
3. Ensure tests are deterministic and reliable
4. Include proper error scenario testing
5. Test responsive design for new UI components
6. Verify cross-browser compatibility for complex interactions

## Compiling the CSS

There is a `petclinic.css` in `src/main/resources/static/resources/css`. It was generated from the `petclinic.scss` source, combined with the [Bootstrap](https://getbootstrap.com/) library. If you make changes to the `scss`, or upgrade Bootstrap, you will need to re-compile the CSS resources using the Maven profile "css", i.e. `./mvnw package -P css`. There is no build profile for Gradle to compile the CSS.

## Working with Petclinic in your IDE

### Prerequisites

The following items should be installed in your system:

- Java 17 or newer (full JDK, not a JRE)
- [Git command line tool](https://help.github.com/articles/set-up-git)
- Your preferred IDE
  - Eclipse with the m2e plugin. Note: when m2e is available, there is an m2 icon in `Help -> About` dialog. If m2e is
  not there, follow the install process [here](https://www.eclipse.org/m2e/)
  - [Spring Tools Suite](https://spring.io/tools) (STS)
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/)
  - [VS Code](https://code.visualstudio.com)

### Steps

1. On the command line run:

    ```bash
    git clone https://github.com/spring-projects/spring-petclinic.git
    ```

1. Inside Eclipse or STS:

    Open the project via `File -> Import -> Maven -> Existing Maven project`, then select the root directory of the cloned repo.

    Then either build on the command line `./mvnw generate-resources` or use the Eclipse launcher (right-click on project and `Run As -> Maven install`) to generate the CSS. Run the application's main method by right-clicking on it and choosing `Run As -> Java Application`.

1. Inside IntelliJ IDEA:

    In the main menu, choose `File -> Open` and select the Petclinic [pom.xml](pom.xml). Click on the `Open` button.

    - CSS files are generated from the Maven build. You can build them on the command line `./mvnw generate-resources` or right-click on the `spring-petclinic` project then `Maven -> Generates sources and Update Folders`.

    - A run configuration named `PetClinicApplication` should have been created for you if you're using a recent Ultimate version. Otherwise, run the application by right-clicking on the `PetClinicApplication` main class and choosing `Run 'PetClinicApplication'`.

1. Navigate to the Petclinic

    Visit [http://localhost:8080](http://localhost:8080) in your browser.

## Looking for something in particular?

|Spring Boot Configuration | Class or Java property files  |
|--------------------------|---|
|The Main Class | [PetClinicApplication](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/java/org/springframework/samples/petclinic/PetClinicApplication.java) |
|Properties Files | [application.properties](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources) |
|Caching | [CacheConfiguration](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/java/org/springframework/samples/petclinic/system/CacheConfiguration.java) |

## Interesting Spring Petclinic branches and forks

The Spring Petclinic "main" branch in the [spring-projects](https://github.com/spring-projects/spring-petclinic)
GitHub org is the "canonical" implementation based on Spring Boot and Thymeleaf. There are
[quite a few forks](https://spring-petclinic.github.io/docs/forks.html) in the GitHub org
[spring-petclinic](https://github.com/spring-petclinic). If you are interested in using a different technology stack to implement the Pet Clinic, please join the community there.

## Interaction with other open-source projects

One of the best parts about working on the Spring Petclinic application is that we have the opportunity to work in direct contact with many Open Source projects. We found bugs/suggested improvements on various topics such as Spring, Spring Data, Bean Validation and even Eclipse! In many cases, they've been fixed/implemented in just a few days.
Here is a list of them:

| Name | Issue |
|------|-------|
| Spring JDBC: simplify usage of NamedParameterJdbcTemplate | [SPR-10256](https://github.com/spring-projects/spring-framework/issues/14889) and [SPR-10257](https://github.com/spring-projects/spring-framework/issues/14890) |
| Bean Validation / Hibernate Validator: simplify Maven dependencies and backward compatibility |[HV-790](https://hibernate.atlassian.net/browse/HV-790) and [HV-792](https://hibernate.atlassian.net/browse/HV-792) |
| Spring Data: provide more flexibility when working with JPQL queries | [DATAJPA-292](https://github.com/spring-projects/spring-data-jpa/issues/704) |

## Contributing

The [issue tracker](https://github.com/spring-projects/spring-petclinic/issues) is the preferred channel for bug reports, feature requests and submitting pull requests.

For pull requests, editor preferences are available in the [editor config](.editorconfig) for easy use in common text editors. Read more and download plugins at <https://editorconfig.org>. All commits must include a __Signed-off-by__ trailer at the end of each commit message to indicate that the contributor agrees to the Developer Certificate of Origin.
For additional details, please refer to the blog post [Hello DCO, Goodbye CLA: Simplifying Contributions to Spring](https://spring.io/blog/2025/01/06/hello-dco-goodbye-cla-simplifying-contributions-to-spring).

## License

The Spring PetClinic sample application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
