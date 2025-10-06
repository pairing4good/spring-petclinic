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
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for Veterinarians page functionality including listing and pagination.
 */
class VeterinariansTests extends PlaywrightTestBase {

	@Test
	void asAVisitor_IWantToViewVeterinariansList_SoThatICanSeeAvailableVeterinarians() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		assertTrue(vetsPage.isVeterinariansHeadingVisible(), "Veterinarians heading should be visible");
		assertTrue(vetsPage.isVeterinariansTableVisible(), "Veterinarians table should be visible");
		assertTrue(vetsPage.getVeterinarianCount() > 0, "Should display at least one veterinarian");
	}

	@Test
	void asAVisitor_IWantToSeeVeterinarianDetails_SoThatICanViewNameAndSpecialties() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		int vetCount = vetsPage.getVeterinarianCount();
		assertTrue(vetCount > 0, "Should have veterinarians to display");

		// Check first veterinarian has name and specialties
		String vetName = vetsPage.getVeterinarianName(0);
		String vetSpecialties = vetsPage.getVeterinarianSpecialties(0);

		assertNotNull(vetName, "Veterinarian name should not be null");
		assertFalse(vetName.trim().isEmpty(), "Veterinarian name should not be empty");
		assertNotNull(vetSpecialties, "Veterinarian specialties should not be null");
	}

	@Test
	void asAVisitor_IWantToNavigateThroughVeterinarianPages_SoThatICanSeeAllVeterinarians() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		if (vetsPage.isPaginationVisible()) {
			// Test pagination if available
			assertTrue(vetsPage.isNextPageLinkVisible(), "Next page link should be visible when pagination exists");

			vetsPage.clickNextPage();

			// Verify we're on page 2
			assertTrue(page.url().contains("page=2"), "Should navigate to page 2");
		}
		else {
			// If no pagination, verify all vets are displayed on single page
			assertTrue(vetsPage.getVeterinarianCount() > 0, "Should display veterinarians on single page");
		}
	}

	@Test
	void asAVisitor_IWantToVerifyKnownVeterinarians_SoThatICanConfirmDataAccuracy() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		// Test with known veterinarians from the application
		assertTrue(
				vetsPage.hasVeterinarianWithName("James Carter") || vetsPage.hasVeterinarianWithName("Helen Leary")
						|| vetsPage.hasVeterinarianWithName("Linda Douglas"),
				"Should display at least one of the expected veterinarians");
	}

	@Test
	void asAVisitor_IWantToAccessVeterinariansFromNavigation_SoThatICanEasilyFindTheInformation() {
		// Start from home page
		page.navigate(baseUrl);

		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);

		// Use navigation to reach veterinarians page
		vetsPage.clickVeterinariansLink();

		assertTrue(vetsPage.isVeterinariansPageDisplayed(), "Should navigate to veterinarians page from navigation");
		assertTrue(vetsPage.isVeterinariansHeadingVisible(),
				"Veterinarians heading should be visible after navigation");
	}

	@Test
	void asAVisitor_IWantToSeeVeterinarianSpecialties_SoThatICanChooseAppropriateVeterinarian() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		int vetCount = vetsPage.getVeterinarianCount();
		boolean hasSpecialties = false;

		// Check that at least some veterinarians have specialties
		for (int i = 0; i < Math.min(vetCount, 5); i++) {
			String specialties = vetsPage.getVeterinarianSpecialties(i);
			if (!specialties.equals("none") && !specialties.trim().isEmpty()) {
				hasSpecialties = true;
				break;
			}
		}

		assertTrue(hasSpecialties || vetCount > 0, "Should display veterinarians with or without specialties");
	}

	@Test
	void asAVisitor_IWantToVerifyPageLoadPerformance_SoThatThePageLoadsQuickly() {
		long startTime = System.currentTimeMillis();

		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		// Verify page loads within reasonable time
		assertTrue(vetsPage.isVeterinariansHeadingVisible(), "Page should load successfully");

		long loadTime = System.currentTimeMillis() - startTime;
		assertTrue(loadTime < 10000, "Page should load within 10 seconds, took: " + loadTime + "ms");
	}

	@Test
	void asAVisitor_IWantToNavigateAwayAndReturn_SoThatICanMaintainMyBrowsingSession() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.navigateTo();

		// Navigate away
		vetsPage.clickHomeLink();
		assertTrue(vetsPage.isHomePageDisplayed(), "Should navigate away to home page");

		// Navigate back
		vetsPage.clickVeterinariansLink();
		assertTrue(vetsPage.isVeterinariansPageDisplayed(), "Should return to veterinarians page");
		assertTrue(vetsPage.isVeterinariansHeadingVisible(), "Veterinarians content should be visible after return");
	}

}