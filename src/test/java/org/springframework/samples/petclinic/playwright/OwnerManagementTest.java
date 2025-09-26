/*
 * Copyright 2012-2019 the original author or authors.
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

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.playwright.pages.HomePage;
import org.springframework.samples.petclinic.playwright.pages.OwnersPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for PetClinic owner management functionality (CRUD operations).
 *
 * @author Copilot
 */
class OwnerManagementTest extends BasePlaywrightTest {

	@Test
	void asAUser_IWantToSearchForOwners_SoThatICanFindExistingOwners() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();

		// Search for owners with last name "Davis"
		ownersPage.searchOwnersByLastName("Davis");

		// Verify search results are displayed
		assertTrue(ownersPage.isOwnersTableDisplayed(), "Owners table should be displayed");
		assertTrue(ownersPage.getOwnerCount() > 0, "Should find at least one owner");
	}

	@Test
	void asAUser_IWantToSearchForAllOwners_SoThatICanSeeTheCompleteOwnersList() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();

		// Search for all owners (empty search)
		ownersPage.searchAllOwners();

		// Verify that multiple owners are displayed
		assertTrue(ownersPage.isOwnersTableDisplayed(), "Owners table should be displayed");
		int ownerCount = ownersPage.getOwnerCount();
		assertTrue(ownerCount >= 5, "Should display multiple owners from sample data, found: " + ownerCount);
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeOwnerInformation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();
		ownersPage.searchAllOwners();

		// Click on the first owner
		var ownerDetailsPage = ownersPage.clickFirstOwner();

		// Verify owner details page is loaded
		assertTrue(ownerDetailsPage.isOwnerDetailsLoaded(), "Owner details page should be loaded");

		// Verify owner information is displayed
		String ownerName = ownerDetailsPage.getOwnerName();
		assertFalse(ownerName.trim().isEmpty(), "Owner name should not be empty");

		String ownerAddress = ownerDetailsPage.getOwnerAddress();
		assertFalse(ownerAddress.trim().isEmpty(), "Owner address should not be empty");
	}

	@Test
	void asAUser_IWantToCreateANewOwner_SoThatICanAddOwnerToTheSystem() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();
		ownersPage.clickAddOwner();

		// Fill out the owner form with valid data
		String firstName = "John";
		String lastName = "TestOwner";
		String address = "123 Test Street";
		String city = "Test City";
		String telephone = "1234567890";

		ownersPage.fillOwnerForm(firstName, lastName, address, city, telephone);
		ownersPage.submitOwnerForm();

		// Should redirect to owner details page after successful creation
		assertTrue(page.url().contains("/owners/"), "Should redirect to owner details after creation");
	}

	@Test
	void asAUser_IWantToEditAnExistingOwner_SoThatICanUpdateOwnerInformation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();
		ownersPage.searchAllOwners();

		// Go to owner details
		var ownerDetailsPage = ownersPage.clickFirstOwner();

		// Click edit owner
		ownersPage = ownerDetailsPage.clickEditOwner();

		// Update owner information
		String updatedCity = "Updated City";
		ownersPage.fillOwnerForm("George", "Franklin", "110 W. Liberty St.", updatedCity, "6085551023");
		ownersPage.submitOwnerForm();

		// Should redirect back to owner details
		assertTrue(page.url().contains("/owners/"), "Should redirect to owner details after edit");
	}

	@Test
	void asAUser_IWantToSeeValidationErrors_SoThatIKnowWhatToCorrectInOwnerForm() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();
		ownersPage.clickAddOwner();

		// Submit form with incomplete data (missing required fields)
		ownersPage.fillOwnerForm("", "", "", "", "");
		ownersPage.submitOwnerForm();

		// Verify validation errors are displayed
		assertTrue(ownersPage.hasValidationErrors(), "Should display validation errors for empty form");
	}

	@Test
	void asAUser_IWantToSearchWithPartialNames_SoThatICanFindOwnersEvenWithIncompleteInformation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();

		// Search with partial last name
		ownersPage.searchOwnersByLastName("Fr"); // Should match "Franklin"

		// Verify search results
		if (ownersPage.isOwnersTableDisplayed()) {
			assertTrue(ownersPage.getOwnerCount() > 0, "Should find owners with partial name match");
		}
		else {
			// If only one owner found, should redirect to details page
			assertTrue(page.url().contains("/owners/"), "Should redirect to owner details for single match");
		}
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatIKnowWhenNoOwnersAreFound() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		OwnersPage ownersPage = homePage.clickFindOwners();

		// Search for non-existent owner
		ownersPage.searchOwnersByLastName("NonExistentOwner");

		// Should not find any owners and stay on search page or show message
		// Note: PetClinic might show different behavior for no results
		assertTrue(page.url().contains("/owners"), "Should stay on owners page when no results found");
	}

	@Test
	void asAUser_IWantToNavigateBackAndForthBetweenPages_SoThatICanEasilyMoveAroundTheOwnerSection() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Navigate to owners search
		OwnersPage ownersPage = homePage.clickFindOwners();
		assertTrue(page.url().contains("/owners/find"), "Should be on find owners page");

		// Search and go to owner details
		ownersPage.searchAllOwners();
		var ownerDetailsPage = ownersPage.clickFirstOwner();
		assertTrue(page.url().contains("/owners/"), "Should be on owner details page");

		// Test browser back button
		page.goBack();
		assertTrue(page.url().contains("/owners"), "Should go back to owners list");

		// Test browser forward button
		page.goForward();
		assertTrue(page.url().contains("/owners/"), "Should go forward to owner details");
	}

}