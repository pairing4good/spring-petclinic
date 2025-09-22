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
 * Playwright E2E tests for pet and visit management functionality. As a user, I want to
 * be able to manage pets and their visits for owners in the pet clinic application.
 */
class PetAndVisitManagementTest extends PlaywrightTestBase {

	@Test
	void asAUserIShouldBeAbleToAccessPetManagementFromOwnerDetails() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Go to owner details
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);

		// Verify owner details page has pet management links
		assertTrue(detailsPage.isOwnerDetailsPage(), "Should be on owner details page");

		// Test Add New Pet link
		CreatePetPage createPetPage = detailsPage.clickAddNewPet();
		assertTrue(createPetPage.isCreatePetPage(), "Should navigate to create pet page");
	}

	@Test
	void asAUserIShouldBeAbleToViewPetInformationWhenOwnerHasPets() {
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
	void asAUserIShouldBeAbleToAccessEditPetPageWhenOwnerHasPets() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// If owner has pets, test Edit Pet link
		if (detailsPage.hasPets()) {
			EditPetPage editPetPage = detailsPage.clickEditFirstPet();
			assertTrue(editPetPage.isEditPetPage(), "Should navigate to edit pet page");
		}
	}

	@Test
	void asAUserIShouldBeAbleToAccessCreateVisitPageForPets() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// If owner has pets, test Add Visit link
		if (detailsPage.hasPets()) {
			CreateVisitPage createVisitPage = detailsPage.clickAddVisitForFirstPet();
			assertTrue(createVisitPage.isCreateVisitPage(), "Should navigate to create visit page");
		}
	}

	@Test
	void asAUserIShouldBeAbleToCreateNewPetForOwner() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Go to first owner
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);

		// Click Add New Pet
		CreatePetPage createPetPage = detailsPage.clickAddNewPet();

		// Verify we're on the create pet page
		assertTrue(createPetPage.isCreatePetPage(), "Should be on create pet page");

		// Fill in pet information (this tests the form interaction)
		String uniquePetName = "TestPet" + System.currentTimeMillis();
		createPetPage.fillPetInfo(uniquePetName, "2023-01-01", "dog");

		// Submit the form
		OwnerDetailsPage updatedDetailsPage = createPetPage.submitForm();

		// Verify we're back on owner details (successful submission redirects back)
		assertTrue(updatedDetailsPage.isOwnerDetailsPage(), "Should be back on owner details after creating pet");
	}

	@Test
	void asAUserIShouldBeAbleToEditExistingPet() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// If owner has pets, test edit functionality
		if (detailsPage.hasPets()) {
			EditPetPage editPetPage = detailsPage.clickEditFirstPet();

			// Verify we're on the edit pet page
			assertTrue(editPetPage.isEditPetPage(), "Should be on edit pet page");

			// Update pet information
			String updatedName = "UpdatedPet" + System.currentTimeMillis();
			editPetPage.updatePetInfo(updatedName, "2023-02-01", "cat");

			// Submit the update
			OwnerDetailsPage updatedDetailsPage = editPetPage.submitUpdate();

			// Verify we're back on owner details
			assertTrue(updatedDetailsPage.isOwnerDetailsPage(), "Should be back on owner details after updating pet");
		}
	}

	@Test
	void asAUserIShouldBeAbleToCreateVisitForPet() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// If owner has pets, test visit creation
		if (detailsPage.hasPets()) {
			CreateVisitPage createVisitPage = detailsPage.clickAddVisitForFirstPet();

			// Verify we're on the create visit page
			assertTrue(createVisitPage.isCreateVisitPage(), "Should be on create visit page");

			// Fill in visit information
			createVisitPage.fillVisitInfo("2024-01-15", "Regular checkup");

			// Submit the visit
			OwnerDetailsPage updatedDetailsPage = createVisitPage.submitForm();

			// Verify we're back on owner details
			assertTrue(updatedDetailsPage.isOwnerDetailsPage(), "Should be back on owner details after creating visit");
		}
	}

	@Test
	void asAUserIShouldSeeConsistentNavigationInPetManagementPages() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Go to owner details
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);

		// Test navigation from create pet page
		CreatePetPage createPetPage = detailsPage.clickAddNewPet();
		assertTrue(createPetPage.isNavigationPresent(), "Navigation should be present on create pet page");

		// Test navigation to home from create pet page
		HomePage homeFromPet = createPetPage.navigateToHome();
		assertTrue(homeFromPet.isHomePage(), "Should be able to navigate to home from create pet page");

		// Test navigation to veterinarians from create pet page
		navigateTo("/owners/1/pets/new");
		createPetPage = new CreatePetPage(page);
		VeterinariansPage vetsFromPet = createPetPage.navigateToVeterinarians();
		assertTrue(vetsFromPet.isVeterinariansPage(),
				"Should be able to navigate to veterinarians from create pet page");
	}

	@Test
	void asAUserIShouldSeeAllPetManagementLinksOnOwnerDetailsPage() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Find an owner with pets
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// Verify pet management functionality is available
		assertTrue(detailsPage.isOwnerDetailsPage(), "Should be on owner details page");

		// For owners with pets, verify pet management links are present
		if (detailsPage.hasPets()) {
			// Test that we can access edit pet page (validates link is present and
			// working)
			EditPetPage editPetPage = detailsPage.clickEditFirstPet();
			assertTrue(editPetPage.isEditPetPage(), "Edit pet link should work");

			// Go back and test visit creation
			navigateTo("/owners/1");
			detailsPage = new OwnerDetailsPage(page);

			CreateVisitPage createVisitPage = detailsPage.clickAddVisitForFirstPet();
			assertTrue(createVisitPage.isCreateVisitPage(), "Add visit link should work");
		}

		// Test add new pet is always available
		navigateTo("/owners/1");
		detailsPage = new OwnerDetailsPage(page);

		CreatePetPage createPetPage = detailsPage.clickAddNewPet();
		assertTrue(createPetPage.isCreatePetPage(), "Add new pet link should always be available");
	}

	@Test
	void asAUserIShouldBeAbleToNavigateBetweenPetManagementPagesAndOtherSections() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Start from home, go to find owners
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName("George Franklin");

		// From owner details, go to create pet
		CreatePetPage createPetPage = detailsPage.clickAddNewPet();

		// From create pet, navigate to veterinarians
		VeterinariansPage vetsPage = createPetPage.navigateToVeterinarians();
		assertTrue(vetsPage.isVeterinariansPage(), "Should navigate to veterinarians from pet page");

		// From veterinarians, navigate to find owners
		FindOwnersPage findOwnersAgain = vetsPage.navigateToFindOwners();
		assertTrue(findOwnersAgain.isFindOwnersPage(), "Should navigate to find owners from veterinarians");

		// Complete the circle back to home
		HomePage homeAgain = findOwnersAgain.navigateToHome();
		assertTrue(homeAgain.isHomePage(), "Should navigate back to home");
	}

}