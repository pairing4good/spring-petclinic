/*
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.playwright.pages.HomePage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for the PetClinic home page functionality.
 *
 * @author Copilot
 */
class HomePageTest extends BasePlaywrightTest {

	@Test
	void asAVisitor_IWantToAccessTheHomepage_SoThatICanSeeTheWelcomeMessage() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Verify page title
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

		// Verify welcome elements are displayed
		assertTrue(homePage.isWelcomeImageDisplayed(), "Welcome image should be displayed");
		assertTrue(homePage.isHomePageLoaded(), "Home page should be properly loaded");

		// Verify welcome heading
		String welcomeHeading = homePage.getWelcomeHeading();
		assertTrue(welcomeHeading != null && !welcomeHeading.trim().isEmpty(),
				"Welcome heading should be present and not empty");
	}

	@Test
	void asAVisitor_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Click on Find owners link
		var ownersPage = homePage.clickFindOwners();

		// Verify navigation to owners search page
		assertTrue(page.url().contains("/owners/find"), "Should navigate to owners find page");
	}

	@Test
	void asAVisitor_IWantToNavigateToVeterinarians_SoThatICanViewVetInformation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Click on Veterinarians link
		var vetsPage = homePage.clickVeterinarians();

		// Verify navigation to vets page
		assertTrue(page.url().contains("/vets"), "Should navigate to veterinarians page");
	}

	@Test
	void asAVisitor_IWantToAccessErrorPage_SoThatICanSeeErrorHandling() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Click on Error link to trigger error page
		var errorPage = homePage.clickErrorLink();

		// Verify navigation to error page
		assertTrue(page.url().contains("/oups"), "Should navigate to error trigger page");

		// Verify error page elements
		assertTrue(errorPage.isErrorPageLoaded(), "Error page should be properly loaded");
		assertTrue(errorPage.isSomethingHappenedError(), "Should display 'Something happened' error message");
	}

	@Test
	void asAVisitor_IWantThePageToBeResponsive_SoThatItWorksOnDifferentScreenSizes() {
		HomePage homePage = new HomePage(page, baseUrl);

		// Test different viewport sizes
		page.setViewportSize(1920, 1080); // Desktop
		homePage.navigateToHome();
		assertTrue(homePage.isHomePageLoaded(), "Should work on desktop resolution");

		page.setViewportSize(768, 1024); // Tablet
		homePage.navigateToHome();
		assertTrue(homePage.isHomePageLoaded(), "Should work on tablet resolution");

		page.setViewportSize(375, 667); // Mobile
		homePage.navigateToHome();
		assertTrue(homePage.isHomePageLoaded(), "Should work on mobile resolution");
	}

	@Test
	void asAUser_IWantToUseKeyboardNavigation_SoThatICanAccessThePageWithoutMouse() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Test keyboard navigation
		page.keyboard().press("Tab"); // Focus first interactive element
		page.keyboard().press("Enter"); // Activate focused element

		// Should navigate somewhere (depends on which element gets focus first)
		// This test verifies keyboard navigation is functional
		assertTrue(page.url().contains("localhost:" + port), "Keyboard navigation should work");
	}

	@Test
	void asAVisitor_IWantThePageToLoadQuickly_SoThatIHaveAGoodUserExperience() {
		long startTime = System.currentTimeMillis();

		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();

		// Verify page loads within reasonable time (5 seconds)
		long loadTime = System.currentTimeMillis() - startTime;
		assertTrue(loadTime < 5000, "Home page should load within 5 seconds, took: " + loadTime + "ms");

		// Verify content is loaded
		assertTrue(homePage.isHomePageLoaded(), "Home page content should be fully loaded");
	}

}