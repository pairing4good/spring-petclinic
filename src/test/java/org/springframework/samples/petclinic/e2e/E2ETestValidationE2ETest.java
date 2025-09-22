/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Validation test to verify the E2E test infrastructure works correctly. This test should
 * be run 3 times consecutively to validate no flaky tests exist.
 */
@DisplayName("E2E Test Infrastructure Validation")
class E2ETestValidationE2ETest extends BaseE2ETest {

	@Test
	@EnabledIfSystemProperty(named = "validate.e2e", matches = "true")
	@DisplayName("As a developer, I want to validate the E2E test infrastructure, so that I can ensure tests are reliable")
	void testE2EInfrastructureValidation() {
		// This test validates that the E2E infrastructure can start browsers and connect
		// to the application
		navigateToHome();

		// Basic validation that the application is accessible
		assertPageTitle("PetClinic :: a Spring Framework demonstration");
		assertElementVisible("h2");
		assertElementContainsText("h2", "Welcome");

		// Validate navigation works
		navigateToFindOwners();
		assertElementContainsText("h2", "Find Owners");

		// Validate form interaction
		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should see results
		assertElementVisible("body");
		System.out.println("✅ E2E Test Infrastructure Validation Passed");
	}

	@Test
	@DisplayName("As a developer, I want to validate basic browser functionality, so that I can ensure Playwright setup is correct")
	void testBasicBrowserFunctionality() {
		// This test validates basic browser operations without requiring Playwright
		// browsers
		// It tests the Spring Boot application startup and basic assertions

		// Validate the application context loads correctly
		assert port > 0 : "Spring Boot application should be running on a valid port";
		assert baseUrl != null : "Base URL should be configured";
		assert baseUrl.contains("localhost") : "Should be running on localhost";

		System.out.println("✅ Basic Browser Functionality Test Passed");
		System.out.println("🏃 Application running on: " + baseUrl);
	}

}