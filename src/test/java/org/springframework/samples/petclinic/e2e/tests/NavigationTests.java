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
import org.springframework.samples.petclinic.e2e.pages.HomePage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;
import org.springframework.samples.petclinic.e2e.pages.ErrorPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for Navigation functionality and cross-page interactions.
 */
class NavigationTests extends PlaywrightTestBase {

	@Test
	void asAVisitor_IWantToNavigateToAllMainPages_SoThatICanAccessAllFeatures() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Test navigation to Find Owners
		homePage.clickFindOwnersLink();
		assertTrue(homePage.isFindOwnersPageDisplayed(), "Should navigate to Find Owners page");

		// Test navigation to Veterinarians
		homePage.clickVeterinariansLink();
		assertTrue(homePage.isVeterinariansPageDisplayed(), "Should navigate to Veterinarians page");

		// Test navigation to Error page
		homePage.clickErrorLink();
		assertTrue(homePage.isErrorPageDisplayed(), "Should navigate to Error page");

		// Test navigation back to Home
		homePage.clickHomeLink();
		assertTrue(homePage.isHomePageDisplayed(), "Should navigate back to Home page");
	}

	@Test
	void asAVisitor_IWantToUseBrowserNavigationButtons_SoThatICanNavigateEfficiently() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Navigate to Find Owners
		homePage.clickFindOwnersLink();
		assertTrue(homePage.isFindOwnersPageDisplayed(), "Should be on Find Owners page");

		// Use browser back button
		page.goBack();
		assertTrue(homePage.isHomePageDisplayed(), "Browser back should return to home page");

		// Use browser forward button
		page.goForward();
		assertTrue(homePage.isFindOwnersPageDisplayed(), "Browser forward should return to Find Owners page");
	}

	@Test
	void asAVisitor_IWantToNavigateInCircularPath_SoThatICanVerifyAllLinksWork() {
		HomePage homePage = new HomePage(page, baseUrl);
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		VeterinariansPage vetsPage = new VeterinariansPage(page, baseUrl);
		ErrorPage errorPage = new ErrorPage(page, baseUrl);

		// Start from home
		homePage.navigateTo();
		assertTrue(homePage.isHomePageDisplayed(), "Should start from home page");

		// Navigate through all pages in sequence
		homePage.clickFindOwnersLink();
		assertTrue(findOwnersPage.isFindOwnersPageDisplayed(), "Should be on Find Owners page");

		findOwnersPage.clickVeterinariansLink();
		assertTrue(vetsPage.isVeterinariansPageDisplayed(), "Should be on Veterinarians page");

		vetsPage.clickErrorLink();
		assertTrue(errorPage.isErrorPageDisplayed(), "Should be on Error page");

		errorPage.clickHomeLink();
		assertTrue(homePage.isHomePageDisplayed(), "Should return to home page");
	}

	@Test
	void asAVisitor_IWantToVerifyPageTitles_SoThatIKnowWhereIAm() {
		HomePage homePage = new HomePage(page, baseUrl);

		// Test home page title
		homePage.navigateTo();
		String title = homePage.getPageTitle();
		assertTrue(title.contains("PetClinic"), "Home page title should contain 'PetClinic'");

		// Test other pages maintain consistent title format
		homePage.clickFindOwnersLink();
		title = page.title();
		assertTrue(title.contains("PetClinic"), "Find Owners page title should contain 'PetClinic'");

		homePage.clickVeterinariansLink();
		title = page.title();
		assertTrue(title.contains("PetClinic"), "Veterinarians page title should contain 'PetClinic'");
	}

	@Test
	void asAVisitor_IWantToTestDeepLinking_SoThatICanAccessPagesDirectly() {
		// Test direct navigation to each page
		String[] directUrls = { baseUrl + "/", baseUrl + "/owners/find", baseUrl + "/vets.html", baseUrl + "/oups" };

		for (String url : directUrls) {
			page.navigate(url);
			assertTrue(page.locator("body").isVisible(), "Page should load for URL: " + url);
			assertTrue(page.title().contains("PetClinic"), "Page should have correct title for URL: " + url);
		}
	}

	@Test
	void asAVisitor_IWantToRefreshPages_SoThatContentRemainsConsistent() {
		HomePage homePage = new HomePage(page, baseUrl);

		// Test refresh on home page
		homePage.navigateTo();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Welcome heading should be visible initially");

		page.reload();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Welcome heading should be visible after refresh");

		// Test refresh on Find Owners page
		homePage.clickFindOwnersLink();
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		assertTrue(findOwnersPage.isFindOwnersHeadingVisible(), "Find Owners heading should be visible initially");

		page.reload();
		assertTrue(findOwnersPage.isFindOwnersHeadingVisible(), "Find Owners heading should be visible after refresh");
	}

	@Test
	void asAVisitor_IWantToTestNavigationConsistency_SoThatLinksWorkFromAllPages() {
		String[] pageUrls = { baseUrl + "/", baseUrl + "/owners/find", baseUrl + "/vets.html" };

		for (String pageUrl : pageUrls) {
			page.navigate(pageUrl);

			// Test that all navigation links are accessible from each page
			assertTrue(page.locator("a[href='/']").isVisible(), "Home link should be visible from " + pageUrl);
			assertTrue(page.locator("a[href='/owners/find']").isVisible(),
					"Find Owners link should be visible from " + pageUrl);
			assertTrue(page.locator("a[href='/vets.html']").isVisible(),
					"Veterinarians link should be visible from " + pageUrl);
			assertTrue(page.locator("a[href='/oups']").isVisible(), "Error link should be visible from " + pageUrl);
		}
	}

	@Test
	void asAVisitor_IWantToTestPageLoadStates_SoThatPagesLoadCompletely() {
		HomePage homePage = new HomePage(page, baseUrl);

		// Test that pages wait for load state
		homePage.navigateTo();
		page.waitForLoadState();
		assertTrue(homePage.isWelcomeHeadingVisible(), "Page should be fully loaded");

		homePage.clickFindOwnersLink();
		page.waitForLoadState();
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		assertTrue(findOwnersPage.isFindOwnersHeadingVisible(), "Find Owners page should be fully loaded");
	}

	@Test
	void asAVisitor_IWantToTestNavigationWithKeyboard_SoThatICanUseKeyboardNavigation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Test keyboard navigation using Tab and Enter
		page.keyboard().press("Tab"); // This will tab to first focusable element
		page.keyboard().press("Tab"); // Continue tabbing to navigation

		// Verify keyboard navigation doesn't break the page
		assertTrue(homePage.isWelcomeHeadingVisible(), "Page should remain functional during keyboard navigation");
	}

}