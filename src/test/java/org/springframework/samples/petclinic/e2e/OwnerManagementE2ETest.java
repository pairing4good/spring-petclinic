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
 * As a user, I want to manage pet owners, so that I can maintain customer records
 */
@DisplayName("Owner Management E2E Tests")
class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to access the find owners page, so that I can search for existing customers")
	void testFindOwnersPageAccess() {
		navigateToFindOwners();

		assertPageTitle("PetClinic :: a Spring Framework demonstration");
		assertElementContainsText("h2", "Find Owners");
		assertElementVisible("input[name='lastName']");
		assertElementVisible("button[type='submit']");
		assertElementVisible("a[href='/owners/new']");
		assertElementContainsText("a[href='/owners/new']", "Add Owner");
	}

	@Test
	@DisplayName("As a user, I want to search for owners by last name, so that I can find specific customers")
	void testSearchOwnersByLastName() {
		navigateToFindOwners();

		// Search for existing owner
		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertUrlContains("/owners?lastName=Davis");
		assertElementContainsText("h2", "Owners");
		assertElementVisible("table");
		assertElementContainsText("table", "Betty Davis");
		assertElementContainsText("table", "Harold Davis");
		assertElementContainsText("table", "Name");
		assertElementContainsText("table", "Address");
		assertElementContainsText("table", "City");
		assertElementContainsText("table", "Telephone");
		assertElementContainsText("table", "Pets");
	}

	@Test
	@DisplayName("As a user, I want to see no results when searching for non-existent owners, so that I know they don't exist")
	void testSearchOwnersNoResults() {
		navigateToFindOwners();

		// Search for non-existent owner
		page.locator("input[name='lastName']").fill("NonExistentName");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementContainsText("body", "has not been found");
	}

	@Test
	@DisplayName("As a user, I want to search with empty criteria, so that I can see all owners")
	void testSearchOwnersWithEmptySearch() {
		navigateToFindOwners();

		// Search with empty last name
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementContainsText("h2", "Owners");
		assertElementVisible("table");
		// Should show multiple owners
		assertElementContainsText("table", "George Franklin");
		assertElementContainsText("table", "Betty Davis");
	}

	@Test
	@DisplayName("As a user, I want to view owner details, so that I can see their complete information")
	void testViewOwnerDetails() {
		navigateToFindOwners();

		// Search and click on an owner
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		assertUrlContains("/owners/");
		assertElementContainsText("h2", "Owner Information");
		assertElementVisible("table");
		assertElementContainsText("table", "Name");
		assertElementContainsText("table", "Address");
		assertElementContainsText("table", "City");
		assertElementContainsText("table", "Telephone");

		// Verify action buttons
		assertElementVisible("a[href*='/edit']");
		assertElementContainsText("a[href*='/edit']", "Edit Owner");
		assertElementVisible("a[href*='/pets/new']");
		assertElementContainsText("a[href*='/pets/new']", "Add New Pet");

		// Verify pets section
		assertElementContainsText("h2", "Pets and Visits");
	}

	@Test
	@DisplayName("As a user, I want to access the add owner form, so that I can register new customers")
	void testAddOwnerFormAccess() {
		navigateToAddOwner();

		assertPageTitle("PetClinic :: a Spring Framework demonstration");
		assertElementContainsText("h2", "Owner");
		assertElementVisible("input[name='firstName']");
		assertElementVisible("input[name='lastName']");
		assertElementVisible("input[name='address']");
		assertElementVisible("input[name='city']");
		assertElementVisible("input[name='telephone']");
		assertElementVisible("button[type='submit']");
		assertElementContainsText("button[type='submit']", "Add Owner");
	}

	@Test
	@DisplayName("As a user, I want to add a new owner with valid data, so that I can register new customers")
	void testAddOwnerSuccess() {
		navigateToAddOwner();

		// Fill form with valid data
		page.locator("input[name='firstName']").fill("John");
		page.locator("input[name='lastName']").fill("Doe");
		page.locator("input[name='address']").fill("123 Main Street");
		page.locator("input[name='city']").fill("Springfield");
		page.locator("input[name='telephone']").fill("5551234567");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect to owner details page
		assertUrlContains("/owners/");
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("table", "John Doe");
		assertElementContainsText("table", "123 Main Street");
		assertElementContainsText("table", "Springfield");
		assertElementContainsText("table", "5551234567");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for incomplete owner data, so that I can correct my input")
	void testAddOwnerValidationErrors() {
		navigateToAddOwner();

		// Submit form with only first name
		page.locator("input[name='firstName']").fill("John");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should stay on form with validation errors
		assertUrlContains("/owners/new");
		assertElementContainsText("body", "must not be blank");

		// Verify specific field errors
		assertElementVisible(".form-group:has(input[name='lastName']) .help-block");
		assertElementVisible(".form-group:has(input[name='address']) .help-block");
		assertElementVisible(".form-group:has(input[name='city']) .help-block");
		assertElementVisible(".form-group:has(input[name='telephone']) .help-block");
	}

	@Test
	@DisplayName("As a user, I want to see telephone format validation, so that I enter valid phone numbers")
	void testAddOwnerTelephoneValidation() {
		navigateToAddOwner();

		// Fill form with invalid telephone format
		page.locator("input[name='firstName']").fill("Jane");
		page.locator("input[name='lastName']").fill("Smith");
		page.locator("input[name='address']").fill("456 Oak Avenue");
		page.locator("input[name='city']").fill("Madison");
		page.locator("input[name='telephone']").fill("123"); // Invalid format

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should show telephone validation error
		assertUrlContains("/owners/new");
		assertElementContainsText("body", "Telephone must be a 10-digit number");
	}

	@Test
	@DisplayName("As a user, I want to edit existing owner information, so that I can update customer records")
	void testEditOwnerAccess() {
		// First find an owner
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Go to owner details
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		// Click edit owner
		page.locator("a[href*='/edit']").click();
		waitForPageLoad();

		assertUrlContains("/edit");
		assertElementContainsText("h2", "Owner");
		assertElementVisible("input[name='firstName']");
		assertElementVisible("input[name='lastName']");
		assertElementVisible("input[name='address']");
		assertElementVisible("input[name='city']");
		assertElementVisible("input[name='telephone']");
		assertElementVisible("button[type='submit']");
		assertElementContainsText("button[type='submit']", "Update Owner");

		// Form should be pre-filled with existing data
		assert !page.locator("input[name='firstName']").inputValue().isEmpty();
		assert !page.locator("input[name='lastName']").inputValue().isEmpty();
	}

	@Test
	@DisplayName("As a user, I want to update owner information, so that I can maintain accurate customer records")
	void testEditOwnerSuccess() {
		// Navigate to edit an existing owner
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		page.locator("a[href*='/edit']").click();
		waitForPageLoad();

		// Update the address
		page.locator("input[name='address']").fill("456 Updated Street");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect back to owner details
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("table", "456 Updated Street");
	}

	@Test
	@DisplayName("As a user, I want to navigate from find owners form to add owner form, so that I can easily add new customers")
	void testNavigateFromFindToAddOwner() {
		navigateToFindOwners();

		page.locator("a[href='/owners/new']").click();
		waitForPageLoad();

		assertUrlContains("/owners/new");
		assertElementContainsText("h2", "Owner");
		assertElementVisible("input[name='firstName']");
	}

	@Test
	@DisplayName("As a user, I want form fields to have proper labels, so that I understand what to enter")
	void testFormFieldLabels() {
		navigateToAddOwner();

		// Verify all form labels are present and correctly associated
		assertElementVisible("label[for='firstName'], .form-group:has(input[name='firstName'])");
		assertElementVisible("label[for='lastName'], .form-group:has(input[name='lastName'])");
		assertElementVisible("label[for='address'], .form-group:has(input[name='address'])");
		assertElementVisible("label[for='city'], .form-group:has(input[name='city'])");
		assertElementVisible("label[for='telephone'], .form-group:has(input[name='telephone'])");

		assertElementContainsText("body", "First Name");
		assertElementContainsText("body", "Last Name");
		assertElementContainsText("body", "Address");
		assertElementContainsText("body", "City");
		assertElementContainsText("body", "Telephone");
	}

}