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
import org.springframework.samples.petclinic.playwright.pages.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for homepage navigation and core user flows. Tests basic navigation
 * functionality and page accessibility.
 */
class HomePageNavigationTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a user, I want to navigate to the home page, so that I can see the main landing page")
	void testNavigateToHomePage() {
		navigateToHome();

		HomePage homePage = new HomePage(page);

		assertTrue(homePage.isHomePage(), "Should be on the home page");
		assertTrue(homePage.getTitle().contains("PetClinic"), "Page title should contain 'PetClinic'");
		assertTrue(homePage.isNavigationBarVisible(), "Navigation bar should be visible");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners page, so that I can search for pet owners")
	void testNavigateToFindOwners() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		assertTrue(findOwnersPage.isFindOwnersPage(), "Should be on the find owners page");
		assertTrue(findOwnersPage.isSearchFormVisible(), "Search form should be visible");
		assertTrue(findOwnersPage.isAddOwnerLinkVisible(), "Add Owner link should be visible");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians page, so that I can view the list of vets")
	void testNavigateToVeterinarians() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		assertTrue(vetsPage.isVeterinariansPage(), "Should be on the veterinarians page");
		assertTrue(vetsPage.isVetsTableVisible(), "Veterinarians table should be visible");
		assertTrue(vetsPage.getVetsCount() > 0, "Should have at least one veterinarian listed");
	}

	@Test
	@DisplayName("As a user, I want to navigate to the Error page, so that I can see how exceptions are handled")
	void testNavigateToErrorPage() {
		navigateToHome();

		HomePage homePage = new HomePage(page);
		ErrorPage errorPage = homePage.navigateToErrorPage();

		assertTrue(errorPage.isErrorPage(), "Should be on the error page");
		assertTrue(errorPage.hasExpectedErrorMessage(), "Should display expected error message");
		assertTrue(errorPage.isNotWhitelabelErrorPage(), "Should not be the default whitelabel error page");
	}

	@Test
	@DisplayName("As a user, I want to navigate back to home from other pages, so that I can return to the main page")
	void testNavigateBackToHome() {
		navigateToHome();

		HomePage homePage = new HomePage(page);

		// Navigate to Find Owners and back
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		assertTrue(findOwnersPage.isFindOwnersPage(), "Should be on find owners page");

		homePage = new HomePage(page);
		homePage.navigateToHome();
		assertTrue(homePage.isHomePage(), "Should be back on home page");
	}

	@Test
	@DisplayName("As a user, I want to see all navigation options, so that I can access all parts of the application")
	void testAllNavigationOptionsVisible() {
		navigateToHome();

		HomePage homePage = new HomePage(page);

		assertTrue(homePage.isNavigationBarVisible(), "Navigation bar should be visible");

		// Test that we can navigate to each main section
		assertDoesNotThrow(() -> {
			FindOwnersPage findPage = homePage.navigateToFindOwners();
			assertTrue(findPage.isFindOwnersPage(), "Find owners navigation should work");
		}, "Should be able to navigate to Find Owners");

		navigateToHome();
		assertDoesNotThrow(() -> {
			VeterinariansPage vetsPage = homePage.navigateToVeterinarians();
			assertTrue(vetsPage.isVeterinariansPage(), "Veterinarians navigation should work");
		}, "Should be able to navigate to Veterinarians");
	}

}