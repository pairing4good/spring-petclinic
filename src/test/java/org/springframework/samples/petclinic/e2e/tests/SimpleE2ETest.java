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

package org.springframework.samples.petclinic.e2e.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple E2E test to validate Playwright setup
 */
@DisplayName("Simple E2E Test")
class SimpleE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to access the PetClinic home page, so that I can use the application")
	void shouldLoadHomePage() {
		// Given - user navigates to the application
		page.navigate(baseUrl);

		// When - the page loads
		page.waitForLoadState();

		// Then - the page should be accessible
		assertTrue(page.url().contains("localhost:" + port), "Should be on the correct URL");
		assertEquals("PetClinic :: a Spring Framework demonstration", page.title(), "Should have correct title");

		// And - welcome content should be visible
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Welcome heading should be visible");
		assertTrue(page.locator("img").first().isVisible(), "Pet image should be visible");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners, so that I can search for pet owners")
	void shouldNavigateToFindOwners() {
		// Given - user is on the home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user clicks Find Owners link
		page.locator("a:has-text('Find Owners')").click();

		// Then - should be on Find Owners page
		assertTrue(page.url().contains("/owners/find"), "Should be on Find Owners page");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Find Owners heading should be visible");
		assertTrue(page.locator("input#lastName").isVisible(), "Last name input should be visible");
	}

	@Test
	@DisplayName("As a user, I want to search for owners, so that I can find specific pet owners")
	void shouldSearchForOwners() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches for owners with last name "Davis"
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should see search results
		assertTrue(page.url().contains("/owners?lastName=Davis"), "Should be on results page");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Owners heading should be visible");
		assertTrue(page.locator("table").isVisible(), "Results table should be visible");
		assertTrue(page.locator("a:has-text('Davis')").count() > 0, "Should show Davis owners");
	}

	@Test
	@DisplayName("As a user, I want to view veterinarians, so that I can see available vets")
	void shouldViewVeterinarians() {
		// Given - user is on the home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user clicks Veterinarians link
		page.locator("a:has-text('Veterinarians')").click();

		// Then - should see veterinarians page
		assertTrue(page.url().contains("/vets.html"), "Should be on veterinarians page");
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Veterinarians heading should be visible");
		assertTrue(page.locator("table").isVisible(), "Veterinarians table should be visible");
		assertTrue(page.locator("td:has-text('James Carter')").isVisible(), "Should show veterinarian names");
	}

	@Test
	@DisplayName("As a user, I want to see error handling, so that errors are displayed appropriately")
	void shouldDisplayErrorPage() {
		// Given - user navigates to error page
		page.navigate(baseUrl + "/oups");

		// When - the error page loads
		page.waitForLoadState();

		// Then - should show error content
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Error heading should be visible");
		assertTrue(page.locator("img").first().isVisible(), "Error image should be visible");
	}

}