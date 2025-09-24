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
 * E2E tests for Find Owners functionality Tests search functionality, validation, and
 * navigation
 */
@DisplayName("Find Owners E2E Tests")
class FindOwnersE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to search for owners by last name, so that I can find specific pet owners")
	void shouldSearchOwnersByLastName() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches for owners with last name "Davis"
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should see search results with Davis owners
		assertTrue(page.url().contains("/owners?lastName=Davis"), "Should be on results page with search parameter");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should show owners results heading");
		assertTrue(page.locator("table").isVisible(), "Should display results table");
		assertTrue(page.locator("a:has-text('Davis')").count() > 0, "Should show Davis owners in results");

		// And - should be able to click on owner to view details
		page.locator("a:has-text('Betty Davis')").first().click();
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should navigate to owner details page");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner details");
	}

	@Test
	@DisplayName("As a user, I want to search with partial last names, so that I can find owners even with incomplete information")
	void shouldSearchWithPartialLastName() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches with partial last name "Fra"
		page.locator("input#lastName").fill("Fra");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should find owners with last names containing "Fra"
		assertTrue(page.url().contains("/owners?lastName=Fra"), "Should have search parameter");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should show results heading");
		assertTrue(page.locator("a:has-text('Franklin')").count() > 0, "Should find Franklin owners");
	}

	@Test
	@DisplayName("As a user, I want to search without entering a last name, so that I can see all owners")
	void shouldShowAllOwnersWithEmptySearch() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches without entering a last name
		page.locator("input#lastName").fill("");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should show all owners
		assertTrue(page.url().contains("/owners?lastName="), "Should have empty search parameter");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should show results heading");
		assertTrue(page.locator("table").isVisible(), "Should display results table");
		assertTrue(page.locator("table tbody tr").count() > 0, "Should show multiple owners");
	}

	@Test
	@DisplayName("As a user, I want to search for non-existent owners, so that I can see appropriate no results message")
	void shouldShowNoResultsForNonExistentOwner() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches for a non-existent owner
		page.locator("input#lastName").fill("NonExistentOwner12345");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should show no results message
		assertTrue(page.url().contains("/owners?lastName=NonExistentOwner12345"), "Should have search parameter");
		assertTrue(page.locator("text=has not been found").isVisible(), "Should show not found message");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Add Owner from Find Owners, so that I can create new owners")
	void shouldNavigateToAddOwnerFromFindOwners() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user clicks Add Owner link
		page.locator("a:has-text('Add Owner')").click();

		// Then - should navigate to Add Owner form
		assertTrue(page.url().contains("/owners/new"), "Should be on add owner page");
		assertTrue(page.locator("h2:has-text('Owner')").isVisible(), "Should show owner form heading");
		assertTrue(page.locator("input[id*='firstName']").isVisible(), "Should show first name field");
		assertTrue(page.locator("input[id*='lastName']").isVisible(), "Should show last name field");
		assertTrue(page.locator("button:has-text('Add Owner')").isVisible(), "Should show add owner button");
	}

	@Test
	@DisplayName("As a user, I want to search with special characters, so that the system handles edge cases gracefully")
	void shouldHandleSpecialCharactersInSearch() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches with special characters
		page.locator("input#lastName").fill("O'Connor-Smith");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should handle the search without errors
		assertTrue(page.url().contains("/owners?lastName="), "Should process search parameter");
		// Should not crash or show error page
		assertFalse(page.locator("h2:has-text('Something happened')").isVisible(), "Should not show error page");
	}

	@Test
	@DisplayName("As a user, I want to use browser back button from search results, so that I can return to search form")
	void shouldHandleBrowserBackButtonFromResults() {
		// Given - user has performed a search and is on results page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should be on results page");

		// When - user clicks browser back button
		page.goBack();

		// Then - should return to find owners form
		assertTrue(page.url().contains("/owners/find"), "Should be back on find owners page");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should show find owners heading");
		// The search field should retain the previous value
		assertEquals("Davis", page.locator("input#lastName").inputValue(), "Should retain search value");
	}

	@Test
	@DisplayName("As a user, I want to search case-insensitively, so that I can find owners regardless of case")
	void shouldPerformCaseInsensitiveSearch() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches with different case variations
		page.locator("input#lastName").fill("davis");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should still find Davis owners
		assertTrue(page.url().contains("/owners?lastName=davis"), "Should have lowercase search parameter");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should show results");
		assertTrue(page.locator("a:has-text('Davis')").count() > 0, "Should find Davis owners regardless of case");
	}

	@Test
	@DisplayName("As a user, I want to search and view owner details efficiently, so that I can quickly access owner information")
	void shouldEnableEfficientOwnerDetailsAccess() {
		// Given - user is on Find Owners page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();

		// When - user searches and clicks on first result
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();

		// Then - should see owner details with complete information
		assertTrue(page.locator("table").isVisible(), "Should show results table");

		// When - user clicks on first owner
		String firstOwnerName = page.locator("table tbody tr").first().locator("td a").first().textContent();
		page.locator("table tbody tr").first().locator("td a").first().click();

		// Then - should see detailed owner information
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should be on owner details page");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner info heading");
		assertTrue(page.locator("table").isVisible(), "Should show owner details table");
		assertTrue(page.locator("td").first().textContent().contains(firstOwnerName),
				"Should show correct owner details");
	}

}