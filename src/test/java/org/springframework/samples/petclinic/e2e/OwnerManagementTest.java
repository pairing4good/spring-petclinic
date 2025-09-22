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

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.pages.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright E2E tests for owner management functionality. As a user, I want to be able
 * to search, view, create, and edit owners with comprehensive validation of all form
 * fields and error conditions.
 */
class OwnerManagementTest extends PlaywrightTestBase {

	@Test
	void asAUserIShouldBeAbleToSearchForAllOwners() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners page
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for all owners (empty search)
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Verify owners are displayed
		assertTrue(ownersPage.isOwnersListPage(), "Should be on owners list page");
		assertTrue(ownersPage.hasOwners(), "Should display owners");
		assertTrue(ownersPage.getOwnersCount() > 0, "Should have at least one owner");
		assertTrue(ownersPage.isPaginationPresent(), "Pagination should be present");
	}

	@Test
	void asAUserIShouldBeAbleToSearchForOwnersByLastName() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for owners with last name "Davis"
		OwnersListPage ownersPage = findOwnersPage.searchByLastName("Davis");

		// Verify search results
		assertTrue(ownersPage.isOwnersListPage(), "Should be on owners list page");
		assertTrue(ownersPage.hasOwners(), "Should find owners with last name Davis");

		// Verify that all results contain "Davis"
		var ownerNames = ownersPage.getOwnerNames();
		assertFalse(ownerNames.isEmpty(), "Should have found owners");
		for (String name : ownerNames) {
			assertTrue(name.contains("Davis"), "All results should contain 'Davis'");
		}
	}

	@Test
	void asAUserIShouldBeAbleToViewOwnerDetails() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Click on the first owner
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);

		// Verify owner details page
		assertTrue(detailsPage.isOwnerDetailsPage(), "Should be on owner details page");
		assertNotNull(detailsPage.getOwnerName(), "Owner name should be displayed");
		assertNotNull(detailsPage.getOwnerAddress(), "Owner address should be displayed");
		assertNotNull(detailsPage.getOwnerCity(), "Owner city should be displayed");
		assertNotNull(detailsPage.getOwnerTelephone(), "Owner telephone should be displayed");
	}

	@Test
	void asAUserIShouldBeAbleToCreateANewOwner() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Click Add Owner
		CreateOwnerPage createPage = findOwnersPage.clickAddOwner();

		// Verify we're on the create owner page
		assertTrue(createPage.isCreateOwnerPage(), "Should be on create owner page");

		// Fill in owner information
		String uniqueLastName = "TestOwner" + System.currentTimeMillis();
		createPage.fillOwnerForm("John", uniqueLastName, "123 Test Street", "Test City", "5551234567");

		// Submit the form
		OwnerDetailsPage detailsPage = createPage.submitForm();

		// Verify owner was created successfully
		assertTrue(detailsPage.isOwnerDetailsPage(), "Should be redirected to owner details");
		assertTrue(detailsPage.getOwnerName().contains("John"), "Should display correct first name");
		assertTrue(detailsPage.getOwnerName().contains(uniqueLastName), "Should display correct last name");
	}

	@Test
	void asAUserIShouldSeeValidationErrorsWhenCreatingOwnerWithInvalidData() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		CreateOwnerPage createPage = findOwnersPage.clickAddOwner();

		// Try to submit form without required fields
		createPage.clearForm();
		CreateOwnerPage pageWithErrors = createPage.submitFormWithErrors();

		// Verify validation errors are shown
		assertTrue(pageWithErrors.hasValidationErrors(), "Should show validation errors");
	}

	@Test
	void asAUserIShouldBeAbleToEditAnExistingOwner() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Get the first owner and go to details
		String originalOwnerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(originalOwnerName);

		// Click Edit Owner
		EditOwnerPage editPage = detailsPage.clickEditOwner();

		// Verify we're on the edit page
		assertTrue(editPage.isEditOwnerPage(), "Should be on edit owner page");

		// Verify the form is pre-populated
		assertNotNull(editPage.getCurrentFirstName(), "First name should be pre-populated");
		assertNotNull(editPage.getCurrentLastName(), "Last name should be pre-populated");

		// Update owner information
		String uniqueFirstName = "Updated" + System.currentTimeMillis();
		editPage.updateOwnerInfo(uniqueFirstName, editPage.getCurrentLastName(), "456 Updated Street", "Updated City",
				"5559876543");

		// Submit the update
		OwnerDetailsPage updatedDetailsPage = editPage.submitUpdate();

		// Verify the update was successful
		assertTrue(updatedDetailsPage.isOwnerDetailsPage(), "Should be back on details page");
		assertTrue(updatedDetailsPage.getOwnerName().contains(uniqueFirstName), "Should show updated first name");
		assertTrue(updatedDetailsPage.getOwnerAddress().contains("456 Updated Street"), "Should show updated address");
	}

	@Test
	void asAUserIShouldBeAbleToNavigatePaginationInOwnersList() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Verify pagination is present
		assertTrue(ownersPage.isPaginationPresent(), "Pagination should be present");

		// Get owners count on first page
		int firstPageCount = ownersPage.getOwnersCount();
		var firstPageOwners = ownersPage.getOwnerNames();

		// Go to next page if available
		ownersPage.goToNextPage();

		// Verify we're still on a valid page
		assertTrue(ownersPage.isOwnersListPage(), "Should still be on owners list page");

		// If there are multiple pages, verify content changed
		if (ownersPage.getOwnersCount() > 0) {
			var secondPageOwners = ownersPage.getOwnerNames();
			// Content should be different between pages
			assertNotEquals(firstPageOwners, secondPageOwners, "Page content should be different");
		}
	}

	@Test
	void asAUserIShouldBeAbleToViewOwnersPetsIfAny() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets (George Franklin has Leo)
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// Verify pets section is present
		assertTrue(detailsPage.hasPets(), "Owner should have pets");
		assertTrue(detailsPage.getPetsCount() > 0, "Should have at least one pet");

		// Verify pet information is displayed
		String petName = detailsPage.getPetName(0);
		assertNotNull(petName, "Pet name should be displayed");
		assertFalse(petName.trim().isEmpty(), "Pet name should not be empty");
	}

	@Test
	void asAUserIShouldBeAbleToAccessPetManagementFromOwnerDetails() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Go to owner details
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);

		// Test Add New Pet link
		CreatePetPage createPetPage = detailsPage.clickAddNewPet();
		assertTrue(createPetPage.isCreatePetPage(), "Should navigate to create pet page");

		// Go back to owner details
		navigateTo("/owners/1");
		detailsPage = new OwnerDetailsPage(page);

		// If owner has pets, test Edit Pet link
		if (detailsPage.hasPets()) {
			EditPetPage editPetPage = detailsPage.clickEditFirstPet();
			assertTrue(editPetPage.isEditPetPage(), "Should navigate to edit pet page");

			// Go back to owner details
			navigateTo("/owners/1");
			detailsPage = new OwnerDetailsPage(page);

			// Test Add Visit link
			CreateVisitPage createVisitPage = detailsPage.clickAddVisitForFirstPet();
			assertTrue(createVisitPage.isCreateVisitPage(), "Should navigate to create visit page");
		}
	}

}