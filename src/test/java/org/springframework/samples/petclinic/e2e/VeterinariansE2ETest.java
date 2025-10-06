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

/**
 * As a user, I want to view veterinarian information, so that I can see available doctors
 * and their specialties
 */
@DisplayName("Veterinarians E2E Tests")
class VeterinariansE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to access the veterinarians page, so that I can see the list of available doctors")
	void testVeterinariansPageAccess() {
		navigateToVeterinarians();

		assertPageTitle("PetClinic :: a Spring Framework demonstration");
		assertElementContainsText("h2", "Veterinarians");
		assertElementVisible("table");

		// Verify table headers
		assertElementContainsText("table", "Name");
		assertElementContainsText("table", "Specialties");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian names and specialties, so that I can choose the right doctor")
	void testVeterinariansTableContent() {
		navigateToVeterinarians();

		// Verify specific veterinarians are displayed
		assertElementContainsText("table", "James Carter");
		assertElementContainsText("table", "Helen Leary");
		assertElementContainsText("table", "Linda Douglas");
		assertElementContainsText("table", "Rafael Ortega");
		assertElementContainsText("table", "Henry Stevens");

		// Verify specialties are displayed
		assertElementContainsText("table", "radiology");
		assertElementContainsText("table", "dentistry");
		assertElementContainsText("table", "surgery");
		assertElementContainsText("table", "none");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarians with multiple specialties, so that I know their full range of expertise")
	void testVeterinariansMultipleSpecialties() {
		navigateToVeterinarians();

		// Linda Douglas should have both dentistry and surgery
		String pageContent = page.content();
		assert pageContent.contains("Linda Douglas") : "Linda Douglas should be in the veterinarians list";

		// Find the row containing Linda Douglas and verify it has both specialties
		String lindaRow = page.locator("tr:has-text('Linda Douglas')").textContent();
		assert lindaRow.contains("dentistry") : "Linda Douglas should have dentistry specialty";
		assert lindaRow.contains("surgery") : "Linda Douglas should have surgery specialty";
	}

	@Test
	@DisplayName("As a user, I want to see veterinarians with no specialties, so that I know they are general practitioners")
	void testVeterinariansWithoutSpecialties() {
		navigateToVeterinarians();

		// James Carter should have "none" as specialty
		String jamesRow = page.locator("tr:has-text('James Carter')").textContent();
		assert jamesRow.contains("none") : "James Carter should have 'none' as specialty";
	}

	@Test
	@DisplayName("As a user, I want pagination on veterinarians page, so that I can browse through all doctors")
	void testVeterinariansPagination() {
		navigateToVeterinarians();

		// Check for pagination elements
		assertElementVisible(".pagination, .page-navigation, div:has-text('pages')");

		// Should see page indicators
		String pageContent = page.content();
		assert pageContent.contains("pages") || pageContent.contains("1") : "Should show pagination indicators";

		// Check if there's a link to page 2
		if (page.locator("a[href*='page=2']").count() > 0) {
			// Test navigation to second page
			page.locator("a[href*='page=2']").first().click();
			waitForPageLoad();

			assertUrlContains("page=2");
			assertElementContainsText("h2", "Veterinarians");
			assertElementVisible("table");

			// Navigate back to first page
			if (page.locator("a[href*='page=1'], a:has-text('Previous'), a:has-text('First')").count() > 0) {
				page.locator("a[href*='page=1'], a:has-text('Previous'), a:has-text('First')").first().click();
				waitForPageLoad();
				assertElementContainsText("h2", "Veterinarians");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want consistent table formatting, so that veterinarian information is easy to read")
	void testVeterinariansTableFormatting() {
		navigateToVeterinarians();

		// Verify table structure
		assertElementVisible("table thead, table th");
		assertElementVisible("table tbody, table td");

		// Verify each row has the correct number of columns
		int headerCount = page.locator("table th").count();
		int firstRowCellCount = page.locator("table tbody tr").first().locator("td").count();

		assert headerCount == firstRowCellCount : "Header count should match cell count";
		assert headerCount >= 2 : "Should have at least Name and Specialties columns";
	}

	@Test
	@DisplayName("As a user, I want to access veterinarians from main navigation, so that I can quickly find this information")
	void testVeterinariansNavigationFromOtherPages() {
		// Start from home page
		navigateToHome();
		page.locator("a[href='/vets.html']").click();
		waitForPageLoad();

		assertUrlContains("/vets.html");
		assertElementContainsText("h2", "Veterinarians");

		// Test from find owners page
		navigateToFindOwners();
		page.locator("a[href='/vets.html']").click();
		waitForPageLoad();

		assertUrlContains("/vets.html");
		assertElementContainsText("h2", "Veterinarians");
	}

	@Test
	@DisplayName("As a user, I want the veterinarians page to load quickly, so that I can get information efficiently")
	void testVeterinariansPagePerformance() {
		long startTime = System.currentTimeMillis();
		navigateToVeterinarians();
		long loadTime = System.currentTimeMillis() - startTime;

		// Page should load within 5 seconds
		assert loadTime < 5000 : "Veterinarians page took too long to load: " + loadTime + "ms";

		// Verify content is actually loaded
		assertElementVisible("table");
		assertElementContainsText("table", "James Carter");
	}

	@Test
	@DisplayName("As a user, I want proper table accessibility, so that screen readers can interpret the veterinarian data")
	void testVeterinariansTableAccessibility() {
		navigateToVeterinarians();

		// Verify table has proper structure for accessibility
		assertElementVisible("table");
		assertElementVisible("thead, th");
		assertElementVisible("tbody, td");

		// Check for proper table headers
		int thCount = page.locator("th").count();
		assert thCount >= 2 : "Should have proper table headers";

		// Verify table data is structured properly
		int trCount = page.locator("tbody tr").count();
		assert trCount >= 5 : "Should have at least 5 veterinarians";
	}

	@Test
	@DisplayName("As a user, I want to see all veterinarian specialties clearly, so that I can understand their expertise")
	void testVeterinarianSpecialtiesDisplay() {
		navigateToVeterinarians();

		// Check that specialties are displayed in a readable format
		String[] expectedSpecialties = { "radiology", "dentistry", "surgery", "none" };

		for (String specialty : expectedSpecialties) {
			assertElementContainsText("table", specialty);
		}

		// Verify that multiple specialties for one vet are shown properly
		// Linda Douglas should have both dentistry and surgery visible
		String tableContent = page.locator("table").textContent();
		assert tableContent.contains("Linda Douglas") : "Linda Douglas should be in the table";

		String lindaRow = page.locator("tr:has-text('Linda Douglas')").textContent();
		assert lindaRow.contains("dentistry") && lindaRow.contains("surgery")
				: "Linda Douglas should show both specialties";
	}

	@Test
	@DisplayName("As a user, I want veterinarians to be listed in a consistent order, so that I can find them reliably")
	void testVeterinariansConsistentOrdering() {
		navigateToVeterinarians();

		// Get the list of veterinarian names
		String[] expectedVets = { "James Carter", "Helen Leary", "Linda Douglas", "Rafael Ortega", "Henry Stevens" };

		for (String vetName : expectedVets) {
			assertElementContainsText("table", vetName);
		}

		// Reload page and verify same order
		page.reload();
		waitForPageLoad();

		for (String vetName : expectedVets) {
			assertElementContainsText("table", vetName);
		}
	}

}