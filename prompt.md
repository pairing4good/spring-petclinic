# Implement Comprehensive Playwright End-to-End Tests

## Overview
Implement a complete suite of Playwright end-to-end tests that cover all user flows, error scenarios, and edge cases for this web application. The goal is to achieve comprehensive test coverage that ensures application reliability and user experience quality.

## User Story
**As a** development team  
**I want** comprehensive end-to-end tests using Playwright  
**So that** we can confidently deploy changes knowing all user flows work correctly and catch regressions early

## Requirements
### 1. Test Implementation
- [ ] **FIRST: Create a simple temporary E2E test** to verify Playwright configuration and basic functionality (e.g., navigate to homepage, verify page title)
- [ ] **Run and fix this temporary test** using the same 10-attempt process described in Section 8
- [ ] **MANDATORY: Once temporary test passes and configuration is verified, DELETE the temporary test**
- [ ] **Then proceed with creating all persona flow tests** as described below
- [ ] Write comprehensive Playwright tests covering ALL user flows in the application
- [ ] **Create a separate test file/class for each user flow** to ensure comprehensive coverage per persona/customer type
- [ ] Cover ALL accessible error pages and error states that can be triggered through web navigation (404 pages, form validation errors, authentication errors, authorization errors, etc.)
- [ ] Cover ALL edge cases (empty states, maximum inputs, special characters, etc.)
- [ ] Focus on testing error scenarios that are accessible through the web interface - do not attempt to test server-side failures that cannot be simulated through normal web navigation
- [ ] Use descriptive test names following the format: "As a [user type], I want [action], so that [outcome]"
- [ ] **CRITICAL**: Write ALL test cases - do NOT add TODO comments for unimplemented tests
- [ ] **MANDATORY: Immediately after writing each test file/class, run the entire test file/class to verify ALL tests pass**
- [ ] **If any tests in the file/class fail, fix them using the 10-attempt process (described in Section 8) before proceeding to write the next test file/class**
- [ ] **Continue this pattern for every single test file/class created - no test file should be left unvalidated**
- [ ] Ensure test isolation - each test should be independent and not rely on others
- [ ] Include both positive and negative test scenarios
- [ ] Test all interactive elements (buttons, forms, navigation, modals, etc.)
- [ ] **Use specific, unique locators**: Prefer `data-testid`, unique text, or specific CSS selectors over generic ones
- [ ] **Avoid ambiguous locators**: Never use locators that could match multiple elements (e.g., `page.locator('button')`)
- [ ] **Implement locator disambiguation strategies**:
  - Use `nth()` with descriptive context: `page.locator('[data-testid="user-card"]').nth(0) // first user in list`
  - Use `filter()` with specific text: `page.locator('button').filter({ hasText: 'Submit Order' })`
  - Use parent-child relationships: `page.locator('[data-testid="checkout-form"]').locator('button[type="submit"]')`
  - Use `getByRole()` with accessible names: `page.getByRole('button', { name: 'Add to Cart' })`
- [ ] **Add descriptive comments** for complex locators explaining why specific disambiguation was chosen

### 2. Test Coverage Areas (Must Include All)
- [ ] User authentication flows (login, logout, registration, password reset)
- [ ] Navigation and routing (all pages, breadcrumbs, back/forward)
- [ ] Form submissions (validation, success, error handling)
- [ ] Search functionality (if applicable)
- [ ] CRUD operations (create, read, update, delete)
- [ ] File upload/download (if applicable)
- [ ] Responsive design on different viewport sizes
- [ ] Browser back/forward button behavior
- [ ] Error pages and states accessible through web navigation (404 pages, form validation errors, authentication/authorization errors)
- [ ] Client-side error handling (network timeouts, invalid responses where simulatable)
- [ ] Loading states and spinners
- [ ] Empty states and placeholder content
- [ ] Accessibility features (keyboard navigation, ARIA labels)
- [ ] Cross-browser compatibility (Chrome, Firefox, Safari)

### 3. Technical Implementation
- [ ] **FIRST STEP: Create simple temporary test for configuration validation - do NOT implement Page Object Model yet**
- [ ] **MANDATORY: Implement proper Page Object Model** - This is REQUIRED for maintainable tests (following language-specific patterns) **AFTER** temporary test validation
- [ ] **MANDATORY: Use the same language and framework as the existing project**:
  - **Java projects**: Use Playwright for Java with existing build tools (Maven/Gradle)
  - **JavaScript/TypeScript projects**: Use Playwright for JavaScript/TypeScript with existing tools (npm/yarn)
  - **C# projects**: Use Playwright for .NET with existing build tools
  - **Python projects**: Use Playwright for Python with existing tools
  - **Other languages**: Use the appropriate Playwright bindings for the project's language
- [ ] Add Playwright as a dependency using the project's build framework (npm/yarn/maven/gradle)
- [ ] Configure Playwright with appropriate browsers and settings
- [ ] **Browser Download Optimization**: Consider using `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1` in CI environments if browsers are pre-installed, but ensure browsers are available through other means (Docker images with browsers, system packages, or separate installation steps)
- [ ] Configure Playwright's built-in retry mechanism following best practices:
  - Set `retries: 2` for CI environments
  - Set `retries: 0` for local development
  - Use proper wait strategies instead of relying on retries
- [ ] Set up proper test data management (seeding, cleanup)
- [ ] Configure test environments (local, CI/CD)
- [ ] Implement proper wait strategies (avoid flaky tests)
- [ ] Set up test reporting with detailed results
- [ ] Configure parallel test execution where appropriate
- [ ] Ensure tests work with existing authentication/authorization
- [ ] **CRITICAL**: Minimize impact on application code - focus on test files, build configurations, and CI/CD workflows

### 4. Documentation Updates
- [ ] Update `README.md` with comprehensive test running instructions following the project's existing documentation style:
  - How to install dependencies using the project's existing build system (npm/yarn/maven/gradle/etc.)
  - How to run tests locally using the project's existing commands (`npm test`, `yarn test`, `mvn test`, `gradle test`, etc.)
  - How to run specific test suites using the project's patterns
  - How to run tests in different browsers
  - How to generate test reports
  - Troubleshooting common issues
  - **Follow the project's existing documentation format and style**

### 5. CI/CD Integration
- [ ] Update ALL existing GitHub Actions workflows (.github/workflows/[name].yml) to include Playwright tests.
  - **Use the following comprehensive workflow template as a reference for proper Playwright setup and installation**:
  ```yaml
  name: # The name of this workflow
  
  on:
    push:
      branches: [ main, master ]
    pull_request:
      branches: [ main, master ]
  
  jobs:
    test:
      runs-on: ubuntu-latest
      steps:
      - uses: actions/checkout@v4
  
      # Setup your language and framework (Node.js, Java, Python, etc.)
      
      # Install your project dependencies (npm ci, mvn compile, pip install, etc.)
      
      # Build your application if needed (npm run build, mvn package, gradle build, etc.)
  
      # Install Playwright browsers - This is the key step!
      - name: Install Playwright Browsers
        run: npx playwright install --with-deps
  
      # Run your Playwright tests using your language's test runner
      # Examples:
      # npm test, npx playwright test
      # mvn test, gradle test  
      # python -m pytest, dotnet test
  
      # Upload test results
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: playwright-report
          path: playwright-report/
          retention-days: 30
  
      # Upload test results for failed tests
      - uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: playwright-videos
          path: test-results/
          retention-days: 30
  ```
- [ ] If multiple build systems exist (e.g., npm AND yarn, or maven AND gradle), update ALL of them
- [ ] **CRITICAL**: Ensure build fails immediately if ANY Playwright test fails
- [ ] Configure test execution with standard fail-fast behavior (build stops on first test failure)
- [ ] Set up test result reporting in CI/CD
- [ ] Ensure existing builds continue to work without breaking changes
- [ ] **Gradle Projects**: Ensure `gradle test` or `./gradlew test` commands exit with non-zero status code when any test fails
- [ ] **Maven Projects**: Ensure `mvn test` exits with non-zero status code when any test fails
- [ ] **npm/yarn Projects**: Ensure test commands exit with non-zero status code when any test fails

### 6. Build System Integration
- [ ] **Use the project's existing build system and follow established patterns**
- [ ] Integrate Playwright into the project's existing build framework properly
- [ ] Ensure the standard test command includes Playwright tests using the project's existing build system:
  - **npm projects**: `npm run test` or `npm test`
  - **yarn projects**: `yarn test`
  - **Maven projects**: `mvn test` or `mvn verify`
  - **Gradle projects**: `gradle test` or `./gradlew test`
  - **Other build systems**: Follow the project's existing test execution patterns
- [ ] Verify all existing build commands continue to work
- [ ] Add separate command for running only Playwright tests following project conventions:
  - **npm projects**: `npm run test:e2e` or `npm run playwright`
  - **yarn projects**: `yarn test:e2e` or `yarn playwright`
  - **Maven projects**: `mvn test -Dtest=**/*E2E*` or custom profile like `mvn test -Pe2e`
  - **Gradle projects**: `gradle playwrightTest` or `./gradlew e2eTest`
  - **Other build systems**: Follow the project's naming conventions for test types
- [ ] Configure proper test timeouts and retries using Playwright's built-in retry mechanisms (recommended: 1-2 retries in CI, 0 retries locally)
- [ ] **CRITICAL**: Minimize impact on application code - focus on test files, build configurations, and CI/CD workflows

### 7. Production Code Impact Guidelines
- [ ] **Minimize changes to application source code** - this is primarily a testing infrastructure task
- [ ] **Acceptable production code changes** (minimal and only if necessary):
  - Adding `data-testid` attributes to elements for reliable test selectors
  - Adding test-specific CSS classes (e.g., `test-submit-button`)
  - Minor accessibility improvements that also benefit testing (e.g., proper ARIA labels)
- [ ] **Prohibited production code changes**:
  - No modifications to business logic or application functionality
  - No changes to API endpoints or data models
  - No alterations to user interface behavior or styling
  - No modifications to authentication/authorization logic
- [ ] **Focus areas for this task**:
  - Test files using the project's existing language and conventions:
    - **JavaScript/TypeScript**: `.spec.js`, `.test.js`, `.spec.ts`, `.test.ts`
    - **Java**: `*Test.java`, `*E2ETest.java`, `*IT.java`
    - **C#**: `*Tests.cs`, `*E2ETests.cs`
    - **Python**: `*_test.py`, `test_*.py`
    - **Other**: Follow the project's existing test file naming conventions
  - Build configuration files (`package.json`, `pom.xml`, `build.gradle`, etc.)
  - CI/CD workflow files (`.github/workflows/`, `.gitlab-ci.yml`, etc.)
  - Documentation files (`README.md`, test documentation)
  - Test configuration files (`playwright.config.js`, `playwright.config.ts`, etc.)



### 8. **CRITICAL** Agent Test Validation Process
- [ ] **MANDATORY: Agent must run each individual test immediately after writing it** to verify it passes
- [ ] **If any test fails, agent MUST fix the test before proceeding to write the next test**
- [ ] **Agent must attempt to fix failing tests up to 10 times per test**:
  - Attempt 1-3: Fix obvious issues (locators, wait conditions, test data)
  - Attempt 4-6: Refactor test approach or test structure
  - Attempt 7-9: Try alternative locator strategies or test methods
  - Attempt 10: Final comprehensive fix attempt
- [ ] **If a test cannot be fixed after 10 attempts, skip the test using the appropriate method for the project's language/framework and add detailed comment explaining why**:
  - **JavaScript/TypeScript**: `test.skip()` or `it.skip()`
  - **Java**: `@Disabled` annotation or `Assumptions.assumeTrue(false)`
  - **C#**: `[Ignore]` attribute or `Assert.Inconclusive()`
  - **Python**: `@pytest.mark.skip` or `unittest.skip()`
  - **Other frameworks**: Use the standard skip/ignore mechanism for the project's testing framework
- [ ] **CRITICAL: No test should be left in a failing state - all tests must either pass or be explicitly skipped**
- [ ] **Agent must identify and run the test commands found in EACH GitHub Actions workflow 3 consecutive times** (extract the actual test commands from each workflow file and run them locally, do NOT attempt to execute the workflows themselves)
- [ ] **If there are multiple workflows with tests, agent must validate each workflow's commands separately**: Extract and run commands from workflow 1 three times consecutively, then extract and run commands from workflow 2 three times consecutively, etc.
- [ ] **If any workflow's test commands fail during any of the 3 validation runs, agent MUST fix the tests, builds, and/or workflows using the 10-attempt process, then restart the 3-run validation for ALL workflow commands from the beginning**
- [ ] **This validation process is ONLY for the agent** - do NOT implement this in code, build scripts, or CI/CD
- [ ] If any test fails during final validation, agent must fix the flaky test and restart the 3-run validation
- [ ] All 3 final runs must have 100% pass rate (excluding explicitly skipped tests) for EVERY workflow before marking issue complete

### 9. Build System Failure Handling
- [ ] **This is the LAST step before considering the issue complete - no exceptions**
- [ ] **MANDATORY: Identify ALL test commands found in EACH GitHub Actions workflow file**
- [ ] **For EACH test command identified, run that exact command 3 consecutive times to verify it passes consistently**
- [ ] **If multiple workflows exist with different test commands, validate EACH command separately**:
  - Extract test command from workflow 1 → run it 3 times consecutively
  - Extract test command from workflow 2 → run it 3 times consecutively
  - Continue for all workflows with test commands
- [ ] **If ANY test command fails during ANY of the 3 runs, fix the failing tests using the 10-attempt process, then restart the 3-run validation for ALL test commands from the beginning**
- [ ] **This step must be repeated after ANY changes are made to tests, workflows, or build configurations**
- [ ] **All test commands from all workflows must pass 3 consecutive times each before the issue can be marked complete**
- [ ] **CRITICAL: This is the final validation that ensures all build script test commands are green and reliable**
- [ ] **Gradle Projects**: 
  - Verify that `test` task fails the build when any test fails (default Gradle behavior)
  - If using custom test tasks, ensure they propagate test failures correctly
  - Add explicit validation: `gradle.taskGraph.useFilter { !it.name.contains('test') || it.state.failure == null }`
  - Ensure no `continueOnFailure = true` or similar configurations that suppress test failures
- [ ] **Maven Projects**: 
  - Ensure `<testFailureIgnore>false</testFailureIgnore>` (default)
  - Verify Surefire/Failsafe plugins fail the build on test failures
### 10. **FINAL MANDATORY STEP** - Complete Build Validation - **THIS CANNOT BE SKIPPED!!!!!**
- [ ] **This MUST BE THE LAST STEP - before considering the issue complete - no exceptions**
- [ ] **MANDATORY: Identify and run ALL commands found in EACH GitHub Actions workflow (not just test commands)**:
  - Build commands (e.g., `./gradlew build`, `./mvnw -B verify`, `npm run build`)
  - Test commands (e.g., `./gradlew test`, `mvn test`, `npm test`)
  - Any other commands that could cause build failure
- [ ] **For EACH command identified, run that exact command 3 consecutive times to verify it passes consistently**
- [ ] **If multiple workflows exist with different commands, validate EACH command separately**:
  - Extract all commands from workflow 1 → run each command 1 (2,3,...) → fix any failures or errors → run the same commands a total of 3 times with no failures or errors 
  - Extract all commands from workflow 2 → run each command 1 (2,3,...) → fix any failures or errors → run the same commands a total of 3 times with no failures or errors 
- [ ] **If ANY command fails during ANY of the 3 runs, fix the issues (formatting, tests, configuration, etc.) using the 10-attempt process, then restart the 3-run validation for ALL commands from ALL workflows from the beginning**
- [ ] **This step must be repeated after ANY changes are made to tests, workflows, build configurations, or source code**
- [ ] **All commands from all workflows must pass 3 consecutive times each before the issue can be marked complete**
- [ ] **MANDATORY AND CRITICAL - ALL BUILD COMMANDS MUST RUN SUCCESSFULLY BEFORE THE SESSION IS COMPLETE AND THE PR IS READY FOR REVIEW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: Running the commands from all workflows validates that the entire build process works correctly, not just the tests - formatting, compilation, testing, and any other build steps must all succeed**

## Acceptance Criteria

### Critical Success Requirements
1. **Complete Test Coverage**: Every user flow, accessible error scenario, and edge case must have corresponding Playwright tests
2. **All Tests Written**: No TODO comments or placeholder tests - all functionality must be tested
3. **MANDATORY Page Object Model**: Proper Page Object Model implementation is REQUIRED - this is not optional
4. **Separate Test Files per Flow**: Each user flow must have its own dedicated test file/class for comprehensive coverage
4. **Element Disambiguation**: All Playwright locators must be specific and unambiguous, with proper strategies to handle elements that could match multiple results
5. **CRITICAL - Individual Test Validation**: The agent MUST run each test individually after writing it and fix any failures (up to 10 attempts per test) before proceeding. Tests that cannot be fixed after 10 attempts must be skipped with detailed comments.
6. **MANDATORY - Test File/Class Validation**: The agent MUST run each complete test file/class immediately after writing it to verify ALL tests in that file pass before proceeding to write the next test file/class.
7. **Agent Final Validation**: The agent must identify and run ALL commands found in EACH GitHub Actions workflow 3 consecutive times (extract all commands from each workflow file and run them locally, do NOT attempt to execute the workflows themselves). This includes build commands, test commands, and any other commands that could cause build failure. If there are multiple workflows, validate each workflow's commands separately. **IMPORTANT**: This is ONLY for the agent's final verification - do NOT implement this as part of the test suite, build scripts, or GitHub Actions
6. **100% Pass Rate**: All tests must pass in all 3 validation runs before considering the issue complete
7. **Flaky Test Resolution**: If any tests fail during the 3-run validation, the agent must fix the flaky tests and then re-run the 3-time validation process until all tests pass consistently
8. **CRITICAL - No Failing Tests**: Every individual test must either pass or be explicitly skipped after 10 fix attempts - no tests should remain in failing state
8. **Red CI/CD on Failure**: GitHub Actions workflows must fail (show red status) immediately when any Playwright test fails
9. **Green CI/CD on Success**: All GitHub Actions workflows must be green only when all tests pass
10. **Build Integrity**: All existing build processes must continue to work without issues

### Quality Standards
- [ ] Tests must be readable and follow "As a [user], I want [action], so that [outcome]" naming convention
- [ ] Tests must be maintainable with clear comments and logical organization
- [ ] Tests must be reliable (no flaky tests allowed)
- [ ] Test execution must be reasonably fast (optimize for efficiency)
- [ ] Error messages must be clear and helpful for debugging
- [ ] Locators must be reliable and specific, avoiding ambiguous element selection

### Additional Requirements
- [ ] Include visual regression testing where appropriate
- [ ] Test mobile responsiveness on different device sizes
- [ ] Verify accessibility compliance (basic WCAG checks)
- [ ] Test performance implications (no major slowdowns from test setup)
- [ ] Ensure proper cleanup after tests (no data pollution)

## Definition of Done
This issue is considered complete ONLY when:

1. ✅ All user flows have comprehensive Playwright tests
2. ✅ All accessible error scenarios and edge cases are tested
3. ✅ **Page Object Model implemented**: Proper Page Object Model structure is in place following language-specific patterns
4. ✅ Each user flow has its own dedicated test file/class
4. ✅ All Playwright locators are properly disambiguated and avoid multi-element matches
5. ✅ **CRITICAL**: Each individual test has been run and verified to pass (or explicitly skipped after 10 fix attempts)
6. ✅ **MANDATORY**: Each test file/class has been run in its entirety and verified to pass before proceeding to next test file/class
7. ✅ All commands from each GitHub Actions workflow have been identified and run 3 times consecutively by the agent with 100% pass rate for every workflow's commands (validation step only - not implemented in code)
7. ✅ README.md is updated with clear testing instructions
8. ✅ All existing build processes continue to work
9. ✅ Playwright tests use the same language and build tools as the existing project
10. ✅ Minimal impact on production code - changes limited to test enablement only
11. ✅ Test execution reports all test results but fails build on any test failure
12. ✅ No TODO comments exist in test files
13. ✅ Tests follow the required naming convention
14. ✅ Cross-browser testing is configured and working

## Technical Notes
- **CRITICAL**: Use the same programming language and framework as the existing project - do not introduce new languages or build tools
- **MANDATORY**: Implement proper Page Object Model - this is REQUIRED for maintainable and scalable tests (following language-specific patterns)
- Use Playwright's best practices for test structure and organization within the project's language
- Use data-testid attributes for reliable element selection
- **Always use the most specific locator possible** and document disambiguation strategies
- Configure appropriate timeouts and retries for stability
- Set up proper test environments and data management using the project's existing patterns
- Consider using Playwright's built-in reporters for detailed test results
- **Important**: Build must fail immediately when any test fails - do NOT configure fail-safe or continue-on-error behaviors
- **Playwright Retry Best Practices**: 
  - Configure `retries: 2` for CI environments in playwright.config.js (or language equivalent)
  - Configure `retries: 0` for local development so developers see failures immediately
  - Use proper wait strategies (`waitForSelector`, `waitForLoadState`) rather than relying heavily on retries
  - Set appropriate timeouts for different types of operations
- **GitHub Actions Failure Handling**: 
  - Do NOT use `continue-on-error: true` for test steps
  - Do NOT use `|| true` or similar error suppression in test commands
  - Ensure test steps fail the entire workflow when tests fail

---
**Note**: This issue must be completed in its entirety. Partial completion is not acceptable. The agent should not mark this issue as complete until ALL acceptance criteria are met and verified.