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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.samples.petclinic.e2e.pages.BasePage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.HomePage;
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;
import org.springframework.samples.petclinic.e2e.pages.ErrorPage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * End-to-End tests for Home/Welcome page flows. Tests navigation, page content, and user
 * interface elements.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class HomeFlowTest {

	@LocalServerPort
	private int port;

	private String baseUrl;

	@BeforeEach
	void setUp() {
		baseUrl = "http://localhost:" + port;
	}

	@AfterAll
	static void tearDown() {
		BasePage.closeAll();
	}

	/**
	 * Helper method to create a page with browser availability check
	 */
	private <T extends BasePage> T createPageSafely(PageCreator<T> creator) {
		try {
			return creator.create();
		}
		catch (BasePage.BrowserNotAvailableException e) {
			// Skip test if browser is not available - follows requirement to skip tests
			// that can't be fixed
			assumeTrue(false, "Browser not available for E2E testing: " + e.getMessage());
			return null; // Never reached due to assumeTrue(false)
		}
	}

	@FunctionalInterface
	private interface PageCreator<T> {

		T create();

	}

	@Test
	void asAVisitor_IWantToAccessTheHomepage_SoThatICanSeeTheWelcomeMessage() {
		// Given - I am a visitor to the Pet Clinic website
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));

		// When - I navigate to the homepage
		// (Navigation happens in constructor)

		// Then - I should see the welcome page with correct title and content
		assertTrue(homePage.hasCorrectTitle(), "Page should have correct title");
		assertTrue(homePage.isWelcomePageDisplayed(), "Welcome page should be displayed");
		assertEquals("Welcome", homePage.getWelcomeHeading(), "Welcome heading should be displayed");
		assertTrue(homePage.isPetsImageDisplayed(), "Pets image should be displayed");
		assertTrue(homePage.isLogoImageDisplayed(), "Logo image should be displayed");

		homePage.close();
	}

	@Test
	void asAVisitor_IWantToNavigateUsingTheMainMenu_SoThatICanAccessDifferentSections() {
		// Given - I am on the homepage
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));
		assertTrue(homePage.isWelcomePageDisplayed(), "Should start on welcome page");

		// When - I check the navigation menu
		// Then - All navigation links should be present
		assertTrue(homePage.areAllNavigationLinksPresent(), "All navigation links should be present");

		homePage.close();
	}

	@Test
	void asAVisitor_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		// Given - I am on the homepage
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));

		// When - I click on Find Owners in the navigation
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Then - I should be on the Find Owners page
		assertTrue(findOwnersPage.isFindOwnersPageDisplayed(), "Should be on Find Owners page");
		assertEquals("Find Owners", findOwnersPage.getPageHeading(), "Page heading should be correct");
		assertTrue(findOwnersPage.areAllFormElementsPresent(), "All form elements should be present");

		findOwnersPage.close();
	}

	@Test
	void asAVisitor_IWantToNavigateToVeterinarians_SoThatICanViewAvailableVets() {
		// Given - I am on the homepage
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));

		// When - I click on Veterinarians in the navigation
		VeterinariansPage vetsPage = homePage.navigateToVeterinarians();

		// Then - I should be on the Veterinarians page
		assertTrue(vetsPage.isVeterinariansPageDisplayed(), "Should be on Veterinarians page");
		assertEquals("Veterinarians", vetsPage.getPageHeading(), "Page heading should be correct");
		assertTrue(vetsPage.isVetsTableDisplayed(), "Vets table should be displayed");
		assertTrue(vetsPage.getVetCount() > 0, "Should display at least one veterinarian");

		vetsPage.close();
	}

	@Test
	void asAVisitor_IWantToNavigateToErrorPage_SoThatICanSeeCustomErrorHandling() {
		// Given - I am on the homepage
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));

		// When - I click on Error in the navigation
		ErrorPage errorPage = homePage.navigateToError();

		// Then - I should be on the custom error page
		assertTrue(errorPage.isErrorPageDisplayed(), "Should be on Error page");
		assertEquals("Something happened...", errorPage.getErrorHeading(), "Error heading should be correct");
		assertTrue(errorPage.isCustomErrorPage(), "Should display custom error page, not whitelabel");
		assertTrue(errorPage.isErrorImageDisplayed(), "Error image should be displayed");

		errorPage.close();
	}

	@Test
	void asAVisitor_IWantToNavigateBackToHome_SoThatICanReturnToTheStartingPoint() {
		// Given - I am on a different page (Find Owners)
		HomePage homePage = createPageSafely(() -> new HomePage(baseUrl));
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		assertTrue(findOwnersPage.isFindOwnersPageDisplayed(), "Should be on Find Owners page");

		// When - I click on the home/brand link
		HomePage homePageAgain = findOwnersPage.goHome();

		// Then - I should be back on the homepage
		assertTrue(homePageAgain.isWelcomePageDisplayed(), "Should be back on welcome page");
		assertTrue(homePageAgain.hasCorrectTitle(), "Should have correct title");

		homePageAgain.close();
	}

}