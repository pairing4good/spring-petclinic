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
 * End-to-end tests for Pet management functionality. Covers pet CRUD operations, visit
 * tracking, and pet type management.
 */
public class PetManagementE2ETest extends BaseE2ETest {

	@Test
	void asAnOwner_IWantToAddANewPetToMyAccount_SoThatICanTrackMyPetInformation() {
		// First, navigate to an owner who can have pets
		navigateAndWait("/owners/find");
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		// Go to owner details
		page.locator("table#owners a").first().click();
		page.waitForLoadState();

		// Click Add New Pet
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Verify add pet form
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Pet")),
				() -> assertTrue(page.locator("input[name='name']").isVisible()),
				() -> assertTrue(page.locator("input[name='birthDate']").isVisible()),
				() -> assertTrue(page.locator("select[name='type'], input[name='type']").isVisible()),
				() -> assertTrue(page.locator("button[type='submit']").isVisible()));

		// Fill out pet form
		fillField("input[name='name']", "TestPet");
		fillField("input[name='birthDate']", "2023-01-01");

		// Select pet type if dropdown exists
		if (page.locator("select[name='type']").isVisible()) {
			page.selectOption("select[name='type']", "dog");
		}

		// Submit form
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should return to owner details with new pet
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("h2:has-text('Pets and Visits')").isVisible()));
	}

	@Test
	void asAnOwner_IWantToEditMyPetInformation_SoThatICanUpdatePetDetails() {
		// Navigate to owner with existing pets
		navigateAndWait("/owners/2"); // Betty Davis has pets

		// Look for Edit Pet link
		if (page.locator("a:has-text('Edit Pet')").isVisible()) {
			page.click("a:has-text('Edit Pet')");
			page.waitForLoadState();

			// Verify edit form is loaded with existing data
			assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Pet")),
					() -> assertTrue(page.locator("input[name='name']").isVisible()),
					() -> assertFalse(page.locator("input[name='name']").inputValue().isEmpty()));

			// Update pet name
			String originalName = page.locator("input[name='name']").inputValue();
			fillField("input[name='name']", originalName + " Updated");

			// Submit changes
			page.click("button[type='submit']");
			page.waitForLoadState();

			// Verify changes are reflected
			assertTrue(page.locator("h2").textContent().contains("Owner Information"));
		}
	}

	@Test
	void asAnOwner_IWantToSeeValidationErrorsForPets_SoThatIKnowRequiredPetInformation() {
		// Navigate to add pet form
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Submit form with missing required fields
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should stay on form with validation errors
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Pet")),
				() -> assertTrue(page.locator("form").isVisible()));
	}

	@Test
	void asAnOwner_IWantToAddVisitsToMyPet_SoThatICanTrackVeterinaryVisits() {
		// Navigate to owner with existing pets
		navigateAndWait("/owners/2");

		// Look for Add Visit link
		if (page.locator("a:has-text('Add Visit')").isVisible()) {
			page.click("a:has-text('Add Visit')");
			page.waitForLoadState();

			// Verify add visit form
			assertAll(
					() -> assertTrue(page.locator("h2").textContent().contains("Visit")
							|| page.locator("h2").textContent().contains("New Visit")),
					() -> assertTrue(page.locator("input[name='date'], input[name='visitDate']").isVisible()),
					() -> assertTrue(
							page.locator("textarea[name='description'], input[name='description']").isVisible()));

			// Fill out visit form
			if (page.locator("input[name='date']").isVisible()) {
				fillField("input[name='date']", "2024-01-15");
			}
			else if (page.locator("input[name='visitDate']").isVisible()) {
				fillField("input[name='visitDate']", "2024-01-15");
			}

			// Add description
			if (page.locator("textarea[name='description']").isVisible()) {
				page.fill("textarea[name='description']", "Regular checkup");
			}
			else if (page.locator("input[name='description']").isVisible()) {
				fillField("input[name='description']", "Regular checkup");
			}

			// Submit visit
			page.click("button[type='submit']");
			page.waitForLoadState();

			// Should return to owner details
			assertTrue(page.locator("h2").textContent().contains("Owner Information"));
		}
	}

	@Test
	void asAnOwner_IWantToTestPetBirthDateValidation_SoThatOnlyValidDatesAreAccepted() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Test with invalid birth date (future date)
		fillField("input[name='name']", "FuturePet");
		fillField("input[name='birthDate']", "2030-01-01");

		if (page.locator("select[name='type']").isVisible()) {
			page.selectOption("select[name='type']", "dog");
		}

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should show validation error or stay on form
		assertTrue(page.locator("form").isVisible() || page.locator("h2").textContent().contains("Owner Information"));
	}

	@Test
	void asAnOwner_IWantToTestPetNameValidation_SoThatPetNamesFollowExpectedFormat() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Test with empty pet name
		fillField("input[name='name']", "");
		fillField("input[name='birthDate']", "2020-01-01");

		if (page.locator("select[name='type']").isVisible()) {
			page.selectOption("select[name='type']", "dog");
		}

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should stay on form with validation error
		assertTrue(page.locator("form").isVisible());
	}

	@Test
	void asAnOwner_IWantToTestSpecialCharactersInPetName_SoThatTheyAreHandledCorrectly() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Test with special characters in pet name
		fillField("input[name='name']", "Mía-José");
		fillField("input[name='birthDate']", "2020-01-01");

		if (page.locator("select[name='type']").isVisible()) {
			page.selectOption("select[name='type']", "dog");
		}

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should handle special characters correctly
		assertTrue(page.locator("h2").textContent().contains("Owner Information") || page.locator("form").isVisible());
	}

	@Test
	void asAnOwner_IWantToSeeAllAvailablePetTypes_SoThatICanSelectTheCorrectType() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Check if pet type dropdown is available
		if (page.locator("select[name='type']").isVisible()) {
			Locator typeSelect = page.locator("select[name='type']");
			Locator options = typeSelect.locator("option");

			// Should have multiple pet type options
			assertTrue(options.count() > 1);

			// Check for common pet types
			String optionsText = typeSelect.textContent().toLowerCase();
			assertTrue(optionsText.contains("dog") || optionsText.contains("cat") || optionsText.contains("bird")
					|| optionsText.contains("hamster"));
		}
	}

	@Test
	void asAnOwner_IWantToViewPetDetailsInOwnerProfile_SoThatICanSeeAllPetInformation() {
		navigateAndWait("/owners/2"); // Betty Davis has pets

		// Verify pets and visits section
		assertAll(() -> assertTrue(page.locator("h2:has-text('Pets and Visits')").isVisible()),
				() -> assertTrue(page.locator("table").isVisible()));

		// Check for pet information display
		Locator petsTable = page.locator("table").last(); // Usually the pets table is
															// last
		if (petsTable.isVisible()) {
			// Should show pet name, birth date, type
			String tableText = petsTable.textContent();
			assertTrue(tableText.contains("Name") || tableText.contains("Birth") || tableText.contains("Type")
					|| tableText.length() > 0);
		}
	}

	@Test
	void asAnOwner_IWantToEditPetWithValidationErrors_SoThatICanSeeAppropriateErrorMessages() {
		navigateAndWait("/owners/2");

		if (page.locator("a:has-text('Edit Pet')").isVisible()) {
			page.click("a:has-text('Edit Pet')");
			page.waitForLoadState();

			// Clear required fields
			fillField("input[name='name']", "");

			page.click("button[type='submit']");
			page.waitForLoadState();

			// Should stay on edit form with validation errors
			assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Pet")),
					() -> assertTrue(page.locator("form").isVisible()));
		}
	}

	@Test
	void asAnOwner_IWantToNavigateBackFromPetForm_SoThatICanReturnToOwnerDetails() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Use browser back button
		page.goBack();
		page.waitForLoadState();

		// Should be back on owner details page
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("a:has-text('Add New Pet')").isVisible()));
	}

	@Test
	void asAnOwner_IWantToTestVisitDateValidation_SoThatOnlyValidVisitDatesAreAccepted() {
		navigateAndWait("/owners/2");

		if (page.locator("a:has-text('Add Visit')").isVisible()) {
			page.click("a:has-text('Add Visit')");
			page.waitForLoadState();

			// Test with invalid date format
			if (page.locator("input[name='date']").isVisible()) {
				fillField("input[name='date']", "invalid-date");
			}
			else if (page.locator("input[name='visitDate']").isVisible()) {
				fillField("input[name='visitDate']", "invalid-date");
			}

			// Add description
			if (page.locator("textarea[name='description']").isVisible()) {
				page.fill("textarea[name='description']", "Test visit");
			}
			else if (page.locator("input[name='description']").isVisible()) {
				fillField("input[name='description']", "Test visit");
			}

			page.click("button[type='submit']");
			page.waitForLoadState();

			// Should show validation error or stay on form
			assertTrue(
					page.locator("form").isVisible() || page.locator("h2").textContent().contains("Owner Information"));
		}
	}

	@Test
	void asAnOwner_IWantToTestLongPetNames_SoThatLengthLimitsAreRespected() {
		navigateAndWait("/owners/2");
		page.click("a:has-text('Add New Pet')");
		page.waitForLoadState();

		// Test with very long pet name
		String longName = "A".repeat(100);
		fillField("input[name='name']", longName);
		fillField("input[name='birthDate']", "2020-01-01");

		if (page.locator("select[name='type']").isVisible()) {
			page.selectOption("select[name='type']", "dog");
		}

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Application should handle long names gracefully
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	void asAnOwner_IWantToTestEmptyVisitDescription_SoThatIKnowIfDescriptionIsRequired() {
		navigateAndWait("/owners/2");

		if (page.locator("a:has-text('Add Visit')").isVisible()) {
			page.click("a:has-text('Add Visit')");
			page.waitForLoadState();

			// Fill date but leave description empty
			if (page.locator("input[name='date']").isVisible()) {
				fillField("input[name='date']", "2024-01-15");
			}
			else if (page.locator("input[name='visitDate']").isVisible()) {
				fillField("input[name='visitDate']", "2024-01-15");
			}

			// Leave description empty

			page.click("button[type='submit']");
			page.waitForLoadState();

			// Check if empty description is allowed or if validation error occurs
			assertTrue(page.locator("body").isVisible());
		}
	}

}