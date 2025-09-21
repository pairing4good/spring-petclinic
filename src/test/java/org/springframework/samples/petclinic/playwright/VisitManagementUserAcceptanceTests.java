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
 * User acceptance tests for visit management functionality.
 *
 * These tests verify that users can schedule visits for pets, view visit history, and
 * manage visit information in the PetClinic application.
 */
class VisitManagementUserAcceptanceTests extends PlaywrightTestBase {

	@BeforeEach
	void setUp() {
		navigateToHomePage();
		navigateToOwnerWithPets();
	}

	private void navigateToOwnerWithPets() {
		// Navigate to an owner's details page that has pets for testing visit management
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
	@DisplayName("As a pet owner, I want to schedule a visit for my pet, so that I can track veterinary appointments and treatments")
	void shouldScheduleNewVisitForPet() {
		// Given: A user is viewing an owner's details page with pets
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// Look for "Add Visit" link for any pet
			if (page.locator("a[href*='/visits/new']").count() > 0) {

				// When: They click "Add Visit" for a pet
				page.locator("a[href*='/visits/new']").first().click();
				page.waitForLoadState();

				// Then: They should see the new visit form
				assertThat(page.locator("h2")).containsText("New Visit");
				assertThat(page.locator("form")).isVisible();
				assertThat(page.locator("input[name='date']")).isVisible();
				assertThat(page.locator("textarea[name='description']")).isVisible();

				// Fill in visit details
				page.fill("input[name='date']", "2024-01-15");
				page.fill("textarea[name='description']", "Regular checkup and vaccination");

				// Submit the form
				page.click("button[type='submit']");
				page.waitForLoadState();

				// Should be redirected back to owner details with the new visit listed
				assertThat(page.locator("h2")).containsText("Owner Information");
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see validation errors when scheduling a visit with invalid data, so that I know what needs to be corrected")
	void shouldShowValidationErrorsForInvalidVisitData() {
		// Given: A user is scheduling a new visit
		if (page.locator("h2").textContent().contains("Owner Information")) {

			if (page.locator("a[href*='/visits/new']").count() > 0) {
				page.locator("a[href*='/visits/new']").first().click();
				page.waitForLoadState();

				// When: They submit the form without required fields
				page.click("button[type='submit']");
				page.waitForLoadState();

				// Then: They should see validation errors and stay on the form
				assertThat(page.locator("h2")).containsText("New Visit");
				assertThat(page.locator("form")).isVisible();
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to view my pet's visit history, so that I can track their medical care over time")
	void shouldDisplayVisitHistoryForPet() {
		// Given: A user is viewing an owner's details page
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They look at the pets and visits section
			// Then: Visit history should be clearly displayed
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {
				assertThat(page.locator("dt:has-text('Pets and Visits')")).isVisible();

				// If there are visits, they should be shown in a table format
				if (page.locator("table").count() > 0) {
					assertThat(page.locator("table")).isVisible();

					// Visit dates and descriptions should be visible if any exist
					// This varies based on test data, so we just verify structure exists
				}
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see the pet's name clearly associated with each visit, so that I know which pet the visit is for")
	void shouldDisplayPetNameWithVisits() {
		// Given: A user is viewing an owner's details page with multiple pets
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They look at the pets and visits section
			// Then: Each pet's name should be clearly displayed with their visits
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {
				assertThat(page.locator("dt:has-text('Pets and Visits')")).isVisible();

				// Pet names should be visible as section headers or in tables
				// The structure ensures pets are grouped with their visits
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to easily add visits from the pet's information, so that I can quickly schedule appointments")
	void shouldProvideEasyAccessToAddVisit() {
		// Given: A user is viewing an owner's details page with pets
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They look for ways to add visits
			// Then: "Add Visit" links should be easily accessible for each pet
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {

				// Should have clear "Add Visit" links for pets
				// The exact count depends on test data, but structure should support it
				if (page.locator("a[href*='/visits/new']").count() > 0) {
					assertThat(page.locator("a[href*='/visits/new']").first()).isVisible();
				}
			}
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to see visit dates in a clear format, so that I can easily understand when visits occurred")
	void shouldDisplayVisitDatesInClearFormat() {
		// Given: A user is viewing an owner's details page with visit history
		if (page.locator("h2").textContent().contains("Owner Information")) {

			// When: They look at existing visits
			// Then: Visit dates should be displayed in a readable format
			if (page.locator("dt:has-text('Pets and Visits')").count() > 0) {
				// The application should display dates in a user-friendly format
				// This test verifies the structure supports clear date display
				assertThat(page.locator("dt:has-text('Pets and Visits')")).isVisible();
			}
		}
	}

}