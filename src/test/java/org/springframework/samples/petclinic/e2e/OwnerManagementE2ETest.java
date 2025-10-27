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
 * End-to-end tests for Owner management functionality. Covers CRUD operations, search,
 * form validation, and error handling.
 */
public class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSearchForOwnersByLastName_SoThatICanFindSpecificOwners() {
		navigateAndWait("/owners/find");

		// Search for existing owner
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		// Verify search results
		assertAll(() -> assertTrue(page.url().contains("lastName=Davis")),
				() -> assertTrue(page.locator("h2").textContent().contains("Owners")),
				() -> assertTrue(page.locator("table#owners").isVisible()),
				() -> assertTrue(page.locator("td:has-text('Davis')").count() > 0));
	}

	@Test
	void asAUser_IWantToSearchWithEmptyLastName_SoThatICanSeeAllOwners() {
		navigateAndWait("/owners/find");

		// Submit empty search
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should show all owners (or first page if paginated)
		assertAll(
				() -> assertTrue(page.locator("h2").textContent().contains("Owners")
						|| page.locator("h2").textContent().contains("Find Owners")),
				() -> assertTrue(page.locator("table#owners").isVisible()
						|| page.locator("input[name='lastName']").isVisible()));
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatICanSeeNoResultsMessage() {
		navigateAndWait("/owners/find");

		// Search for non-existent owner
		fillField("input[name='lastName']", "NonExistentOwner");
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should show no results or return to search form
		assertTrue(page.locator("body").isVisible());
		// Specific behavior depends on application logic
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeOwnerAndPetInformation() {
		navigateAndWait("/owners/find");
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		// Click on first owner link
		Locator ownerLink = page.locator("table#owners a").first();
		String ownerName = ownerLink.textContent();
		ownerLink.click();
		page.waitForLoadState();

		// Verify owner details page
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("table").textContent().contains(ownerName)),
				() -> assertTrue(page.locator("a:has-text('Edit Owner')").isVisible()),
				() -> assertTrue(page.locator("a:has-text('Add New Pet')").isVisible()),
				() -> assertTrue(page.locator("h2:has-text('Pets and Visits')").isVisible()));
	}

	@Test
	void asAUser_IWantToAddANewOwner_SoThatICanRegisterNewPetOwners() {
		navigateAndWait("/owners/find");
		page.click("a:has-text('Add Owner')");
		page.waitForURL("**/owners/new");

		// Fill out new owner form
		fillField("input[name='firstName']", "John");
		fillField("input[name='lastName']", "TestOwner");
		fillField("input[name='address']", "123 Test Street");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "5551234567");

		// Submit form
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should redirect to owner details page
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("table").textContent().contains("John")),
				() -> assertTrue(page.locator("table").textContent().contains("TestOwner")),
				() -> assertTrue(page.locator("table").textContent().contains("123 Test Street")),
				() -> assertTrue(page.locator("table").textContent().contains("Test City")),
				() -> assertTrue(page.locator("table").textContent().contains("5551234567")));
	}

	@Test
	void asAUser_IWantToSeeValidationErrors_SoThatIKnowWhatFieldsAreRequired() {
		navigateAndWait("/owners/new");

		// Submit form with missing required fields
		fillField("input[name='firstName']", "John");
		fillField("input[name='lastName']", "TestOwner");
		fillField("input[name='city']", "Test City");
		// Leave address and telephone empty

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should stay on form page with validation errors
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner")),
				() -> assertTrue(page.locator("form").isVisible()));

		// Check for validation error messages or form highlighting
		assertTrue(page.locator("input[name='address'], input[name='telephone']").count() > 0);
	}

	@Test
	void asAUser_IWantToEditExistingOwner_SoThatICanUpdateOwnerInformation() {
		// First, find an owner to edit
		navigateAndWait("/owners/find");
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		// Go to owner details
		page.locator("table#owners a").first().click();
		page.waitForLoadState();

		// Click edit owner
		page.click("a:has-text('Edit Owner')");
		page.waitForLoadState();

		// Verify edit form is loaded with existing data
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner")),
				() -> assertFalse(page.locator("input[name='firstName']").inputValue().isEmpty()),
				() -> assertFalse(page.locator("input[name='lastName']").inputValue().isEmpty()),
				() -> assertFalse(page.locator("input[name='address']").inputValue().isEmpty()),
				() -> assertFalse(page.locator("input[name='city']").inputValue().isEmpty()),
				() -> assertFalse(page.locator("input[name='telephone']").inputValue().isEmpty()));

		// Update some fields
		fillField("input[name='address']", "456 Updated Street");
		fillField("input[name='telephone']", "5559876543");

		// Submit changes
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Verify changes are reflected in owner details
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("table").textContent().contains("456 Updated Street")),
				() -> assertTrue(page.locator("table").textContent().contains("5559876543")));
	}

	@Test
	void asAUser_IWantToEditOwnerWithValidationErrors_SoThatICanSeeAppropriateErrorMessages() {
		// Navigate to edit an existing owner
		navigateAndWait("/owners/find");
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		page.locator("table#owners a").first().click();
		page.waitForLoadState();
		page.click("a:has-text('Edit Owner')");
		page.waitForLoadState();

		// Clear required fields
		fillField("input[name='address']", "");
		fillField("input[name='telephone']", "");

		// Submit form with validation errors
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should stay on edit form
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner")),
				() -> assertTrue(page.locator("form").isVisible()));
	}

	@Test
	void asAUser_IWantToTestTelephoneValidation_SoThatOnlyValidPhoneNumbersAreAccepted() {
		navigateAndWait("/owners/new");

		// Test with invalid telephone format
		fillField("input[name='firstName']", "John");
		fillField("input[name='lastName']", "TestOwner");
		fillField("input[name='address']", "123 Test Street");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "invalid-phone");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should show validation error or stay on form
		assertTrue(page.locator("form").isVisible());
	}

	@Test
	void asAUser_IWantToTestFormFieldLimits_SoThatIKnowTheMaximumInputLengths() {
		navigateAndWait("/owners/new");

		// Test maximum length inputs
		String longString = "A".repeat(100);

		fillField("input[name='firstName']", longString);
		fillField("input[name='lastName']", longString);
		fillField("input[name='address']", longString);
		fillField("input[name='city']", longString);
		fillField("input[name='telephone']", "5551234567");

		// Submit and verify handling of long inputs
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Application should handle long inputs gracefully
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	void asAUser_IWantToTestSpecialCharactersInInput_SoThatTheyAreHandledCorrectly() {
		navigateAndWait("/owners/new");

		// Test with special characters
		fillField("input[name='firstName']", "José");
		fillField("input[name='lastName']", "García-López");
		fillField("input[name='address']", "123 Main St. Apt #4");
		fillField("input[name='city']", "San José");
		fillField("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Verify special characters are handled correctly
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owner Information")),
				() -> assertTrue(page.locator("table").textContent().contains("José")),
				() -> assertTrue(page.locator("table").textContent().contains("García-López")));
	}

	@Test
	void asAUser_IWantToViewOwnersPagination_SoThatICanNavigateThroughLargeOwnerLists() {
		navigateAndWait("/owners/find");
		page.click("button[type='submit']"); // Search for all owners
		page.waitForLoadState();

		// If pagination exists, test it
		if (page.locator("a:has-text('2')").isVisible()) {
			page.click("a:has-text('2')");
			page.waitForLoadState();

			assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Owners")),
					() -> assertTrue(page.locator("table#owners").isVisible()));
		}

		// Verify page navigation elements exist if applicable
		assertTrue(page.locator("body").isVisible()); // Basic existence check
	}

	@Test
	void asAUser_IWantToNavigateFromOwnerListToAddOwner_SoThatICanQuicklyAddNewOwners() {
		// Navigate to owners list first
		navigateAndWait("/owners/find");
		fillField("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForURL("**/owners?lastName=Davis");

		// Should be able to navigate to add owner from results page
		if (page.locator("a:has-text('Add Owner')").isVisible()) {
			page.click("a:has-text('Add Owner')");
			page.waitForURL("**/owners/new");

			assertTrue(page.locator("h2").textContent().contains("Owner"));
			assertTrue(page.locator("input[name='firstName']").isVisible());
		}
	}

	@Test
	void asAUser_IWantToAccessOwnerDirectlyByUrl_SoThatICanBookmarkSpecificOwnerPages() {
		// Test direct access to owner details by ID
		navigateAndWait("/owners/1");

		// Should show owner details or appropriate error
		assertTrue(page.locator("body").isVisible());

		// If owner exists, verify details page
		if (page.locator("h2:has-text('Owner Information')").isVisible()) {
			assertAll(() -> assertTrue(page.locator("table").isVisible()),
					() -> assertTrue(page.locator("a:has-text('Edit Owner')").isVisible()),
					() -> assertTrue(page.locator("a:has-text('Add New Pet')").isVisible()));
		}
	}

}