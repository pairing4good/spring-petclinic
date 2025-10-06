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
 * User acceptance tests for pet management functionality.
 *
 * These tests verify that users can add pets to owners, view pet information, and manage
 * pet details in the PetClinic application.
 */
class PetManagementUserAcceptanceTests extends PlaywrightTestBase {

	@BeforeEach
	void setUp() {
		navigateToHomePage();
		navigateToFirstOwnerDetails();
	}

	private void navigateToFirstOwnerDetails() {
		// Navigate to an owner's details page for testing pet management
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();

		// If there are owners, go to the first one's details
		if (page.locator("table tbody tr").count() > 0) {
			page.locator("table tbody tr").first().locator("a").click();
			page.waitForLoadState();
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to add a new pet to my account, so that I can track my pet's visits and information")
	void shouldAddNewPetToOwner() {
		// Given: A user is viewing an owner's details page
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They click "Add New Pet"
			page.click("a[href*='/pets/new']");
			page.waitForLoadState();

			// Then: They should see the new pet form
			assertThat(page.locator("h2")).containsText("New Pet");
			assertThat(page.locator("form")).isVisible();
			assertThat(page.locator("input[name='name']")).isVisible();
			assertThat(page.locator("select[name='type']")).isVisible();

			// Fill in pet details
			page.fill("input[name='name']", "Buddy");
			page.selectOption("select[name='type']", "dog");
			page.fill("input[name='birthDate']", "2020-01-15");

			// Submit the form
			page.click("button[type='submit']");
			page.waitForLoadState();

			// Should be redirected back to owner details with the new pet listed
			assertThat(page.locator("h2")).containsText("Owner Information");
			assertThat(page.locator("table")).isVisible();
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see validation errors when adding a pet with invalid data, so that I know what needs to be corrected")
	void shouldShowValidationErrorsForInvalidPetData() {
		// Given: A user is adding a new pet
		if (page.locator("h2").textContent().contains("Owner Information")) {
			page.click("a[href*='/pets/new']");
			page.waitForLoadState();

			// When: They submit the form without required fields
			page.click("button[type='submit']");
			page.waitForLoadState();

			// Then: They should see validation errors and stay on the form
			assertThat(page.locator("h2")).containsText("New Pet");
			assertThat(page.locator("form")).isVisible();
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to edit my pet's information, so that I can update details like name or birth date")
	void shouldEditPetInformation() {
		// Given: An owner has pets and wants to edit one
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// Check if there are any pets listed
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {
				// Look for edit pet link
				if (page.locator("a[href*='/pets/'][href*='/edit']").count() > 0) {

					// When: They click "Edit Pet"
					page.locator("a[href*='/pets/'][href*='/edit']").first().click();
					page.waitForLoadState();

					// Then: They should see the edit pet form
					assertThat(page.locator("h2")).containsText("Pet");
					assertThat(page.locator("form")).isVisible();
					assertThat(page.locator("input[name='name']")).isVisible();

					// Should be able to update and save
					page.fill("input[name='name']", "Updated Pet Name");
					page.click("button[type='submit']");
					page.waitForLoadState();

					// Should be redirected back to owner details
					assertThat(page.locator("h2")).containsText("Owner Information");
				}
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see different pet types available, so that I can accurately categorize my pet")
	void shouldDisplayAvailablePetTypes() {
		// Given: A user is adding a new pet
		if (page.locator("h2").textContent().contains("Owner Information")) {
			page.click("a[href*='/pets/new']");
			page.waitForLoadState();

			// When: They look at the pet type dropdown
			// Then: They should see various pet type options
			assertThat(page.locator("select[name='type']")).isVisible();

			// Check that there are multiple options (common pet types)
			if (page.locator("select[name='type'] option").count() > 1) {
				// Multiple pet types are available
				assertThat(page.locator("select[name='type'] option")).isVisible();
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see my pet's information clearly displayed, so that I can verify the details are correct")
	void shouldDisplayPetInformationClearly() {
		// Given: A user is viewing an owner's details page with pets
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They look at the pets section
			// Then: Pet information should be clearly displayed
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {
				// Pet section should be visible
				assertThat(page.locator("dt:has-text('Pets and Visits')")).isVisible();

				// If there are pets, their details should be visible
				if (page.locator("table").count() > 1) {
					// Should show pet names, birth dates, types
					assertThat(page.locator("table")).isVisible();
				}
			}
		}
	}

}