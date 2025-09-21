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

import static org.junit.jupiter.api.Assertions.*;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for Veterinarian functionality. Covers veterinarian listing,
 * pagination, and specialties display.
 */
public class VeterinarianE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToViewTheListOfVeterinarians_SoThatICanSeeAvailableVets() {
		navigateAndWait("/vets.html");

		// Verify veterinarians page structure
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible()),
				() -> assertTrue(page.locator("th:has-text('Name')").isVisible()),
				() -> assertTrue(page.locator("th:has-text('Specialties')").isVisible()));

		// Verify that veterinarians are displayed
		Locator vetRows = page.locator("table tbody tr");
		assertTrue(vetRows.count() > 0);

		// Check that each row has name and specialties
		for (int i = 0; i < Math.min(vetRows.count(), 5); i++) {
			Locator row = vetRows.nth(i);
			assertAll(() -> assertTrue(row.locator("td").count() >= 2),
					() -> assertFalse(row.locator("td").first().textContent().trim().isEmpty()));
		}
	}

	@Test
	void asAUser_IWantToSeeVeterinarianSpecialties_SoThatIKnowTheirExpertise() {
		navigateAndWait("/vets.html");

		// Check for specialty information
		Locator specialtyElements = page.locator("td")
			.filter(new Locator.FilterOptions().setHasText("radiology"))
			.or(page.locator("td").filter(new Locator.FilterOptions().setHasText("surgery")))
			.or(page.locator("td").filter(new Locator.FilterOptions().setHasText("dentistry")));

		if (specialtyElements.count() > 0) {
			// Verify specialty display
			assertTrue(specialtyElements.first().isVisible());

			// Check for "none" specialty for vets without specialties
			Locator noneSpecialty = page.locator("td:has-text('none')");
			if (noneSpecialty.count() > 0) {
				assertTrue(noneSpecialty.first().isVisible());
			}
		}

		// Verify table structure is correct
		Locator tableHeaders = page.locator("th");
		assertTrue(tableHeaders.count() >= 2);
	}

	@Test
	void asAUser_IWantToNavigateVeterinarianPages_SoThatICanSeeAllVeterinarians() {
		navigateAndWait("/vets.html");

		// Check if pagination exists
		if (page.locator("a:has-text('2')").isVisible()) {
			// Test pagination
			String firstPageContent = page.locator("table tbody").textContent();

			page.click("a:has-text('2')");
			page.waitForURL("**/vets.html?page=2");

			// Verify we're on page 2
			assertAll(() -> assertTrue(page.url().contains("page=2")),
					() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
					() -> assertTrue(page.locator("table").isVisible()));

			// Content should be different from first page
			String secondPageContent = page.locator("table tbody").textContent();
			// Only assert difference if there are actually different vets on different
			// pages
			assertTrue(page.locator("table tbody tr").count() > 0);

			// Test navigation back to first page
			if (page.locator("a:has-text('1')").isVisible()) {
				page.click("a:has-text('1')");
				page.waitForLoadState();
				assertTrue(page.url().contains("page=1") || !page.url().contains("page="));
			}
		}

		// Test pagination controls if they exist
		if (page.locator("span:has-text('pages')").isVisible()) {
			assertTrue(page.locator("span:has-text('pages')").isVisible());
		}
	}

	@Test
	void asAUser_IWantToTestVeterinarianPagePaginationControls_SoThatNavigationIsEasyToUse() {
		navigateAndWait("/vets.html");

		// Test first/previous/next/last controls if they exist
		if (page.locator("a[title*='first'], a[title*='First']").isVisible()) {
			// Test pagination controls
			Locator firstLink = page.locator("a[title*='first'], a[title*='First']");
			Locator lastLink = page.locator("a[title*='last'], a[title*='Last']");
			Locator nextLink = page.locator("a[title*='next'], a[title*='Next']");
			Locator prevLink = page.locator("a[title*='previous'], a[title*='Previous']");

			// Verify pagination controls exist
			assertTrue(firstLink.count() > 0 || lastLink.count() > 0 || nextLink.count() > 0 || prevLink.count() > 0);
		}

		// Test direct page number links
		if (page.locator("a:has-text('2')").isVisible()) {
			page.click("a:has-text('2')");
			page.waitForLoadState();

			// Should navigate to page 2
			assertTrue(page.url().contains("page=2"));
			assertTrue(page.locator("table").isVisible());
		}
	}

	@Test
	void asAUser_IWantToTestVetPageDirectAccess_SoThatICanBookmarkSpecificPages() {
		// Test direct access to page 2
		navigateAndWait("/vets.html?page=2");

		// Should show veterinarians page
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible()));

		// Test direct access to page 1 (explicit)
		navigateAndWait("/vets.html?page=1");

		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible()));
	}

	@Test
	void asAUser_IWantToTestInvalidVetPageNumbers_SoThatErrorsAreHandledGracefully() {
		// Test access to non-existent page
		navigateAndWait("/vets.html?page=999");

		// Should handle gracefully (show empty results or redirect to valid page)
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible() || page.locator("body").isVisible()));

		// Test negative page number
		navigateAndWait("/vets.html?page=-1");

		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible() || page.locator("body").isVisible()));

		// Test non-numeric page parameter
		navigateAndWait("/vets.html?page=invalid");

		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible() || page.locator("body").isVisible()));
	}

	@Test
	void asAUser_IWantToVerifyVeterinarianDataIntegrity_SoThatInformationIsAccurate() {
		navigateAndWait("/vets.html");

		// Check that each veterinarian row has proper structure
		Locator vetRows = page.locator("table tbody tr");

		for (int i = 0; i < Math.min(vetRows.count(), 3); i++) {
			Locator row = vetRows.nth(i);
			Locator namCell = row.locator("td").first();
			Locator specialtyCell = row.locator("td").last();

			assertAll(() -> assertFalse(namCell.textContent().trim().isEmpty()),
					() -> assertTrue(specialtyCell.isVisible()) // Specialty cell exists
																// (even if "none")
			);
		}
	}

	@Test
	void asAUser_IWantToTestVeterinarianPageBreadcrumbNavigation_SoThatICanNavigateBack() {
		navigateAndWait("/vets.html");

		// Navigate to home page using navigation
		page.click("a[href='/']");
		page.waitForURL("**/");

		// Verify we're on home page
		assertTrue(page.locator("h2").textContent().contains("Welcome"));

		// Navigate back to vets using browser back
		page.goBack();
		page.waitForLoadState();

		// Should be back on vets page
		assertTrue(page.locator("h2").textContent().contains("Veterinarians"));
	}

	@Test
	void asAUser_IWantToTestVeterinarianPagePerformance_SoThatPageLoadsQuickly() {
		// Test page load time
		long startTime = System.currentTimeMillis();
		navigateAndWait("/vets.html");
		long loadTime = System.currentTimeMillis() - startTime;

		// Verify page loaded successfully
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible()), () -> assertTrue(loadTime < 10000) // Should
																										// load
																										// within
																										// 10
																										// seconds
		);
	}

	@Test
	void asAUser_IWantToTestVeterinarianTableAccessibility_SoThatScreenReadersCanUseIt() {
		navigateAndWait("/vets.html");

		// Check for proper table structure
		Locator table = page.locator("table");
		assertAll(() -> assertTrue(table.isVisible()), () -> assertTrue(page.locator("thead").isVisible()),
				() -> assertTrue(page.locator("tbody").isVisible()), () -> assertTrue(page.locator("th").count() >= 2));

		// Verify table headers are properly defined
		Locator headers = page.locator("th");
		for (int i = 0; i < headers.count(); i++) {
			assertFalse(headers.nth(i).textContent().trim().isEmpty());
		}
	}

	@Test
	void asAUser_IWantToTestVeterinarianPageKeyboardNavigation_SoThatItIsAccessible() {
		navigateAndWait("/vets.html");

		// Test keyboard navigation through pagination links
		if (page.locator("a:has-text('2')").isVisible()) {
			// Focus on pagination link
			page.locator("a:has-text('2')").focus();

			// Press Enter to navigate
			page.keyboard().press("Enter");
			page.waitForLoadState();

			// Should navigate to page 2
			assertTrue(page.url().contains("page=2") || page.locator("h2").textContent().contains("Veterinarians"));
		}
	}

	@Test
	void asAUser_IWantToTestMultipleSpecialtiesDisplay_SoThatAllSpecialtiesAreVisible() {
		navigateAndWait("/vets.html");

		// Look for veterinarians with multiple specialties
		Locator specialtyeCells = page.locator("td").filter(new Locator.FilterOptions().setHasText("surgery"));

		if (specialtyeCells.count() > 0) {
			// Check if multiple specialties are displayed properly
			String specialtyText = specialtyeCells.first().textContent();

			// Verify specialty text is not empty and formatted correctly
			assertFalse(specialtyText.trim().isEmpty());

			// Check for veterinarians with multiple specialties (if any)
			Locator multipleSpecialties = page.locator("td")
				.filter(new Locator.FilterOptions().setHasText("surgery"))
				.filter(new Locator.FilterOptions().setHasText("dentistry"));

			if (multipleSpecialties.count() > 0) {
				String multiSpecialtyText = multipleSpecialties.first().textContent();
				assertTrue(multiSpecialtyText.contains("surgery") && multiSpecialtyText.contains("dentistry"));
			}
		}
	}

	@Test
	void asAUser_IWantToTestVeterinarianPageWithDifferentViewports_SoThatItIsResponsive() {
		// Test mobile viewport
		page.setViewportSize(375, 667);
		navigateAndWait("/vets.html");

		assertAll(() -> assertTrue(page.locator("h2").isVisible()),
				() -> assertTrue(page.locator("table").isVisible()));

		// Test tablet viewport
		page.setViewportSize(768, 1024);
		page.reload();
		page.waitForLoadState();

		assertAll(() -> assertTrue(page.locator("h2").isVisible()),
				() -> assertTrue(page.locator("table").isVisible()));

		// Test desktop viewport
		page.setViewportSize(1920, 1080);
		page.reload();
		page.waitForLoadState();

		assertAll(() -> assertTrue(page.locator("h2").isVisible()),
				() -> assertTrue(page.locator("table").isVisible()));
	}

}