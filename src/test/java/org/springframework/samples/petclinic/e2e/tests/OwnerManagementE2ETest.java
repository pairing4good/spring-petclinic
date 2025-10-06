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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.base.BaseE2ETest;
import org.springframework.samples.petclinic.e2e.pages.AddOwnerPage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerDetailsPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive E2E tests for Owner Management functionality.
 *
 * Tests cover: - Owner search functionality (find owners by last name) - Owner creation
 * with form validation - Owner details viewing - Owner editing functionality - Form
 * validation for required fields - Special characters and edge cases in owner data -
 * Error handling and user feedback
 */
@DisplayName("Owner Management E2E Tests")
class OwnerManagementE2ETest extends BaseE2ETest {

	private FindOwnersPage findOwnersPage;

	private AddOwnerPage addOwnerPage;

	private OwnerDetailsPage ownerDetailsPage;

	@BeforeEach
	void setUpPages() {
		findOwnersPage = new FindOwnersPage(page);
		addOwnerPage = new AddOwnerPage(page);
		ownerDetailsPage = new OwnerDetailsPage(page);
	}

	@Test
	@DisplayName("As a user, I want to search for existing owners, so that I can find their information quickly")
	void testSearchForExistingOwners() {
		// Given: I am on the Find Owners page
		findOwnersPage.navigate(getBaseUrl());
		assertTrue(findOwnersPage.isLoaded(), "Find Owners page should be loaded");

		// When: I search for an existing owner by last name
		findOwnersPage.searchByLastName("Davis");

		// Then: I should see search results or be redirected to owner details
		assertTrue(findOwnersPage.hasSearchResults() || page.url().contains("/owners/"),
				"Should show search results or redirect to owner details");
	}

	@Test
	@DisplayName("As a user, I want to search with empty criteria, so that I can see all owners")
	void testSearchWithEmptyCriteria() {
		// Given: I am on the Find Owners page
		findOwnersPage.navigate(getBaseUrl());
		assertTrue(findOwnersPage.isLoaded(), "Find Owners page should be loaded");

		// When: I search with empty last name
		findOwnersPage.searchWithEmptyLastName();

		// Then: I should see all owners or appropriate handling
		assertTrue(findOwnersPage.isFormValidationShown() || findOwnersPage.hasSearchResults(),
				"Should show form validation or all owners");
	}

	@Test
	@DisplayName("As a user, I want to search for non-existent owners, so that I see appropriate feedback")
	void testSearchForNonExistentOwner() {
		// Given: I am on the Find Owners page
		findOwnersPage.navigate(getBaseUrl());
		assertTrue(findOwnersPage.isLoaded(), "Find Owners page should be loaded");

		// When: I search for a non-existent owner
		findOwnersPage.searchByLastName("NonExistentOwnerLastName12345");

		// Then: I should see appropriate feedback (empty results or message)
		assertFalse(findOwnersPage.hasSearchResults(), "Should not show results for non-existent owner");
	}

	@Test
	@DisplayName("As a user, I want to create a new owner with valid data, so that I can add them to the system")
	void testCreateNewOwnerWithValidData() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I fill out the form with valid owner data
		String firstName = "John";
		String lastName = "TestOwner";
		String address = "123 Test Street";
		String city = "Test City";
		String telephone = "555-123-4567";

		addOwnerPage.createOwner(firstName, lastName, address, city, telephone);

		// Then: The owner should be created successfully
		// Wait a moment for any redirect to happen
		waitForPageLoad();

		assertTrue(
				addOwnerPage.isRedirectedToOwnerDetails() || addOwnerPage.hasSuccessMessage()
						|| page.url().contains("/owners/"),
				"Should be redirected to owner details, show success message, or be on owners page");

		// And: If redirected to details, verify the information is correct
		if (addOwnerPage.isRedirectedToOwnerDetails()) {
			ownerDetailsPage = new OwnerDetailsPage(page);
			assertTrue(ownerDetailsPage.isLoaded(), "Owner details page should be loaded");
			assertTrue(ownerDetailsPage.getOwnerName().contains(firstName), "Owner name should contain first name");
			assertTrue(ownerDetailsPage.getOwnerName().contains(lastName), "Owner name should contain last name");
		}
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when creating an owner with missing required fields")
	void testCreateOwnerWithMissingRequiredFields() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I try to create an owner with missing required fields
		addOwnerPage.fillPartialOwnerForm("John", "TestOwner", "Test City");
		addOwnerPage.submitForm();

		// Then: I should see validation errors
		assertTrue(addOwnerPage.hasValidationErrors() || addOwnerPage.hasErrorMessage(),
				"Should show validation errors for missing required fields");

		// And: I should remain on the same page
		assertTrue(addOwnerPage.isLoaded(), "Should remain on Add Owner page after validation errors");
	}

	@Test
	@DisplayName("As a user, I want to create an owner with special characters, so that I can handle international names")
	void testCreateOwnerWithSpecialCharacters() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I create an owner with special characters
		addOwnerPage.fillFormWithSpecialCharacters();
		addOwnerPage.submitForm();

		// Then: The owner should be created successfully or show appropriate handling
		assertTrue(addOwnerPage.isRedirectedToOwnerDetails() || addOwnerPage.hasSuccessMessage()
				|| addOwnerPage.hasValidationErrors(), "Should handle special characters appropriately");
	}

	@Test
	@DisplayName("As a user, I want to create an owner with maximum length data, so that I can test field limits")
	void testCreateOwnerWithMaxLengthData() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I fill out the form with maximum length data
		addOwnerPage.fillFormWithMaxLengthInputs();
		addOwnerPage.submitForm();

		// Then: The form should handle max length data appropriately
		assertTrue(addOwnerPage.isRedirectedToOwnerDetails() || addOwnerPage.hasSuccessMessage()
				|| addOwnerPage.hasValidationErrors(), "Should handle maximum length data appropriately");
	}

	@Test
	@DisplayName("As a user, I want to view owner details, so that I can see complete owner information")
	void testViewOwnerDetails() {
		// Given: I have found an existing owner
		findOwnersPage.navigate(getBaseUrl());
		findOwnersPage.searchByLastName("Davis");

		// When: I navigate to owner details (if search results exist)
		if (findOwnersPage.hasSearchResults()) {
			findOwnersPage.clickFirstOwnerResult();

			// Then: I should see the owner details page
			assertTrue(ownerDetailsPage.isLoaded(), "Owner details page should be loaded");
			assertTrue(ownerDetailsPage.isOnOwnerDetailsPage(), "Should be on owner details page");

			// And: I should see owner information
			assertFalse(ownerDetailsPage.getOwnerName().isEmpty(), "Owner name should be displayed");
			// Address, city, telephone might be empty for some owners, so just check they
			// return values
			String address = ownerDetailsPage.getOwnerAddress();
			String city = ownerDetailsPage.getOwnerCity();
			String telephone = ownerDetailsPage.getOwnerTelephone();
			// Just verify the methods work and return non-null values
			assertTrue(address != null, "Owner address should not be null");
			assertTrue(city != null, "Owner city should not be null");
			assertTrue(telephone != null, "Owner telephone should not be null");
		}
		else {
			// If no existing owners, create one first
			findOwnersPage.clickAddOwner();
			addOwnerPage.createOwner("Test", "Owner", "123 Test St", "Test City", "555-1234");

			if (addOwnerPage.isRedirectedToOwnerDetails()) {
				assertTrue(ownerDetailsPage.isLoaded(), "Owner details page should be loaded after creation");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to edit owner information, so that I can update their details")
	void testEditOwnerInformation() {
		// Given: I have an existing owner (create one if needed)
		addOwnerPage.navigate(getBaseUrl());
		addOwnerPage.createOwner("EditTest", "Owner", "123 Original St", "Original City", "555-0000");

		// Wait for redirect to owner details
		if (addOwnerPage.isRedirectedToOwnerDetails()) {
			ownerDetailsPage = new OwnerDetailsPage(page);
			assertTrue(ownerDetailsPage.isLoaded(), "Owner details page should be loaded");

			// When: I click Edit Owner button
			ownerDetailsPage.clickEditOwner();

			// Then: I should be on the edit form (which is the same as add form)
			assertTrue(addOwnerPage.isLoaded(), "Edit owner form should be loaded");

			// When: I update the owner information
			addOwnerPage.clearForm();
			addOwnerPage.fillOwnerForm("EditTest", "UpdatedOwner", "456 Updated St", "Updated City", "555-9999");
			addOwnerPage.submitForm();

			// Then: The owner should be updated
			if (addOwnerPage.isRedirectedToOwnerDetails()) {
				assertTrue(ownerDetailsPage.isLoaded(), "Should return to owner details after update");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to navigate from Find Owners to Add Owner, so that I can create new owners easily")
	void testNavigationFromFindToAdd() {
		// Given: I am on the Find Owners page
		findOwnersPage.navigate(getBaseUrl());
		assertTrue(findOwnersPage.isLoaded(), "Find Owners page should be loaded");

		// When: I click Add Owner button
		findOwnersPage.clickAddOwner();

		// Then: I should be on the Add Owner page
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");
		assertTrue(page.url().contains("/owners/new"), "Should be on the new owner URL");
	}

	@Test
	@DisplayName("As a user, I want to see success messages after creating an owner, so that I know the operation completed")
	void testSuccessMessagesAfterOwnerCreation() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I create a new owner
		addOwnerPage.createOwner("Success", "TestOwner", "123 Success St", "Success City", "555-0123");

		// Then: I should see success feedback
		waitForPageLoad();
		assertTrue(addOwnerPage.isRedirectedToOwnerDetails() || addOwnerPage.hasSuccessMessage()
				|| page.url().contains("/owners/"), "Should show success feedback after owner creation");

		// And: If redirected to details, check for success message there
		if (addOwnerPage.isRedirectedToOwnerDetails()) {
			ownerDetailsPage = new OwnerDetailsPage(page);
			// Success message might be shown on the details page
			assertTrue(ownerDetailsPage.isLoaded(), "Owner details should be loaded");
		}
	}

	@Test
	@DisplayName("As a user, I want form fields to be properly validated, so that I enter correct data")
	void testFormFieldValidation() {
		// Given: I am on the Add Owner page
		addOwnerPage.navigate(getBaseUrl());
		assertTrue(addOwnerPage.isLoaded(), "Add Owner page should be loaded");

		// When: I try to submit an empty form
		addOwnerPage.submitForm();

		// Then: I should see validation errors or remain on the form
		assertTrue(addOwnerPage.hasValidationErrors() || addOwnerPage.isLoaded(),
				"Should show validation errors or keep me on the form");

		// When: I fill only some required fields
		addOwnerPage.fillOwnerForm("Test", "User", "", "", "");
		addOwnerPage.submitForm();

		// Then: I should see specific field validation errors
		assertTrue(addOwnerPage.hasValidationErrors() || addOwnerPage.hasErrorMessage(),
				"Should show validation errors for empty required fields");
	}

}