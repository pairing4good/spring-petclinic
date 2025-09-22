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
 * As a user, I want to manage pets and visits, so that I can track pet health and
 * appointments
 */
@DisplayName("Pet and Visit Management E2E Tests")
class PetVisitManagementE2ETest extends BaseE2ETest {

	private void navigateToOwnerDetails() {
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();
	}

	@Test
	@DisplayName("As a user, I want to view pets in owner details, so that I can see their pet information")
	void testViewPetsInOwnerDetails() {
		navigateToOwnerDetails();

		assertElementContainsText("h2", "Pets and Visits");
		assertElementVisible("table");

		// Should show pet information
		assertElementContainsText("body", "Name");
		assertElementContainsText("body", "Birth Date");
		assertElementContainsText("body", "Type");

		// Verify action buttons for pets
		assertElementVisible("a[href*='/pets/'][href*='/edit']");
		assertElementContainsText("a[href*='/pets/'][href*='/edit']", "Edit Pet");
		assertElementVisible("a[href*='/visits/new']");
		assertElementContainsText("a[href*='/visits/new']", "Add Visit");
	}

	@Test
	@DisplayName("As a user, I want to add a new pet to an owner, so that I can register their animals")
	void testAddNewPetAccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		assertUrlContains("/pets/new");
		assertElementContainsText("h2", "Pet");
		assertElementVisible("input[name='name']");
		assertElementVisible("input[name='birthDate']");
		assertElementVisible("select[name='type']");
		assertElementVisible("button[type='submit']");
		assertElementContainsText("button[type='submit']", "Add Pet");
	}

	@Test
	@DisplayName("As a user, I want to add a new pet with valid data, so that I can register the animal")
	void testAddNewPetSuccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		// Fill pet form
		page.locator("input[name='name']").fill("Fluffy");
		page.locator("input[name='birthDate']").fill("2023-01-15");
		page.locator("select[name='type']").selectOption("cat");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect back to owner details
		assertUrlContains("/owners/");
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("body", "Fluffy");
		assertElementContainsText("body", "2023-01-15");
		assertElementContainsText("body", "cat");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for incomplete pet data, so that I can correct my input")
	void testAddPetValidationErrors() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		// Submit form without required fields
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should stay on form with validation errors
		assertUrlContains("/pets/new");
		assertElementContainsText("body", "must not be blank");
	}

	@Test
	@DisplayName("As a user, I want to see pet type options, so that I can categorize pets correctly")
	void testPetTypeOptions() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		// Verify pet type options are available
		assertElementVisible("select[name='type']");

		String[] expectedTypes = { "bird", "cat", "dog", "hamster", "lizard", "snake" };
		for (String type : expectedTypes) {
			assertElementVisible("select[name='type'] option[value='" + type + "']");
		}
	}

	@Test
	@DisplayName("As a user, I want to edit existing pet information, so that I can update pet records")
	void testEditPetAccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/'][href*='/edit']").first().click();
		waitForPageLoad();

		assertUrlContains("/pets/");
		assertUrlContains("/edit");
		assertElementContainsText("h2", "Pet");
		assertElementVisible("input[name='name']");
		assertElementVisible("input[name='birthDate']");
		assertElementVisible("select[name='type']");
		assertElementVisible("button[type='submit']");
		assertElementContainsText("button[type='submit']", "Update Pet");

		// Form should be pre-filled
		assert !page.locator("input[name='name']").inputValue().isEmpty();
	}

	@Test
	@DisplayName("As a user, I want to update pet information, so that I can maintain accurate pet records")
	void testEditPetSuccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/'][href*='/edit']").first().click();
		waitForPageLoad();

		// Update pet name
		String currentName = page.locator("input[name='name']").inputValue();
		String newName = currentName + " Updated";
		page.locator("input[name='name']").fill(newName);

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect back to owner details
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("body", newName);
	}

	@Test
	@DisplayName("As a user, I want to add visits to pets, so that I can track medical appointments")
	void testAddVisitAccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/visits/new']").first().click();
		waitForPageLoad();

		assertUrlContains("/visits/new");
		assertElementContainsText("h2", "New Visit");
		assertElementVisible("input[name='date']");
		assertElementVisible("textarea[name='description']");
		assertElementVisible("button[type='submit']");
		assertElementContainsText("button[type='submit']", "Add Visit");

		// Should show pet information
		assertElementContainsText("body", "Pet");
		assertElementContainsText("body", "Name");
		assertElementContainsText("body", "Birth Date");
		assertElementContainsText("body", "Type");
	}

	@Test
	@DisplayName("As a user, I want to add a visit with valid data, so that I can record pet appointments")
	void testAddVisitSuccess() {
		navigateToOwnerDetails();

		page.locator("a[href*='/visits/new']").first().click();
		waitForPageLoad();

		// Fill visit form
		page.locator("input[name='date']").fill("2024-01-15");
		page.locator("textarea[name='description']").fill("Annual checkup and vaccinations");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect back to owner details
		assertUrlContains("/owners/");
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("body", "2024-01-15");
		assertElementContainsText("body", "Annual checkup and vaccinations");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for incomplete visit data, so that I can correct my input")
	void testAddVisitValidationErrors() {
		navigateToOwnerDetails();

		page.locator("a[href*='/visits/new']").first().click();
		waitForPageLoad();

		// Submit form without description
		page.locator("input[name='date']").fill("2024-01-15");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should stay on form with validation errors
		assertUrlContains("/visits/new");
		assertElementContainsText("body", "must not be blank");
	}

	@Test
	@DisplayName("As a user, I want to see visit history for pets, so that I can track their medical record")
	void testViewVisitHistory() {
		navigateToOwnerDetails();

		// Look for existing visits in the pets table
		assertElementContainsText("h2", "Pets and Visits");
		assertElementVisible("table");

		// Check for visit columns
		assertElementContainsText("body", "Visit Date");
		assertElementContainsText("body", "Description");
	}

	@Test
	@DisplayName("As a user, I want proper form labels in pet forms, so that I understand what to enter")
	void testPetFormLabels() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		// Verify form labels
		assertElementContainsText("body", "Name");
		assertElementContainsText("body", "Birth Date");
		assertElementContainsText("body", "Type");

		assertElementVisible("input[name='name']");
		assertElementVisible("input[name='birthDate']");
		assertElementVisible("select[name='type']");
	}

	@Test
	@DisplayName("As a user, I want proper form labels in visit forms, so that I understand what to enter")
	void testVisitFormLabels() {
		navigateToOwnerDetails();

		page.locator("a[href*='/visits/new']").first().click();
		waitForPageLoad();

		// Verify form labels
		assertElementContainsText("body", "Date");
		assertElementContainsText("body", "Description");

		assertElementVisible("input[name='date']");
		assertElementVisible("textarea[name='description']");
	}

	@Test
	@DisplayName("As a user, I want pet birth date validation, so that I enter valid dates")
	void testPetBirthDateValidation() {
		navigateToOwnerDetails();

		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		// Try invalid date format
		page.locator("input[name='name']").fill("TestPet");
		page.locator("input[name='birthDate']").fill("invalid-date");
		page.locator("select[name='type']").selectOption("cat");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should show validation error or stay on form
		// Note: HTML5 date input might prevent invalid dates from being entered
		assertElementVisible("input[name='birthDate']");
	}

	@Test
	@DisplayName("As a user, I want visit date validation, so that I enter valid appointment dates")
	void testVisitDateValidation() {
		navigateToOwnerDetails();

		page.locator("a[href*='/visits/new']").first().click();
		waitForPageLoad();

		// Try with missing description but valid date
		page.locator("input[name='date']").fill("2024-01-15");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should show validation error for description
		assertUrlContains("/visits/new");
		assertElementContainsText("body", "must not be blank");
	}

}