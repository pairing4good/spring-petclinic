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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for navigation and homepage functionality. Tests all main navigation flows
 * and homepage content.
 */
class NavigationAndHomepageE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToViewTheHomepage_SoThatICanSeeTheWelcomeMessage() {
		HomePage homePage = new HomePage(page, baseUrl).open();

		assertEquals("PetClinic :: a Spring Framework demonstration", homePage.getTitle());
		assertTrue(homePage.getWelcomeMessage().contains("Welcome"));
		assertTrue(homePage.isPetsImageVisible());
		assertTrue(homePage.isSpringLogoVisible());
		assertTrue(homePage.isPageLoaded());
	}

	@Test
	void asAUser_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		HomePage homePage = new HomePage(page, baseUrl).open();
		FindOwnersPage findOwnersPage = homePage.goToFindOwners();

		assertTrue(findOwnersPage.getCurrentUrl().contains("/owners/find"));
		assertTrue(findOwnersPage.isFormDisplayed());
		assertTrue(findOwnersPage.isPageLoaded());
	}

	@Test
	void asAUser_IWantToNavigateToVeterinarians_SoThatICanViewAvailableVets() {
		HomePage homePage = new HomePage(page, baseUrl).open();
		VeterinariansPage vetsPage = homePage.goToVeterinarians();

		assertTrue(vetsPage.getCurrentUrl().contains("/vets.html"));
		vetsPage.waitForPageLoad();
		assertTrue(vetsPage.isPageLoaded());
	}

	@Test
	void asAUser_IWantToNavigateToErrorPage_SoThatICanSeeErrorHandling() {
		HomePage homePage = new HomePage(page, baseUrl).open();
		ErrorPage errorPage = homePage.goToErrorPage();

		assertTrue(errorPage.getCurrentUrl().contains("/oups"));
		errorPage.waitForPageLoad();
		assertEquals("Something happened...", errorPage.getErrorMessage());
	}

	@Test
	void asAUser_IWantToUseBackAndForwardButtons_SoThatICanNavigateThroughHistory() {
		HomePage homePage = new HomePage(page, baseUrl).open();

		// Navigate to Find Owners
		FindOwnersPage findOwnersPage = homePage.goToFindOwners();
		assertTrue(findOwnersPage.getCurrentUrl().contains("/owners/find"));

		// Navigate to Veterinarians
		VeterinariansPage vetsPage = findOwnersPage.goToVeterinarians();
		assertTrue(vetsPage.getCurrentUrl().contains("/vets.html"));

		// Use browser back button
		page.goBack();
		assertTrue(page.url().contains("/owners/find"));

		// Use browser forward button
		page.goForward();
		assertTrue(page.url().contains("/vets.html"));

		// Go back to home
		page.goBack();
		page.goBack();
		assertTrue(page.url().equals(baseUrl + "/"));
	}

	@Test
	void asAUser_IWantToRefreshTheHomepage_SoThatICanReloadContent() {
		HomePage homePage = new HomePage(page, baseUrl).open();
		String originalTitle = homePage.getTitle();

		// Refresh the page
		page.reload();

		// Verify content is still there after refresh
		assertEquals(originalTitle, page.title());
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible());
	}

	@Test
	void asAUser_IWantToDirectlyAccessHomepage_SoThatICanReachItViaDirectURL() {
		// Test direct navigation to home page
		navigateTo("/");

		assertEquals("PetClinic :: a Spring Framework demonstration", page.title());
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible());
	}

	@Test
	void asAUser_IWantToSeeConsistentNavigation_SoThatAllPagesHaveTheSameMenu() {
		// Test navigation menu is present on all main pages

		// Home page
		HomePage homePage = new HomePage(page, baseUrl).open();
		assertTrue(page.locator("nav.navbar ul.navbar-nav").isVisible());
		assertTrue(page.locator("nav a:has-text('Home')").isVisible());
		assertTrue(page.locator("nav a:has-text('Find Owners')").isVisible());
		assertTrue(page.locator("nav a:has-text('Veterinarians')").isVisible());
		assertTrue(page.locator("nav a:has-text('Error')").isVisible());

		// Find Owners page
		FindOwnersPage findOwnersPage = homePage.goToFindOwners();
		assertTrue(page.locator("nav.navbar ul.navbar-nav").isVisible());
		assertTrue(page.locator("nav a:has-text('Home')").isVisible());

		// Veterinarians page
		VeterinariansPage vetsPage = findOwnersPage.goToVeterinarians();
		assertTrue(page.locator("nav.navbar ul.navbar-nav").isVisible());
		assertTrue(page.locator("nav a:has-text('Home')").isVisible());
	}

}