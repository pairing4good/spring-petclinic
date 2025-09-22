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
 * Playwright E2E tests for navigation and basic page functionality. As a user, I want to
 * navigate through the application pages and see the expected content and layout.
 */
class NavigationAndPagesTest extends PlaywrightTestBase {

	@Test
	void asAUserIShouldBeAbleToAccessTheHomePage() {
		// Navigate to home page
		goToHomePage();

		HomePage homePage = new HomePage(page);

		// Verify we're on the home page
		assertTrue(homePage.isHomePage(), "Should be on the home page");
		assertEquals("Welcome", homePage.getWelcomeText(), "Should display welcome message");
		assertTrue(homePage.isLogoPresent(), "Logo should be present");
		assertTrue(homePage.isNavigationPresent(), "Navigation should be present");
		assertTrue(homePage.isPetImageDisplayed(), "Pet image should be displayed");
	}

	@Test
	void asAUserIShouldBeAbleToNavigateToFindOwnersPage() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners page
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Verify we're on the Find Owners page
		assertTrue(findOwnersPage.isFindOwnersPage(), "Should be on Find Owners page");
		assertTrue(findOwnersPage.isNavigationPresent(), "Navigation should be present");
		assertTrue(findOwnersPage.isLogoPresent(), "Logo should be present");
	}

	@Test
	void asAUserIShouldBeAbleToNavigateToVeterinariansPage() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Veterinarians page
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Verify we're on the Veterinarians page
		assertTrue(vetsPage.isVeterinariansPage(), "Should be on Veterinarians page");
		assertTrue(vetsPage.hasVets(), "Should display veterinarians");
		assertTrue(vetsPage.isNavigationPresent(), "Navigation should be present");
		assertTrue(vetsPage.isLogoPresent(), "Logo should be present");
	}

	@Test
	void asAUserIShouldBeAbleToAccessTheErrorPage() {
		goToHomePage();
		HomePage homePage = new HomePage(page);

		// Navigate to Error page
		ErrorPage errorPage = homePage.navigateToError();

		// Verify we're on the Error page
		assertTrue(errorPage.isErrorPage(), "Should be on Error page");
		assertTrue(errorPage.isInternalServerError(), "Should be a 500 error page");
		assertEquals("Something happened...", errorPage.getErrorHeadingText(), "Should display error message");
		assertTrue(errorPage.isErrorImageDisplayed(), "Error image should be displayed");
		assertTrue(errorPage.isNavigationPresent(), "Navigation should be present");
		assertTrue(errorPage.isLogoPresent(), "Logo should be present");
	}

	@Test
	void asAUserIShouldSeeConsistentNavigationAcrossAllPages() {
		goToHomePage();

		// Test navigation from Home page
		HomePage homePage = new HomePage(page);
		assertTrue(homePage.isNavigationPresent(), "Navigation should be present on home page");

		// Test navigation from Find Owners page
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		assertTrue(findOwnersPage.isNavigationPresent(), "Navigation should be present on Find Owners page");

		// Test navigation from Veterinarians page
		VeterinariansPage vetsPage = findOwnersPage.navigateToVeterinarians();
		assertTrue(vetsPage.isNavigationPresent(), "Navigation should be present on Veterinarians page");

		// Test navigation from Error page
		ErrorPage errorPage = vetsPage.navigateToError();
		assertTrue(errorPage.isNavigationPresent(), "Navigation should be present on Error page");

		// Navigate back to home from error page
		HomePage backToHome = errorPage.navigateToHome();
		assertTrue(backToHome.isHomePage(), "Should be able to navigate back to home");
	}

	@Test
	void asAUserIShouldSeeCorrectPageTitlesOnAllPages() {
		String expectedTitle = "PetClinic :: a Spring Framework demonstration";

		// Check home page title
		goToHomePage();
		assertEquals(expectedTitle, page.title(), "Home page should have correct title");

		// Check Find Owners page title
		navigateTo("/owners/find");
		assertEquals(expectedTitle, page.title(), "Find Owners page should have correct title");

		// Check Veterinarians page title
		navigateTo("/vets.html");
		assertEquals(expectedTitle, page.title(), "Veterinarians page should have correct title");

		// Check Error page title
		navigateTo("/oups");
		assertEquals(expectedTitle, page.title(), "Error page should have correct title");
	}

}