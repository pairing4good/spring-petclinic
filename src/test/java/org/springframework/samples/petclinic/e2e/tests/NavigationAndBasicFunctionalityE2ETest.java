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
import org.springframework.samples.petclinic.e2e.pages.ErrorPage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.HomePage;
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive E2E tests for navigation and basic functionality. Tests all main
 * navigation flows and page verification.
 */
public class NavigationAndBasicFunctionalityE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToAccessTheHomePage_SoThatICanSeeTheWelcomeScreen() {
		// Navigate to home page
		navigateToHome();

		HomePage homePage = new HomePage(page);

		// Verify page title
		assertTrue(homePage.isPageTitleCorrect(), "Page title should be correct");

		// Verify welcome heading is visible and contains expected text
		assertTrue(homePage.isWelcomeHeadingVisible(), "Welcome heading should be visible");
		assertEquals("Welcome", homePage.getWelcomeHeadingText(), "Welcome heading should have correct text");

		// Verify pets image is displayed
		assertTrue(homePage.isPetsImageVisible(), "Pets image should be visible");
	}

	@Test
	void asAUser_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		// Start from home page
		navigateToHome();
		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners page
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Verify page loaded correctly
		assertTrue(findOwnersPage.isPageTitleCorrect(), "Page title should be correct");
		assertTrue(findOwnersPage.isPageHeadingVisible(), "Find Owners heading should be visible");
		assertEquals("Find Owners", findOwnersPage.getPageHeadingText(), "Page heading should be correct");

		// Verify form elements are present
		assertTrue(findOwnersPage.isFindOwnerButtonVisible(), "Find Owner button should be visible");
		assertTrue(findOwnersPage.isAddOwnerLinkVisible(), "Add Owner link should be visible");
	}

	@Test
	void asAUser_IWantToNavigateToVeterinarians_SoThatICanViewTheListOfVets() {
		// Start from home page
		navigateToHome();
		HomePage homePage = new HomePage(page);

		// Navigate to Veterinarians page
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify page loaded correctly
		assertTrue(vetsPage.isPageTitleCorrect(), "Page title should be correct");
		assertTrue(vetsPage.isPageHeadingVisible(), "Veterinarians heading should be visible");
		assertEquals("Veterinarians", vetsPage.getPageHeadingText(), "Page heading should be correct");

		// Verify table is present and has veterinarians
		assertTrue(vetsPage.isVeterinariansTableVisible(), "Veterinarians table should be visible");
		assertTrue(vetsPage.getVeterinarianCount() > 0, "Should have at least one veterinarian");

		// Verify specific veterinarians from sample data
		assertTrue(vetsPage.hasVeterinarianWithName("James Carter"), "Should have James Carter");
		assertTrue(vetsPage.hasVeterinarianWithName("Helen Leary"), "Should have Helen Leary");

		// Verify specialties
		assertEquals("none", vetsPage.getVeterinarianSpecialties("James Carter"),
				"James Carter should have no specialties");
		assertEquals("radiology", vetsPage.getVeterinarianSpecialties("Helen Leary"),
				"Helen Leary should have radiology specialty");
	}

	@Test
	void asAUser_IWantToNavigateToErrorPage_SoThatICanSeeHowErrorsAreHandled() {
		// Start from home page
		navigateToHome();
		HomePage homePage = new HomePage(page);

		// Navigate to Error page
		ErrorPage errorPage = homePage.navigateToError();

		// Verify this is the expected error page, not a generic 404
		assertTrue(errorPage.isPageTitleCorrect(), "Page title should be correct");
		assertTrue(errorPage.isExpectedErrorPage(), "Should be the expected error page");

		// Verify error elements
		assertTrue(errorPage.isErrorHeadingVisible(), "Error heading should be visible");
		assertTrue(errorPage.getErrorHeadingText().contains("Something happened"),
				"Error heading should contain expected text");

		assertTrue(errorPage.isErrorMessageVisible(), "Error message should be visible");
		assertTrue(errorPage.getErrorMessageText().contains("Expected: controller used to showcase"),
				"Error message should contain expected text");

		assertTrue(errorPage.isErrorImageVisible(), "Error image should be visible");
	}

	@Test
	void asAUser_IWantToNavigateBetweenPages_SoThatICanAccessAllApplicationFeatures() {
		// Start from home page
		navigateToHome();
		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners and back to Home
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		assertTrue(findOwnersPage.isPageHeadingVisible(), "Find Owners page should load");

		homePage = findOwnersPage.navigateToHome();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Should navigate back to home");

		// Navigate to Veterinarians and back to Home
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();
		assertTrue(vetsPage.isPageHeadingVisible(), "Veterinarians page should load");

		homePage = vetsPage.navigateToHome();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Should navigate back to home");

		// Navigate to Error page and back to Home
		ErrorPage errorPage = homePage.navigateToError();
		assertTrue(errorPage.isExpectedErrorPage(), "Error page should load");

		homePage = errorPage.navigateToHome();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Should navigate back to home");
	}

	@Test
	void asAUser_IWantAllPagesToHaveCorrectTitles_SoThatICanIdentifyWhatPageIAmOn() {
		String expectedTitle = "PetClinic :: a Spring Framework demonstration";

		// Test home page title
		navigateToHome();
		assertEquals(expectedTitle, page.title(), "Home page should have correct title");

		// Test find owners page title
		navigateTo("/owners/find");
		assertEquals(expectedTitle, page.title(), "Find Owners page should have correct title");

		// Test veterinarians page title
		navigateTo("/vets.html");
		assertEquals(expectedTitle, page.title(), "Veterinarians page should have correct title");

		// Test error page title
		navigateTo("/oups");
		assertEquals(expectedTitle, page.title(), "Error page should have correct title");
	}

	@Test
	void asAUser_IWantToUseKeyboardNavigation_SoThatICanNavigateWithoutAMouse() {
		navigateToHome();

		// Test tab navigation to find owners link
		page.keyboard().press("Tab");
		page.keyboard().press("Tab"); // Skip logo, go to Home
		page.keyboard().press("Tab"); // Go to Find Owners
		page.keyboard().press("Enter");

		// Should be on Find Owners page
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		assertTrue(findOwnersPage.isPageHeadingVisible(), "Should navigate to Find Owners using keyboard");
	}

}