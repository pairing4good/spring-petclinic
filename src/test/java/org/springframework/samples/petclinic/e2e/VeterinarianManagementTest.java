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
 * Playwright E2E tests for veterinarian functionality. As a user, I want to view
 * veterinarian information and navigate through the veterinarian listing.
 */
class VeterinarianManagementTest extends PlaywrightTestBase {

	@Test
	void asAUserIShouldBeAbleToViewVeterinarianList() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Veterinarians page
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify veterinarians page
		assertTrue(vetsPage.isVeterinariansPage(), "Should be on veterinarians page");
		assertTrue(vetsPage.hasVets(), "Should display veterinarians");
		assertTrue(vetsPage.getVetsCount() > 0, "Should have at least one veterinarian");
		assertTrue(vetsPage.isPaginationPresent(), "Pagination should be present");
	}

	@Test
	void asAUserIShouldSeeVeterinarianNamesAndSpecialties() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify veterinarian information is displayed
		int vetCount = vetsPage.getVetsCount();
		assertTrue(vetCount > 0, "Should have veterinarians");

		for (int i = 0; i < vetCount; i++) {
			String vetName = vetsPage.getVetName(i);
			String vetSpecialties = vetsPage.getVetSpecialties(i);

			assertNotNull(vetName, "Veterinarian name should not be null");
			assertFalse(vetName.trim().isEmpty(), "Veterinarian name should not be empty");
			assertNotNull(vetSpecialties, "Veterinarian specialties should not be null");
			// Note: Some vets may have "none" as specialties, which is valid
		}
	}

	@Test
	void asAUserIShouldSeeExpectedVeterinarians() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify specific veterinarians are present (based on sample data)
		assertTrue(vetsPage.hasVetWithName("James Carter"), "Should have James Carter");
		assertTrue(vetsPage.hasVetWithName("Helen Leary"), "Should have Helen Leary");
		assertTrue(vetsPage.hasVetWithName("Linda Douglas"), "Should have Linda Douglas");
	}

	@Test
	void asAUserIShouldSeeDifferentSpecialtiesForVeterinarians() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		int vetCount = vetsPage.getVetsCount();
		boolean foundVetWithSpecialties = false;
		boolean foundVetWithoutSpecialties = false;

		for (int i = 0; i < vetCount; i++) {
			if (vetsPage.vetHasSpecialties(i)) {
				foundVetWithSpecialties = true;
			}
			else {
				foundVetWithoutSpecialties = true;
			}
		}

		// Verify we have both types of veterinarians
		assertTrue(foundVetWithSpecialties, "Should have at least one vet with specialties");
		assertTrue(foundVetWithoutSpecialties, "Should have at least one vet without specialties");
	}

	@Test
	void asAUserIShouldBeAbleToNavigateThroughVeterinarianPages() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify pagination is present
		assertTrue(vetsPage.isPaginationPresent(), "Pagination should be present");

		// Get first page veterinarians
		var firstPageVets = new java.util.ArrayList<String>();
		int firstPageCount = vetsPage.getVetsCount();
		for (int i = 0; i < firstPageCount; i++) {
			firstPageVets.add(vetsPage.getVetName(i));
		}

		// Go to next page if available
		vetsPage.goToNextPage();

		// Verify we're still on veterinarians page
		assertTrue(vetsPage.isVeterinariansPage(), "Should still be on veterinarians page");

		// If there are veterinarians on the second page, verify they're different
		if (vetsPage.getVetsCount() > 0) {
			var secondPageVets = new java.util.ArrayList<String>();
			int secondPageCount = vetsPage.getVetsCount();
			for (int i = 0; i < secondPageCount; i++) {
				secondPageVets.add(vetsPage.getVetName(i));
			}

			// Verify content is different between pages
			assertNotEquals(firstPageVets, secondPageVets, "Page content should be different");
		}
	}

	@Test
	void asAUserIShouldBeAbleToNavigateBackToVeterinariansFromOtherPages() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners first
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		assertTrue(findOwnersPage.isFindOwnersPage(), "Should be on Find Owners page");

		// Then navigate to Veterinarians
		VeterinariansPage vetsPage = findOwnersPage.navigateToVeterinarians();
		assertTrue(vetsPage.isVeterinariansPage(), "Should be on Veterinarians page");

		// Navigate to Error page
		ErrorPage errorPage = vetsPage.navigateToError();
		assertTrue(errorPage.isErrorPage(), "Should be on Error page");

		// Navigate back to Veterinarians
		VeterinariansPage vetsPageAgain = errorPage.navigateToVeterinarians();
		assertTrue(vetsPageAgain.isVeterinariansPage(), "Should be back on Veterinarians page");
	}

	@Test
	void asAUserIShouldSeeConsistentVeterinarianDataAcrossNavigation() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Get initial vet count and names
		int initialVetCount = vetsPage.getVetsCount();
		String firstVetName = vetsPage.getVetName(0);

		// Navigate away and back
		HomePage homePageAgain = vetsPage.navigateToHome();
		VeterinariansPage vetsPageAgain = homePageAgain.navigateToVeterinarians();

		// Verify data is consistent
		assertEquals(initialVetCount, vetsPageAgain.getVetsCount(), "Vet count should be consistent");
		assertEquals(firstVetName, vetsPageAgain.getVetName(0), "First vet name should be consistent");
	}

}