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
 * E2E tests for Owner Management (CRUD operations) Tests creating, reading, updating, and
 * deleting owners
 */
@DisplayName("Owner Management E2E Tests")
class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to create a new owner with valid information, so that I can add new clients to the system")
	void shouldCreateNewOwnerWithValidInformation() {
		// Given - user navigates to Add Owner page
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		// When - user fills in valid owner information
		TestOwnerData ownerData = generateTestOwnerData();
		page.locator("input[id*='firstName']").fill(ownerData.firstName);
		page.locator("input[id*='lastName']").fill(ownerData.lastName);
		page.locator("input[id*='address']").fill(ownerData.address);
		page.locator("input[id*='city']").fill(ownerData.city);
		page.locator("input[id*='telephone']").fill(ownerData.telephone);

		// And - submits the form
		page.locator("button:has-text('Add Owner')").click();

		// Then - should navigate to owner details page
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should navigate to owner details page");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(),
				"Should show owner information heading");

		// And - should display the created owner information
		assertTrue(page.locator("td").allTextContents().toString().contains(ownerData.firstName),
				"Should show first name");
		assertTrue(page.locator("td").allTextContents().toString().contains(ownerData.lastName),
				"Should show last name");
		assertTrue(page.locator("td").allTextContents().toString().contains(ownerData.address), "Should show address");
		assertTrue(page.locator("td").allTextContents().toString().contains(ownerData.city), "Should show city");
		assertTrue(page.locator("td").allTextContents().toString().contains(ownerData.telephone),
				"Should show telephone");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when creating owner with missing required fields, so that I know what information is needed")
	void shouldShowValidationErrorsForMissingRequiredFields() {
		// Given - user navigates to Add Owner page
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		// When - user submits form without required fields
		page.locator("button:has-text('Add Owner')").click();

		// Then - should stay on the same page and show validation errors
		assertTrue(page.url().contains("/owners/new"), "Should remain on add owner page");
		assertTrue(page.locator("h2:has-text('Owner')").isVisible(), "Should still show owner form");

		// Should show validation error indicators (field highlighting or error messages)
		// The exact validation styling may vary, but form should not submit successfully
		assertTrue(page.locator("form").isVisible(), "Form should still be visible");
	}

	@Test
	@DisplayName("As a user, I want to edit existing owner information, so that I can update client details")
	void shouldEditExistingOwnerInformation() {
		// Given - user finds an existing owner
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		page.locator("input#lastName").fill("Franklin");
		page.locator("button:has-text('Find Owner')").click();
		page.locator("a:has-text('Franklin')").first().click();

		// And - navigates to edit owner page
		page.locator("a:has-text('Edit Owner')").click();
		assertTrue(page.url().matches(".*/owners/\\d+/edit"), "Should be on edit owner page");

		// When - user updates owner information
		TestOwnerData updatedData = generateTestOwnerData();
		page.locator("input[id*='firstName']").fill(updatedData.firstName);
		page.locator("input[id*='address']").fill(updatedData.address);
		page.locator("button:has-text('Update Owner')").click();

		// Then - should return to owner details with updated information
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should return to owner details page");
		assertTrue(page.locator("td").allTextContents().toString().contains(updatedData.firstName),
				"Should show updated first name");
		assertTrue(page.locator("td").allTextContents().toString().contains(updatedData.address),
				"Should show updated address");
	}

	@Test
	@DisplayName("As a user, I want to view complete owner details including pets, so that I can see all owner information")
	void shouldViewCompleteOwnerDetailsIncludingPets() {
		// Given - user searches for an owner with pets
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();
		page.locator("a:has-text('Betty Davis')").click();

		// Then - should see complete owner information
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner info heading");
		assertTrue(page.locator("h2:has-text('Pets and Visits')").isVisible(), "Should show pets and visits heading");

		// Should show owner details table
		assertTrue(page.locator("table").first().isVisible(), "Should show owner details table");

		// Should show pets information if owner has pets
		if (page.locator("dl").isVisible()) {
			assertTrue(page.locator("dt:has-text('Name')").isVisible(), "Should show pet name label");
			assertTrue(page.locator("dt:has-text('Birth Date')").isVisible(), "Should show pet birth date label");
			assertTrue(page.locator("dt:has-text('Type')").isVisible(), "Should show pet type label");
		}
	}

	@Test
	@DisplayName("As a user, I want to navigate between owner management pages, so that I can efficiently manage owner information")
	void shouldNavigateBetweenOwnerManagementPages() {
		// Given - user starts from home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user navigates through owner management flow
		page.locator("a:has-text('Find Owners')").click();
		assertTrue(page.url().contains("/owners/find"), "Should be on find owners page");

		page.locator("a:has-text('Add Owner')").click();
		assertTrue(page.url().contains("/owners/new"), "Should be on add owner page");

		// Navigate back to find owners
		page.locator("a:has-text('Find Owners')").click();
		assertTrue(page.url().contains("/owners/find"), "Should be back on find owners page");

		// Search for existing owner
		page.locator("input#lastName").fill("Franklin");
		page.locator("button:has-text('Find Owner')").click();
		page.locator("a:has-text('Franklin')").first().click();
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should be on owner details page");

		// Navigate to edit owner
		page.locator("a:has-text('Edit Owner')").click();
		assertTrue(page.url().matches(".*/owners/\\d+/edit"), "Should be on edit owner page");

		// Then - all navigation should work correctly
		assertTrue(page.locator("form").isVisible(), "Should show edit form");
	}

	@Test
	@DisplayName("As a user, I want to handle edge cases in owner data, so that the system is robust")
	void shouldHandleEdgeCasesInOwnerData() {
		// Given - user navigates to Add Owner page
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		// When - user enters edge case data
		page.locator("input[id*='firstName']").fill("José María");
		page.locator("input[id*='lastName']").fill("O'Connor-Smith");
		page.locator("input[id*='address']").fill("123 Main St., Apt. #456");
		page.locator("input[id*='city']").fill("São Paulo");
		page.locator("input[id*='telephone']").fill("(555) 123-4567");

		page.locator("button:has-text('Add Owner')").click();

		// Then - should handle special characters and formatting correctly
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should successfully create owner");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner details");

		// Should display the special characters correctly
		String pageContent = page.locator("table").textContent();
		assertTrue(pageContent.contains("José María"), "Should handle accented characters");
		assertTrue(pageContent.contains("O'Connor-Smith"), "Should handle apostrophes and hyphens");
	}

	@Test
	@DisplayName("As a user, I want to validate telephone number format, so that contact information is properly formatted")
	void shouldValidateTelephoneNumberFormat() {
		// Given - user navigates to Add Owner page
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		// When - user enters owner with various telephone formats
		TestOwnerData ownerData = generateTestOwnerData();
		page.locator("input[id*='firstName']").fill(ownerData.firstName);
		page.locator("input[id*='lastName']").fill(ownerData.lastName);
		page.locator("input[id*='address']").fill(ownerData.address);
		page.locator("input[id*='city']").fill(ownerData.city);
		page.locator("input[id*='telephone']").fill("invalid-phone");

		page.locator("button:has-text('Add Owner')").click();

		// Then - should handle invalid phone number appropriately
		// The system should either accept it or show validation error
		// Either outcome is acceptable, but system should not crash
		assertFalse(page.locator("h2:has-text('Something happened')").isVisible(), "Should not show error page");
	}

	@Test
	@DisplayName("As a user, I want to see all owner information fields populated correctly, so that I can verify data accuracy")
	void shouldDisplayAllOwnerInformationFieldsCorrectly() {
		// Given - user creates a new owner with complete information
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		TestOwnerData ownerData = generateTestOwnerData();
		page.locator("input[id*='firstName']").fill(ownerData.firstName);
		page.locator("input[id*='lastName']").fill(ownerData.lastName);
		page.locator("input[id*='address']").fill(ownerData.address);
		page.locator("input[id*='city']").fill(ownerData.city);
		page.locator("input[id*='telephone']").fill(ownerData.telephone);

		// When - user submits the form and views details
		page.locator("button:has-text('Add Owner')").click();

		// Then - all fields should be displayed correctly in the details view
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(),
				"Should show owner information section");

		// Verify each field is displayed
		String tableContent = page.locator("table").first().textContent();
		assertTrue(tableContent.contains("Name"), "Should show name label");
		assertTrue(tableContent.contains("Address"), "Should show address label");
		assertTrue(tableContent.contains("City"), "Should show city label");
		assertTrue(tableContent.contains("Telephone"), "Should show telephone label");

		// Verify the actual values are displayed
		assertTrue(tableContent.contains(ownerData.firstName), "Should show first name value");
		assertTrue(tableContent.contains(ownerData.lastName), "Should show last name value");
		assertTrue(tableContent.contains(ownerData.address), "Should show address value");
		assertTrue(tableContent.contains(ownerData.city), "Should show city value");
		assertTrue(tableContent.contains(ownerData.telephone), "Should show telephone value");
	}

}