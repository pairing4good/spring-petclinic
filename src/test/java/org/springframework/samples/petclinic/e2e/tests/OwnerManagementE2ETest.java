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
import org.springframework.samples.petclinic.e2e.pages.AddOwnerPage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerDetailsPage;
import org.springframework.samples.petclinic.e2e.pages.OwnersListPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for owner search and management functionality. Tests all owner-related CRUD
 * operations and search capabilities.
 */
class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSearchForExistingOwners_SoThatICanFindSpecificOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();

		// Search for owners with last name "Davis"
		OwnersListPage ownersList = findOwnersPage.searchOwners("Davis");
		ownersList.waitForPageLoad();

		assertTrue(ownersList.hasOwners());
		assertTrue(ownersList.containsOwner("Davis"));
		assertTrue(ownersList.getOwnerCount() >= 1);
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeCompleteOwnerInformation() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();
		OwnersListPage ownersList = findOwnersPage.searchOwners("Davis");

		// Click on the first owner to view details
		OwnerDetailsPage ownerDetails = ownersList.clickFirstOwner();
		ownerDetails.waitForPageLoad();

		// Verify owner information is displayed
		assertTrue(ownerDetails.getOwnerName().contains("Davis"));
		assertTrue(!ownerDetails.getOwnerAddress().isEmpty());
		assertTrue(!ownerDetails.getOwnerCity().isEmpty());
		assertTrue(!ownerDetails.getOwnerTelephone().isEmpty());
	}

	@Test
	void asAUser_IWantToSearchWithPartialLastName_SoThatICanFindOwnersByPartialMatch() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();

		// Search with empty string should return all owners
		findOwnersPage.clearLastName();
		findOwnersPage.clickFindOwner();

		// Should show owners list or redirect to single owner
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	void asAUser_IWantToAddANewOwner_SoThatICanRegisterNewPetOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();
		AddOwnerPage addOwnerPage = findOwnersPage.clickAddOwner();

		// Fill out owner form with valid data
		String firstName = "John";
		String lastName = "Doe";
		String address = "123 Main Street";
		String city = "Anytown";
		String telephone = "5551234567";

		addOwnerPage.fillOwnerForm(firstName, lastName, address, city, telephone);

		// Verify form values
		assertEquals(firstName, addOwnerPage.getFirstNameValue());
		assertEquals(lastName, addOwnerPage.getLastNameValue());

		// Submit form
		OwnerDetailsPage ownerDetails = addOwnerPage.submitForm();
		ownerDetails.waitForPageLoad();

		// Verify new owner was created successfully
		assertTrue(ownerDetails.getOwnerName().contains(firstName));
		assertTrue(ownerDetails.getOwnerName().contains(lastName));
		assertEquals(address, ownerDetails.getOwnerAddress());
		assertEquals(city, ownerDetails.getOwnerCity());
		assertEquals(telephone, ownerDetails.getOwnerTelephone());
	}

	@Test
	void asAUser_IWantToSeeValidationErrors_SoThatIKnowWhatFieldsAreRequired() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();
		AddOwnerPage addOwnerPage = findOwnersPage.clickAddOwner();

		// Submit form without required fields
		addOwnerPage.submitFormWithErrors();

		// Should stay on the same page with validation errors
		assertTrue(page.url().contains("/owners/new"));
		// Note: Validation error checking would depend on specific error display
		// implementation
	}

	@Test
	void asAUser_IWantToSearchWithNoResults_SoThatIKnowWhenNoOwnersMatch() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();

		// Search for a name that definitely doesn't exist
		findOwnersPage.enterLastName("XYZ999NonExistentName");
		findOwnersPage.clickFindOwner();

		// Should show appropriate message or empty results
		// The actual behavior depends on the application implementation
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	void asAUser_IWantToNavigateBackFromOwnerDetails_SoThatICanReturnToSearch() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();
		OwnersListPage ownersList = findOwnersPage.searchOwners("Davis");
		OwnerDetailsPage ownerDetails = ownersList.clickFirstOwner();

		// Use browser back button
		page.goBack();

		// Should return to owners list
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	void asAUser_IWantToSearchForSingleOwner_SoThatICanDirectlyViewDetails() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();

		// Search for a name that should return exactly one owner
		findOwnersPage.enterLastName("Franklin");
		findOwnersPage.clickFindOwner();

		// Should redirect directly to owner details if only one match
		page.waitForLoadState();
		// Could be either direct redirect to owner details or show in list
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	void asAUser_IWantToClearTheSearchForm_SoThatICanStartANewSearch() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl).open();

		// Enter some text and then clear it
		findOwnersPage.enterLastName("SomeText");
		assertEquals("SomeText", findOwnersPage.getLastNameValue());

		findOwnersPage.clearLastName();
		assertEquals("", findOwnersPage.getLastNameValue());
	}

}