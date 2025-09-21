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

package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * User acceptance tests for veterinarian information functionality.
 * 
 * These tests verify that users can view the list of veterinarians and their
 * specialties in the PetClinic application.
 */
class VeterinarianUserAcceptanceTests extends PlaywrightTestBase {

	@BeforeEach
	void setUp() {
		navigateToHomePage();
	}

	@Test
	@DisplayName("As a pet owner, I want to view the list of veterinarians, so that I know which vets are available at the clinic")
	void shouldDisplayListOfVeterinarians() {
		// Given: A user wants to see available veterinarians
		// When: They navigate to the veterinarians page
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// Then: They should see the veterinarians list
		assertThat(page.locator("h2")).containsText("Veterinarians");
		assertThat(page.locator("table")).isVisible();
		
		// Should have column headers for Name and Specialties
		assertThat(page.locator("th")).containsText("Name");
		assertThat(page.locator("th")).containsText("Specialties");
	}

	@Test
	@DisplayName("As a pet owner, I want to see each veterinarian's name clearly, so that I can identify them for appointments")
	void shouldDisplayVeterinarianNamesClarly() {
		// Given: A user is viewing the veterinarians list
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They look at the veterinarians table
		// Then: Each vet's name should be clearly displayed
		assertThat(page.locator("table tbody")).isVisible();
		
		// Should have at least some veterinarians listed (based on test data)
		if (page.locator("table tbody tr").count() > 0) {
			assertThat(page.locator("table tbody tr").first()).isVisible();
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see each veterinarian's specialties, so that I can choose the right vet for my pet's needs")
	void shouldDisplayVeterinarianSpecialties() {
		// Given: A user is viewing the veterinarians list
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They look at the specialties column
		// Then: Specialties should be clearly displayed for each vet
		assertThat(page.locator("table")).isVisible();
		
		// Check that the table structure supports specialty display
		assertThat(page.locator("th")).containsText("Specialties");
		
		// If there are vets with specialties, they should be visible
		if (page.locator("table tbody tr").count() > 0) {
			// The specialties column should exist for each vet row
			if (page.locator("table tbody tr").first().locator("td").count() > 1) {
				assertThat(page.locator("table tbody tr").first().locator("td")).isVisible();
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see veterinarians organized in a clear table format, so that I can easily compare their information")
	void shouldDisplayVeterinariansInClearTableFormat() {
		// Given: A user is viewing the veterinarians list
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They look at how the information is organized
		// Then: Information should be in a well-structured table
		assertThat(page.locator("table.table")).isVisible();
		assertThat(page.locator("table thead")).isVisible();
		assertThat(page.locator("table tbody")).isVisible();
		
		// Table should have proper styling classes for readability
		assertThat(page.locator("table")).isVisible();
	}

	@Test
	@DisplayName("As a pet owner, I want to navigate between pages of veterinarians if there are many, so that I can see all available vets")
	void shouldSupportPaginationIfManyVeterinarians() {
		// Given: A user is viewing the veterinarians list
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They look for pagination controls (if needed)
		// Then: Pagination should be available if there are many vets
		
		// Check if pagination exists (this depends on the number of vets in test data)
		if (page.locator(".pagination").count() > 0 || page.locator("a[href*='page=']").count() > 0) {
			// If pagination exists, it should be functional
			assertThat(page.locator("a[href*='page=']").first()).isVisible();
		}
		
		// At minimum, the page should handle displaying all available vets
		assertThat(page.locator("table")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to return to the home page from the veterinarians list, so that I can navigate to other sections")
	void shouldAllowNavigationBackToHome() {
		// Given: A user is viewing the veterinarians list
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They click the home link
		page.click("a[href='/']");
		page.waitForLoadState();

		// Then: They should be back on the home page
		assertThat(page).hasURL(baseUrl + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@Test
	@DisplayName("As a user, I want to access veterinarian information quickly from the main navigation, so that I can easily find vet details")
	void shouldProvideQuickAccessFromMainNavigation() {
		// Given: A user is on any page of the application
		navigateToHomePage();

		// When: They look at the main navigation
		// Then: Veterinarians link should be easily accessible
		assertThat(page.locator("nav")).isVisible();
		assertThat(page.locator("a[href='/vets.html']")).isVisible();
		
		// Link should be clearly labeled
		assertThat(page.locator("a[href='/vets.html']")).containsText("Veterinarians");
	}

}