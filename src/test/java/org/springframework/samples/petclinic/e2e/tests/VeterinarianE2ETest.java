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
 * E2E tests for Veterinarian functionality Tests veterinarian listing, pagination, and
 * information display
 */
@DisplayName("Veterinarian E2E Tests")
class VeterinarianE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to view the list of veterinarians, so that I can see available vets")
	void shouldDisplayVeterinariansList() {
		// Given - user navigates to veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - should see veterinarians page with proper content
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians heading");
		assertTrue(page.locator("table").isVisible(), "Should show veterinarians table");

		// Should show table headers
		assertTrue(page.locator("th:has-text('Name')").isVisible(), "Should show name column header");
		assertTrue(page.locator("th:has-text('Specialties')").isVisible(), "Should show specialties column header");

		// Should have veterinarian data
		assertTrue(page.locator("table tbody tr").count() > 0, "Should show veterinarian records");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian names and specialties, so that I can choose appropriate care")
	void shouldDisplayVeterinarianNamesAndSpecialties() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - should see specific veterinarians with their specialties
		assertTrue(page.locator("td:has-text('James Carter')").isVisible(), "Should show James Carter");
		assertTrue(page.locator("td:has-text('Helen Leary')").isVisible(), "Should show Helen Leary");
		assertTrue(page.locator("td:has-text('Linda Douglas')").isVisible(), "Should show Linda Douglas");

		// Should show specialties
		assertTrue(page.locator("td:has-text('radiology')").isVisible(), "Should show radiology specialty");
		assertTrue(page.locator("td:has-text('surgery')").isVisible(), "Should show surgery specialty");
		assertTrue(page.locator("td:has-text('dentistry')").isVisible(), "Should show dentistry specialty");

		// Should show vets with no specialties
		assertTrue(page.locator("td:has-text('none')").isVisible(), "Should show vets with no specialties");
	}

	@Test
	@DisplayName("As a user, I want to navigate through multiple pages of veterinarians, so that I can see all available vets")
	void shouldSupportPaginationThroughVeterinarians() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// When - there are multiple pages available
		if (page.locator("a[href*='page=']").count() > 0) {
			// Should see pagination controls
			assertTrue(page.locator("text=pages").isVisible(), "Should show pagination info");

			// Should be able to navigate to next page
			page.locator("a[href*='page=2']").first().click();
			page.waitForLoadState();

			// Then - should be on page 2
			assertTrue(page.url().contains("page=2"), "Should be on page 2");
			assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(),
					"Should still show veterinarians heading");
			assertTrue(page.locator("table").isVisible(), "Should still show veterinarians table");
		}
		else {
			// If no pagination, should still show all veterinarians on single page
			assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians heading");
			assertTrue(page.locator("table tbody tr").count() > 0, "Should show veterinarian records");
		}
	}

	@Test
	@DisplayName("As a user, I want to access veterinarians page from main navigation, so that I can easily find this information")
	void shouldAccessVeterinariansFromMainNavigation() {
		// Given - user is on home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user clicks veterinarians link in navigation
		page.locator("a:has-text('Veterinarians')").click();

		// Then - should navigate to veterinarians page
		assertTrue(page.url().contains("/vets.html"), "Should be on veterinarians page");
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians heading");
		assertTrue(page.locator("table").isVisible(), "Should show veterinarians table");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarians with multiple specialties properly displayed, so that I can understand their expertise")
	void shouldDisplayVeterinariansWithMultipleSpecialties() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - should see veterinarians with multiple specialties
		// Linda Douglas has both dentistry and surgery
		assertTrue(page.locator("td:has-text('Linda Douglas')").isVisible(), "Should show Linda Douglas");

		// Find Linda Douglas's row and check her specialties
		String lindaRow = page.locator("tr")
			.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Linda Douglas"))
			.textContent();
		assertTrue(lindaRow.contains("dentistry"), "Should show dentistry specialty for Linda");
		assertTrue(lindaRow.contains("surgery"), "Should show surgery specialty for Linda");
	}

	@Test
	@DisplayName("As a user, I want to return to home from veterinarians page, so that I can navigate to other sections")
	void shouldReturnToHomeFromVeterinariansPage() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should be on veterinarians page");

		// When - user clicks home link
		page.locator("a:has-text('Home')").click();

		// Then - should return to home page
		assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should be on home page");
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome heading");
	}

	@Test
	@DisplayName("As a user, I want the veterinarians page to load quickly, so that I can access information efficiently")
	void shouldLoadVeterinariansPageQuickly() {
		// Given - user starts timing
		long startTime = System.currentTimeMillis();

		// When - user navigates to veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - page should load within reasonable time
		long loadTime = System.currentTimeMillis() - startTime;
		assertTrue(loadTime < 5000, "Veterinarians page should load within 5 seconds, actual: " + loadTime + "ms");

		// And - content should be displayed
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians heading");
		assertTrue(page.locator("table").isVisible(), "Should show veterinarians table");
	}

	@Test
	@DisplayName("As a user, I want to see consistent table formatting for veterinarians, so that information is easy to read")
	void shouldDisplayConsistentTableFormatting() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - table should have consistent structure
		assertTrue(page.locator("table thead").isVisible(), "Should have table header");
		assertTrue(page.locator("table tbody").isVisible(), "Should have table body");

		// Should have exactly 2 columns (Name and Specialties)
		assertEquals(2, page.locator("table thead th").count(), "Should have 2 table header columns");

		// Each row should have 2 cells
		int rowCount = page.locator("table tbody tr").count();
		assertTrue(rowCount > 0, "Should have at least one veterinarian row");

		for (int i = 0; i < rowCount; i++) {
			assertEquals(2, page.locator("table tbody tr").nth(i).locator("td").count(),
					"Row " + i + " should have exactly 2 cells");
		}
	}

	@Test
	@DisplayName("As a user, I want to verify all veterinarians are displayed with complete information, so that I can make informed decisions")
	void shouldDisplayCompleteVeterinarianInformation() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - each veterinarian row should have name and specialty information
		int rowCount = page.locator("table tbody tr").count();
		assertTrue(rowCount > 0, "Should have veterinarian records");

		for (int i = 0; i < rowCount; i++) {
			String nameCell = page.locator("table tbody tr").nth(i).locator("td").first().textContent();
			String specialtyCell = page.locator("table tbody tr").nth(i).locator("td").nth(1).textContent();

			assertFalse(nameCell.trim().isEmpty(), "Veterinarian name should not be empty");
			assertFalse(specialtyCell.trim().isEmpty(),
					"Veterinarian specialty should not be empty (should show 'none' if no specialties)");
		}
	}

	@Test
	@DisplayName("As a user, I want to handle browser refresh on veterinarians page, so that the page remains functional")
	void shouldHandleBrowserRefreshOnVeterinariansPage() {
		// Given - user is on veterinarians page
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should be on veterinarians page");

		// When - user refreshes the page
		page.reload();
		page.waitForLoadState();

		// Then - page should reload correctly
		assertTrue(page.url().contains("/vets.html"), "Should still be on veterinarians page");
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(),
				"Should show veterinarians heading after refresh");
		assertTrue(page.locator("table").isVisible(), "Should show veterinarians table after refresh");
		assertTrue(page.locator("table tbody tr").count() > 0, "Should show veterinarian records after refresh");
	}

}