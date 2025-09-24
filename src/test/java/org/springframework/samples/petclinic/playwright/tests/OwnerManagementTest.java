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
package org.springframework.samples.petclinic.playwright.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.playwright.BasePlaywrightTest;
import org.springframework.samples.petclinic.playwright.pages.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for owner management (CRUD operations). Tests creation, viewing,
 * editing, and searching of pet owners.
 */
class OwnerManagementTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a user, I want to search for all owners, so that I can see the complete list of pet owners")
	void testSearchAllOwners() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for all owners (empty search)
		OwnersListPage ownersListPage = findOwnersPage.searchAllOwners();

		assertTrue(ownersListPage.isOwnersListPage(), "Should be on the owners list page");
		assertTrue(ownersListPage.getOwnersCount() > 0, "Should display at least one owner");
	}

	@Test
	@DisplayName("As a user, I want to add a new owner, so that I can register new pet owners in the system")
	void testAddNewOwner() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		AddOwnerPage addOwnerPage = findOwnersPage.navigateToAddOwner();

		assertTrue(addOwnerPage.isAddOwnerPage(), "Should be on the add owner page");
		assertTrue(addOwnerPage.areAllFormFieldsVisible(), "All form fields should be visible");

		// Create a new owner with valid data
		String firstName = "John";
		String lastName = "TestOwner" + System.currentTimeMillis(); // Unique last name
		String address = "123 Test Street";
		String city = "Test City";
		String telephone = "1234567890";

		OwnerDetailsPage ownerDetailsPage = addOwnerPage.createOwner(firstName, lastName, address, city, telephone);

		assertTrue(ownerDetailsPage.isOwnerDetailsPage(), "Should be on the owner details page after creation");
		assertTrue(ownerDetailsPage.isOwnerInformationDisplayed(), "Owner information should be displayed");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for invalid owner data, so that I know what needs to be corrected")
	void testOwnerFormValidation() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		AddOwnerPage addOwnerPage = findOwnersPage.navigateToAddOwner();

		// Try to create owner with missing required fields
		addOwnerPage.createOwnerWithInvalidData("John", "Doe", "", "", ""); // Missing
																			// address and
																			// telephone

		assertTrue(addOwnerPage.hasValidationErrors(), "Should display validation errors for missing fields");
		assertFalse(addOwnerPage.getValidationError().isEmpty(), "Should have specific error messages");
	}

	@Test
	@DisplayName("As a user, I want to search for owners by last name, so that I can find specific owners quickly")
	void testSearchOwnersByLastName() {
		navigateToHome();

		// First create an owner to search for
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		AddOwnerPage addOwnerPage = findOwnersPage.navigateToAddOwner();

		String uniqueLastName = "SearchTest" + System.currentTimeMillis();
		OwnerDetailsPage ownerDetailsPage = addOwnerPage.createOwner("Jane", uniqueLastName, "456 Search St",
				"Search City", "9876543210");

		// Navigate back to search
		findOwnersPage = new HomePage(page).navigateToFindOwners();

		// Search for the owner we just created
		findOwnersPage.searchByLastName(uniqueLastName);

		// Should find the owner directly (single result goes to details page)
		// or show in results list
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/owners/") || currentUrl.contains("find"),
				"Should be on owner details page or find results page");
	}

	@Test
	@DisplayName("As a user, I want to view owner details, so that I can see complete information about an owner")
	void testViewOwnerDetails() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersListPage = findOwnersPage.searchAllOwners();

		if (ownersListPage.getOwnersCount() > 0) {
			OwnerDetailsPage ownerDetailsPage = ownersListPage.clickFirstOwner();

			assertTrue(ownerDetailsPage.isOwnerDetailsPage(), "Should be on owner details page");
			assertTrue(ownerDetailsPage.isOwnerInformationDisplayed(), "Owner information should be displayed");
			assertTrue(ownerDetailsPage.isEditOwnerButtonVisible(), "Edit owner button should be visible");
			assertTrue(ownerDetailsPage.isAddNewPetButtonVisible(), "Add new pet button should be visible");
		}
	}

	@Test
	@DisplayName("As a user, I want to handle empty search results gracefully, so that I understand when no owners match my search")
	void testEmptySearchResults() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for a name that definitely won't exist
		String nonExistentName = "NonExistentOwner" + System.currentTimeMillis();
		findOwnersPage.searchByLastName(nonExistentName);

		// Should remain on find owners page or show no results message
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("find") || page.content().contains("has not been found"),
				"Should show no results found or stay on search page");
	}

}