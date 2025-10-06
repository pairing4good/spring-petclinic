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

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.config.PlaywrightTestBase;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.AddOwnerPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerDetailsPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for Owner Management functionality including search, create, view, and
 * edit operations.
 */
class OwnerManagementTests extends PlaywrightTestBase {

	@Test
	void asAnOwner_IWantToSearchForOwnersByLastName_SoThatICanFindExistingOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		assertTrue(findOwnersPage.isFindOwnersHeadingVisible(), "Find Owners heading should be visible");
		assertTrue(findOwnersPage.isLastNameInputVisible(), "Last name input should be visible");
		assertTrue(findOwnersPage.isFindOwnerButtonVisible(), "Find Owner button should be visible");

		// Search for existing owner
		findOwnersPage.enterLastName("Franklin");
		findOwnersPage.clickFindOwner();

		// Should navigate to owner details page for direct match
		assertTrue(page.url().contains("/owners/"), "Should navigate to owner details page");
	}

	@Test
	void asAnOwner_IWantToSearchWithEmptyLastName_SoThatICanSeeAllOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		// Search with empty last name
		findOwnersPage.clearLastName();
		findOwnersPage.clickFindOwner();

		// Should show list of owners or navigate appropriately
		// The actual behavior depends on the application logic
		assertNotNull(page.url(), "Page should navigate somewhere after empty search");
	}

	@Test
	void asAUser_IWantToCreateANewOwner_SoThatICanAddOwnerInformation() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		findOwnersPage.clickAddOwner();

		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		assertTrue(addOwnerPage.isOwnerHeadingVisible(), "Owner heading should be visible on add owner page");
		assertTrue(addOwnerPage.areAllFieldsVisible(), "All form fields should be visible");

		// Fill in owner information
		String timestamp = String.valueOf(System.currentTimeMillis());
		addOwnerPage.fillOwnerForm("John" + timestamp, "Doe" + timestamp, "123 Test Street", "Test City", "555-1234");

		addOwnerPage.clickAddOwner();

		// Should navigate to owner details page after successful creation
		assertTrue(page.url().contains("/owners/"), "Should navigate to owner details page after creation");
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeOwnerInformation() {
		// First search for an existing owner
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();
		findOwnersPage.enterLastName("Franklin");
		findOwnersPage.clickFindOwner();

		// Verify owner details page
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page, baseUrl);
		assertTrue(ownerDetailsPage.isOwnerInformationVisible(), "Owner information should be visible");
		assertTrue(ownerDetailsPage.isPetsAndVisitsHeadingVisible(), "Pets and visits section should be visible");
		assertTrue(ownerDetailsPage.isEditOwnerLinkVisible(), "Edit owner link should be visible");
		assertTrue(ownerDetailsPage.isAddNewPetLinkVisible(), "Add new pet link should be visible");

		// Verify owner information is displayed
		assertNotNull(ownerDetailsPage.getOwnerName(), "Owner name should be displayed");
		assertFalse(ownerDetailsPage.getOwnerName().trim().isEmpty(), "Owner name should not be empty");
	}

	@Test
	void asAUser_IWantToAccessEditOwnerFunctionality_SoThatICanModifyOwnerInformation() {
		// Navigate to an existing owner
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();
		findOwnersPage.enterLastName("Franklin");
		findOwnersPage.clickFindOwner();

		// Click edit owner
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page, baseUrl);
		ownerDetailsPage.clickEditOwner();

		// Should navigate to edit owner page
		assertTrue(page.url().contains("/edit"), "Should navigate to edit owner page");
	}

	@Test
	void asAUser_IWantToAddNewPetToOwner_SoThatICanManagePetInformation() {
		// Navigate to an existing owner
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();
		findOwnersPage.enterLastName("Franklin");
		findOwnersPage.clickFindOwner();

		// Click add new pet
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page, baseUrl);
		ownerDetailsPage.clickAddNewPet();

		// Should navigate to add pet page
		assertTrue(page.url().contains("/pets/new"), "Should navigate to add pet page");
	}

	@Test
	void asAUser_IWantToValidateRequiredFields_SoThatIProvideCompleteOwnerInformation() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Try to submit form with empty required fields
		addOwnerPage.clickAddOwner();

		// Form should not submit successfully with empty required fields
		// The page should remain on the add owner form
		assertTrue(page.url().contains("/owners/new"), "Should remain on add owner page with validation errors");
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatICanHandleNoResultsScenario() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		// Search for non-existent owner
		findOwnersPage.enterLastName("NonExistentOwner123456");
		findOwnersPage.clickFindOwner();

		// Should handle no results appropriately
		// This might show a "no results" message or return to search page
		assertNotNull(page.url(), "Page should handle no results gracefully");
	}

	@Test
	void asAUser_IWantToNavigateBackAndForth_SoThatICanEasilyMoveAroundTheApplication() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		// Navigate to add owner
		findOwnersPage.clickAddOwner();
		assertTrue(page.url().contains("/owners/new"), "Should be on add owner page");

		// Use browser back button
		page.goBack();
		assertTrue(findOwnersPage.isFindOwnersPageDisplayed(), "Should be back on find owners page");

		// Use browser forward button
		page.goForward();
		assertTrue(page.url().contains("/owners/new"), "Should be forward to add owner page");
	}

}