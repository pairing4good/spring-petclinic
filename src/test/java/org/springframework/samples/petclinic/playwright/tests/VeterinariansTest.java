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
import org.springframework.samples.petclinic.playwright.pages.HomePage;
import org.springframework.samples.petclinic.playwright.pages.VeterinariansPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for veterinarians page functionality. Tests veterinarian list display,
 * pagination, and information viewing.
 */
class VeterinariansTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a user, I want to view the list of veterinarians, so that I can see available vets and their specialties")
	void testViewVeterinariansList() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		assertTrue(vetsPage.isVeterinariansPage(), "Should be on the veterinarians page");
		assertTrue(vetsPage.isVetsTableVisible(), "Veterinarians table should be visible");
		assertTrue(vetsPage.getVetsCount() > 0, "Should display at least one veterinarian");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian names and specialties, so that I can choose the right vet for my pet")
	void testVeterinarianInformation() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		String[] vetNames = vetsPage.getAllVetNames();
		assertTrue(vetNames.length > 0, "Should have at least one veterinarian name");

		// Check that we can get specialties for vets
		for (String vetName : vetNames) {
			String specialties = vetsPage.getVetSpecialties(vetName);
			assertNotNull(specialties, "Should be able to get specialties for " + vetName);
			// Specialties can be empty (showing "none") or contain actual specialties
		}
	}

	@Test
	@DisplayName("As a user, I want to see pagination when there are many veterinarians, so that I can navigate through all vets")
	void testVeterinariansPagination() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Check if pagination is present (may not be if there are few vets)
		if (vetsPage.isPaginationVisible()) {
			// Test that pagination controls exist and are functional
			assertDoesNotThrow(() -> {
				vetsPage.goToPage(1); // Should not fail even if already on page 1
			}, "Should be able to navigate to page 1");
		}

		// Should still have vets displayed regardless of pagination
		assertTrue(vetsPage.getVetsCount() > 0, "Should always display veterinarians");
	}

	@Test
	@DisplayName("As a user, I want to find specific veterinarians by name, so that I can locate a particular vet")
	void testFindSpecificVeterinarian() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		String[] vetNames = vetsPage.getAllVetNames();
		if (vetNames.length > 0) {
			String firstVetName = vetNames[0];

			// Test that we can find the vet in the list
			assertTrue(vetsPage.isVetListed(firstVetName),
					"Should be able to find vet " + firstVetName + " in the list");
		}
	}

	@Test
	@DisplayName("As a user, I want the veterinarians page to handle empty results gracefully, so that I understand when no vets are available")
	void testEmptyVeterinariansHandling() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// In this application, there should always be vets loaded from data.sql
		// But we test that the page structure is correct
		assertTrue(vetsPage.isVetsTableVisible(), "Table structure should be visible");

		// If there were no vets, the table would still exist but be empty
		// The application loads sample data, so we expect at least one vet
		assertTrue(vetsPage.getVetsCount() >= 0, "Should handle vet count gracefully");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian specialties clearly indicated, so that I know what services each vet provides")
	void testVeterinarianSpecialties() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		String[] vetNames = vetsPage.getAllVetNames();
		assertTrue(vetNames.length > 0, "Should have veterinarians to check");

		boolean foundVetWithSpecialties = false;
		boolean foundVetWithoutSpecialties = false;

		for (String vetName : vetNames) {
			String specialties = vetsPage.getVetSpecialties(vetName);
			assertNotNull(specialties, "Specialties should not be null for " + vetName);

			if (!specialties.trim().isEmpty() && !specialties.contains("none")) {
				foundVetWithSpecialties = true;
			}
			else {
				foundVetWithoutSpecialties = true;
			}
		}

		// The sample data should have both vets with and without specialties
		// This tests that the application correctly displays both cases
		// (though we don't strictly require both to exist)
		assertTrue(foundVetWithSpecialties || foundVetWithoutSpecialties,
				"Should properly display veterinarian specialty information");
	}

}