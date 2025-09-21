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

This project includes comprehensive end-to-end tests using [Playwright](https://playwright.dev/) that cover all user flows, error scenarios, and edge cases.

### Prerequisites for E2E Testing

- Node.js 18 or newer
- npm or yarn package manager
- All browsers will be installed automatically by Playwright

### Running E2E Tests

#### With Maven

```bash
# Run all tests including E2E tests
./mvnw verify

# Run only E2E tests
./mvnw frontend:npm@"playwright test" -DskipTests

# Skip E2E tests during build
./mvnw verify -DskipTests
```

#### With Gradle

```bash
# Run all tests including E2E tests
./gradlew test

# Run only E2E tests
./gradlew playwrightTest

# Run E2E tests with alternative task
./gradlew e2eTest

# Skip E2E tests during build
./gradlew build -PskipE2ETests
```

#### With npm directly

```bash
# Install dependencies
npm install

# Install Playwright browsers
npx playwright install

# Run all E2E tests
npm test
# or
npm run test:e2e

# Run tests in headed mode (with browser UI)
npm run test:headed

# Run tests in debug mode
npm run test:debug

# View test report
npm run test:report
```

### E2E Test Coverage

The Playwright test suite covers:

- **Navigation and Welcome Page**: Home page, menu navigation, responsive design
- **Owner Management**: Create, find, edit, view owner details, form validation
- **Pet Management**: Add pets, edit pet details, pet type selection, validation
- **Visit Management**: Schedule visits, view history, form validation
- **Veterinarian Features**: View vet list, pagination, specialties
- **Error Handling**: 404 pages, form validation errors, server errors
- **Responsive Design**: Mobile, tablet, and desktop viewports
- **Accessibility**: Keyboard navigation, ARIA labels, screen reader compatibility
- **Cross-browser**: Chrome, Firefox, and mobile browsers

### Test Structure

```
e2e-tests/
├── pages/              # Page Object Model classes
│   ├── BasePage.ts     # Common page functionality
│   ├── WelcomePage.ts  # Home page interactions
│   ├── OwnerPage.ts    # Owner management
│   ├── PetPage.ts      # Pet management
│   ├── VisitPage.ts    # Visit scheduling
│   └── VetPage.ts      # Veterinarian listings
├── welcome.spec.ts     # Welcome page and navigation tests
├── owner.spec.ts       # Owner CRUD operations tests
├── pet.spec.ts         # Pet management tests
├── visit.spec.ts       # Visit scheduling tests
├── vet.spec.ts         # Veterinarian viewing tests
├── error-handling.spec.ts    # Error scenarios and edge cases
├── accessibility.spec.ts     # Accessibility compliance tests
└── responsive.spec.ts        # Responsive design tests
```

### Browser Configuration

Tests run on multiple browsers by default:
- **Chromium** (Chrome-based browsers)
- **Firefox**
- **Mobile Chrome** (mobile viewport simulation)

### CI/CD Integration

E2E tests are automatically run in GitHub Actions for both Maven and Gradle builds. Test reports and videos are uploaded as artifacts when tests fail.

### Troubleshooting E2E Tests

#### Common Issues

1. **Browser Installation Fails**
   ```bash
   # Install with system dependencies
   npx playwright install --with-deps
   
   # Or install specific browser
   npx playwright install chromium
   ```

2. **Tests Fail in Headless Mode**
   ```bash
   # Run in headed mode to see what's happening
   npm run test:headed
   
   # Run with debug mode
   npm run test:debug
   ```

3. **Application Not Starting**
   - Ensure Spring Boot application is running on port 8080
   - Check that no other process is using port 8080
   - Verify database connectivity

4. **Flaky Tests**
   - Tests include proper wait strategies and retry mechanisms
   - Check network connectivity and system performance
   - Review test logs and screenshots in `test-results/` directory

5. **Permission Issues**
   ```bash
   # On Linux/Mac, ensure execute permissions
   chmod +x ./mvnw ./gradlew
   
   # Install browsers with system packages
   sudo npx playwright install-deps
   ```

#### Debug Mode

Run tests in debug mode to step through them interactively:

```bash
npm run test:debug
```

This opens the Playwright Inspector where you can:
- Step through tests line by line
- Inspect page elements
- View console logs and network activity
- Generate and test selectors

#### Test Reports

After running tests, view the HTML report:

```bash
npm run test:report
```

This provides:
- Detailed test results with screenshots
- Error traces and logs
- Performance metrics
- Video recordings of failed tests

### Accessibility Testing

The test suite includes comprehensive accessibility checks:
- Keyboard navigation support
- ARIA label validation
- Color contrast verification
- Screen reader compatibility
- Focus management

Run accessibility tests specifically:
```bash
npx playwright test accessibility.spec.ts
```

### Performance Testing

E2E tests include basic performance validations:
- Page load times
- Navigation responsiveness  
- Form submission speed

For detailed performance analysis, use browser developer tools during headed test runs.

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
