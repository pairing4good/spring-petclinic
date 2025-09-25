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
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for veterinarians listing functionality. Tests veterinarian display,
 * pagination, and specialties.
 */
class VeterinariansE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToViewVeterinariansList_SoThatICanSeeAvailableVets() {
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		navigateTo("/vets.html");
		vetsPage.waitForPageLoad();

		// Verify the page loads correctly
		assertTrue(page.url().contains("/vets.html"));
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible());
		assertTrue(page.locator("table").isVisible());
	}

	@Test
	void asAUser_IWantToSeeVeterinarianInformation_SoThatIKnowTheirNamesAndSpecialties() {
		navigateTo("/vets.html");
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		// Verify table headers are present
		assertTrue(page.locator("table th:has-text('Name')").isVisible());
		assertTrue(page.locator("table th:has-text('Specialties')").isVisible());

		// Verify at least one veterinarian is listed
		assertTrue(page.locator("table tbody tr").count() > 0);

		// Check for specific veterinarians that should exist in the sample data
		assertTrue(page.locator("table tbody tr:has(td:has-text('James Carter'))").isVisible());
	}

	@Test
	void asAUser_IWantToSeePagination_SoThatICanViewAllVeterinarians() {
		navigateTo("/vets.html");
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		// Check if pagination controls are present (they appear when there are multiple
		// pages)
		if (page.locator("a:has-text('2')").isVisible()) {
			// If pagination exists, test it
			page.locator("a:has-text('2')").click();
			page.waitForLoadState();

			// Should still be on vets page but with different data
			assertTrue(page.url().contains("/vets.html"));
			assertTrue(page.locator("table").isVisible());
		}
	}

	@Test
	void asAUser_IWantToSeeVeterinarianSpecialties_SoThatIKnowTheirExpertise() {
		navigateTo("/vets.html");
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		// Look for veterinarians with specialties
		assertTrue(page.locator("table tbody tr:has(td:has-text('radiology'))").count() > 0);
		assertTrue(page.locator("table tbody tr:has(td:has-text('surgery'))").count() > 0);
		assertTrue(page.locator("table tbody tr:has(td:has-text('dentistry'))").count() > 0);

		// Some vets should have no specialties (showing "none")
		assertTrue(page.locator("table tbody tr:has(td:has-text('none'))").count() > 0);
	}

	@Test
	void asAUser_IWantToRefreshVeterinariansList_SoThatICanGetUpdatedInformation() {
		navigateTo("/vets.html");
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		int initialVetCount = page.locator("table tbody tr").count();

		// Refresh the page
		page.reload();
		vetsPage.waitForPageLoad();

		// Should still show the same number of vets
		int refreshedVetCount = page.locator("table tbody tr").count();
		assertTrue(refreshedVetCount == initialVetCount);
	}

	@Test
	void asAUser_IWantToNavigateToVetsFromOtherPages_SoThatICanAccessVetInfo() {
		// Start from home page
		navigateTo("/");

		// Click veterinarians link in navigation
		page.locator("nav a:has-text('Veterinarians')").click();

		// Should navigate to vets page
		assertTrue(page.url().contains("/vets.html"));
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible());
	}

	@Test
	void asAUser_IWantToSeeConsistentVetData_SoThatInformationIsReliable() {
		navigateTo("/vets.html");
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		vetsPage.waitForPageLoad();

		// Verify specific veterinarians that should exist in sample data
		assertTrue(page.locator("td:has-text('Helen Leary')").isVisible());
		assertTrue(page.locator("td:has-text('Linda Douglas')").isVisible());
		assertTrue(page.locator("td:has-text('Rafael Ortega')").isVisible());
		assertTrue(page.locator("td:has-text('Henry Stevens')").isVisible());

		// Verify their specialties are displayed correctly
		assertTrue(page.locator("tr:has(td:has-text('Helen Leary')) td:has-text('radiology')").isVisible());
		assertTrue(page.locator("tr:has(td:has-text('Linda Douglas')) td:has-text('dentistry')").isVisible());
		assertTrue(page.locator("tr:has(td:has-text('Linda Douglas')) td:has-text('surgery')").isVisible());
	}

}