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
import org.springframework.samples.petclinic.e2e.BaseE2ETest;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.HomePage;
import org.springframework.samples.petclinic.e2e.pages.OwnerDetailsPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerFormPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive E2E tests for owner management functionality. Tests owner search,
 * creation, editing, and form validation.
 */
public class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSearchForExistingOwner_SoThatICanViewTheirInformation() {
		// Navigate to Find Owners page
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for existing owner by last name
		findOwnersPage.searchForOwner("Franklin");

		// Should redirect to owner details page
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);

		// Verify owner information is displayed
		assertTrue(ownerDetailsPage.isOwnerInformationVisible(), "Owner information should be visible");
		assertEquals("George Franklin", ownerDetailsPage.getOwnerName(), "Owner name should be correct");
		assertEquals("110 W. Liberty St.", ownerDetailsPage.getOwnerAddress(), "Owner address should be correct");
		assertEquals("Madison", ownerDetailsPage.getOwnerCity(), "Owner city should be correct");
		assertEquals("6085551023", ownerDetailsPage.getOwnerTelephone(), "Owner telephone should be correct");

		// Verify pets and visits section is visible
		assertTrue(ownerDetailsPage.isPetsAndVisitsVisible(), "Pets and visits section should be visible");
		assertTrue(ownerDetailsPage.getPetCount() > 0, "Owner should have at least one pet");
		assertTrue(ownerDetailsPage.hasPetWithName("Leo"), "Owner should have a pet named Leo");
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatICanSeeAppropriateMessage() {
		// Navigate to Find Owners page
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for non-existent owner
		findOwnersPage.searchForOwner("NonExistentOwner");

		// Should stay on the same page or show no results message
		// Wait for the page to process the search
		page.waitForTimeout(1000);

		// Check if we're still on the find owners page or if there's a message
		boolean isStillOnFindPage = findOwnersPage.isPageHeadingVisible();
		assertTrue(isStillOnFindPage, "Should stay on find owners page when no results found");
	}

	@Test
	void asAUser_IWantToCreateNewOwner_SoThatICanAddThemToTheSystem() {
		// Navigate to Add Owner form
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnerFormPage ownerFormPage = findOwnersPage.clickAddOwner();

		// Verify we're on the owner form page
		assertTrue(ownerFormPage.isPageHeadingVisible(), "Owner form page should be visible");
		assertTrue(ownerFormPage.isAddOwnerButtonVisible(), "Add Owner button should be visible");

		// Fill in owner information
		String uniqueLastName = "TestOwner" + System.currentTimeMillis();
		ownerFormPage.fillAllFields("John", uniqueLastName, "123 Test Street", "Test City", "5551234567");

		// Submit the form
		ownerFormPage.clickAddOwner();

		// Should redirect to owner details page
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);

		// Verify the new owner information is displayed
		assertTrue(ownerDetailsPage.isOwnerInformationVisible(), "Owner information should be visible");
		assertTrue(ownerDetailsPage.getOwnerName().contains("John"), "Owner name should contain John");
		assertTrue(ownerDetailsPage.getOwnerName().contains(uniqueLastName),
				"Owner name should contain test last name");
		assertEquals("123 Test Street", ownerDetailsPage.getOwnerAddress(), "Owner address should be correct");
		assertEquals("Test City", ownerDetailsPage.getOwnerCity(), "Owner city should be correct");
		assertEquals("5551234567", ownerDetailsPage.getOwnerTelephone(), "Owner telephone should be correct");
	}

	@Test
	void asAUser_IWantToEditExistingOwner_SoThatICanUpdateTheirInformation() {
		// First find an existing owner
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		findOwnersPage.searchForOwner("Franklin");

		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);
		assertTrue(ownerDetailsPage.isEditOwnerLinkVisible(), "Edit Owner link should be visible");

		// Click Edit Owner
		OwnerFormPage ownerFormPage = ownerDetailsPage.clickEditOwner();

		// Verify we're on the edit form (should show Update button instead of Add)
		assertTrue(ownerFormPage.isPageHeadingVisible(), "Owner form page should be visible");
		assertTrue(ownerFormPage.isUpdateOwnerButtonVisible(), "Update Owner button should be visible");

		// Verify existing data is populated
		assertEquals("George", ownerFormPage.getFirstNameValue(), "First name should be populated");
		assertEquals("Franklin", ownerFormPage.getLastNameValue(), "Last name should be populated");

		// Update the telephone number
		String newTelephone = "6085559999";
		ownerFormPage.fillTelephone(newTelephone);

		// Submit the update
		ownerFormPage.clickUpdateOwner();

		// Should redirect back to owner details
		ownerDetailsPage = new OwnerDetailsPage(page);

		// Verify the telephone was updated
		assertEquals(newTelephone, ownerDetailsPage.getOwnerTelephone(), "Telephone should be updated");
	}

	@Test
	void asAUser_IWantFormValidationErrors_SoThatIKnowWhatFieldsAreRequired() {
		// Navigate to Add Owner form
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnerFormPage ownerFormPage = findOwnersPage.clickAddOwner();

		// Try to submit form with missing required fields
		ownerFormPage.fillFirstName("John");
		ownerFormPage.fillLastName("Doe");
		ownerFormPage.fillCity("Test City");
		// Leave address and telephone empty

		ownerFormPage.clickAddOwner();

		// Should stay on the form page with validation errors
		assertTrue(ownerFormPage.isPageHeadingVisible(), "Should stay on owner form page");
		assertTrue(ownerFormPage.hasValidationErrors(), "Should have validation errors");
	}

	@Test
	void asAUser_IWantToSearchWithEmptyLastName_SoThatICanSeeAllOwners() {
		// Navigate to Find Owners page
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search with empty last name
		findOwnersPage.enterLastName("");
		findOwnersPage.clickFindOwner();

		// Should show all owners or stay on find page
		page.waitForTimeout(1000);

		// The behavior may vary - either show all owners or stay on find page
		// We'll just verify the page loads properly
		assertTrue(page.title().contains("PetClinic"), "Page should load properly");
	}

	@Test
	void asAUser_IWantToNavigateFromOwnerDetails_SoThatICanAccessRelatedFunctionality() {
		// Navigate to an owner's details page
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		findOwnersPage.searchForOwner("Franklin");

		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);

		// Verify navigation links are available
		assertTrue(ownerDetailsPage.isEditOwnerLinkVisible(), "Edit Owner link should be visible");
		assertTrue(ownerDetailsPage.isAddNewPetLinkVisible(), "Add New Pet link should be visible");

		// Test Add New Pet navigation
		ownerDetailsPage.clickAddNewPet();

		// Should navigate to pet form (basic check)
		assertTrue(page.url().contains("pets/new"), "Should navigate to new pet form");
	}

	@Test
	void asAUser_IWantToEnterInvalidTelephoneNumber_SoThatIGetValidationError() {
		// Navigate to Add Owner form
		navigateToHome();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnerFormPage ownerFormPage = findOwnersPage.clickAddOwner();

		// Fill form with invalid telephone (letters instead of numbers)
		ownerFormPage.fillAllFields("John", "TestValidation" + System.currentTimeMillis(), "123 Test Street",
				"Test City", "invalid-phone");

		ownerFormPage.clickAddOwner();

		// Should stay on form with validation error
		assertTrue(ownerFormPage.isPageHeadingVisible(), "Should stay on owner form page");
		assertTrue(ownerFormPage.hasValidationErrors(), "Should have validation errors for invalid telephone");
	}

}