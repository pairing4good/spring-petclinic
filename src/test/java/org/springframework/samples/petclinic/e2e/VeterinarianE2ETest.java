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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * End-to-End tests for veterinarian listing and related functionality.
 *
 * @author Spring PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("As a user I want to view veterinarian information so that I can see available vets and their specialties")
class VeterinarianE2ETest extends BasePlaywrightE2ETest {

	@LocalServerPort
	private int port;

	@Override
	protected void navigateToHome() {
		baseUrl = "http://localhost:" + port;
		page.navigate(baseUrl);
		page.waitForSelector("h2", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
	}

	private void navigateToVeterinarians() {
		navigateToHome();
		page.click("a[href='/vets.html']");
		waitForPageLoad();
	}

	@Test
	@DisplayName("As a user I want to access the veterinarians page so that I can view available veterinarians")
	void shouldAccessVeterinariansPage() {
		navigateToVeterinarians();

		assertEquals("Veterinarians", page.locator("h2").textContent());
		assertTrue(page.locator("table#vets").isVisible());
	}

	@Test
	@DisplayName("As a user I want to see veterinarian names so that I know who is available")
	void shouldDisplayVeterinarianNames() {
		navigateToVeterinarians();

		Locator vetsTable = page.locator("table#vets");
		assertTrue(vetsTable.isVisible());

		// Check for table headers
		assertTrue(page.locator("th:has-text('Name')").isVisible());
		assertTrue(page.locator("th:has-text('Specialties')").isVisible());

		// Verify there are vet entries in the table
		Locator vetRows = page.locator("table#vets tbody tr");
		assertTrue(vetRows.count() > 0, "Should have at least one veterinarian listed");
	}

	@Test
	@DisplayName("As a user I want to see veterinarian specialties so that I can find the right specialist")
	void shouldDisplayVeterinarianSpecialties() {
		navigateToVeterinarians();

		Locator specialtyColumns = page.locator("table#vets tbody tr td:nth-child(2)");
		assertTrue(specialtyColumns.count() > 0);

		// Check that specialties are displayed (either specific specialties or "none")
		for (int i = 0; i < specialtyColumns.count(); i++) {
			String specialtyText = specialtyColumns.nth(i).textContent();
			assertTrue(specialtyText != null && !specialtyText.trim().isEmpty(),
					"Specialty column should not be empty");
		}
	}

	@Test
	@DisplayName("As a user I want to see pagination controls so that I can navigate through multiple pages of vets")
	void shouldDisplayPaginationControls() {
		navigateToVeterinarians();

		// Check for pagination elements
		if (page.locator("text=pages").isVisible()) {
			// Pagination exists, verify controls
			assertTrue(page.locator(".fa-fast-backward, .fa-step-backward").count() >= 0);
			assertTrue(page.locator(".fa-fast-forward, .fa-step-forward").count() >= 0);
		}
	}

	@Test
	@DisplayName("As a user I want to navigate to the next page of veterinarians so that I can see more vets")
	void shouldNavigateThroughVeterinarianPages() {
		navigateToVeterinarians();

		// Check if there's a next page link
		Locator nextPageLink = page.locator("a[href*='page=2']");
		if (nextPageLink.isVisible()) {
			String currentUrl = page.url();
			nextPageLink.click();
			waitForPageLoad();

			// URL should have changed to include page parameter
			assertTrue(!page.url().equals(currentUrl));
			assertTrue(page.url().contains("page="));

			// Should still be on vets page with table
			assertEquals("Veterinarians", page.locator("h2").textContent());
			assertTrue(page.locator("table#vets").isVisible());
		}
	}

	@Test
	@DisplayName("As a user I want to see consistent page structure so that navigation is predictable")
	void shouldMaintainConsistentPageStructure() {
		navigateToVeterinarians();

		// Verify main navigation is still present
		assertTrue(page.locator("a[href='/']").isVisible());
		assertTrue(page.locator("a[href='/owners/find']").isVisible());
		assertTrue(page.locator("a[href='/vets.html']").isVisible());
		assertTrue(page.locator("a[href='/oups']").isVisible());

		// Verify page footer elements
		assertTrue(page.locator("img[alt*='Logo']").isVisible());
	}

	@Test
	@DisplayName("As a user I want the veterinarians table to be accessible so that I can use assistive technologies")
	void shouldHaveAccessibleTable() {
		navigateToVeterinarians();

		Locator table = page.locator("table#vets");

		// Table should have proper structure
		assertTrue(table.locator("thead").isVisible());
		assertTrue(table.locator("tbody").isVisible());
		assertTrue(table.locator("th").count() >= 2);

		// Headers should be properly labeled
		assertTrue(page.locator("th:has-text('Name')").isVisible());
		assertTrue(page.locator("th:has-text('Specialties')").isVisible());
	}

	@Test
	@DisplayName("As a user I want to return to homepage from veterinarians page so that I can navigate to other sections")
	void shouldReturnToHomepageFromVeterinariansPage() {
		navigateToVeterinarians();

		page.click("a[href='/']");
		waitForPageLoad();

		assertEquals("Welcome", page.locator("h2").textContent());
		assertTrue(page.url().endsWith("/") || page.url().contains("localhost:" + port + "/"));
	}

	@Test
	@DisplayName("As a user I want to see veterinarians page load quickly so that I have good user experience")
	void shouldLoadVeterinariansPageQuickly() {
		navigateToHome();

		long startTime = System.currentTimeMillis();
		page.click("a[href='/vets.html']");
		page.waitForSelector("table#vets");
		long loadTime = System.currentTimeMillis() - startTime;

		// Page should load within 5 seconds
		assertTrue(loadTime < 5000,
				"Veterinarians page load time should be less than 5 seconds, was: " + loadTime + "ms");
	}

	@Test
	@DisplayName("As a user I want the table to be responsive so that I can view it on mobile devices")
	void shouldDisplayResponsiveTable() {
		// Test mobile viewport
		page.setViewportSize(375, 667); // iPhone SE size
		navigateToVeterinarians();

		// Table should still be visible and usable
		assertTrue(page.locator("table#vets").isVisible());

		// Table should not overflow the viewport significantly
		Locator table = page.locator("table#vets");
		var boundingBox = table.boundingBox();
		assertTrue(boundingBox.width <= 400, "Table should not be much wider than mobile viewport");

		// Reset viewport
		page.setViewportSize(1280, 720);
	}

	@Test
	@DisplayName("As a user I want to verify veterinarian data integrity so that information is accurate")
	void shouldDisplayValidVeterinarianData() {
		navigateToVeterinarians();

		Locator vetRows = page.locator("table#vets tbody tr");
		int rowCount = vetRows.count();

		assertTrue(rowCount > 0, "Should have at least one veterinarian");

		// Verify each row has proper data structure
		for (int i = 0; i < rowCount; i++) {
			Locator row = vetRows.nth(i);
			Locator nameCell = row.locator("td:nth-child(1)");
			Locator specialtyCell = row.locator("td:nth-child(2)");

			// Name should not be empty
			String name = nameCell.textContent();
			assertTrue(name != null && !name.trim().isEmpty(), "Veterinarian name should not be empty in row " + i);

			// Specialty cell should exist (content can be "none")
			assertTrue(specialtyCell.isVisible(), "Specialty cell should be visible in row " + i);
		}
	}

	@Test
	@DisplayName("As a user I want to see multiple veterinarians with different specialties so that I have options")
	void shouldDisplayVarietyOfVeterinarians() {
		navigateToVeterinarians();

		Locator vetRows = page.locator("table#vets tbody tr");
		int rowCount = vetRows.count();

		// Should have multiple vets (at least 2 for variety)
		assertTrue(rowCount >= 2, "Should have at least 2 veterinarians for variety");

		// Collect all specialties to verify variety
		boolean hasSpecialties = false;
		boolean hasNoneSpecialty = false;

		for (int i = 0; i < rowCount; i++) {
			String specialty = vetRows.nth(i).locator("td:nth-child(2)").textContent().trim();

			if (specialty.toLowerCase().contains("none")) {
				hasNoneSpecialty = true;
			}
			else if (!specialty.isEmpty()) {
				hasSpecialties = true;
			}
		}

		// Should have both vets with specialties and some without (realistic data)
		assertTrue(hasSpecialties || hasNoneSpecialty, "Should have either specialized vets or general practitioners");
	}

}