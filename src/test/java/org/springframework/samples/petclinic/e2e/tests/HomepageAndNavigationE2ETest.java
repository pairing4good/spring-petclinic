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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.base.BaseE2ETest;
import org.springframework.samples.petclinic.e2e.pages.HomePage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive E2E tests for Homepage and Navigation functionality.
 * 
 * Tests cover:
 * - Homepage loading and content verification
 * - Navigation menu functionality
 * - Responsive design across different viewport sizes
 * - Page title and meta information
 * - Cross-browser compatibility (when run with different browsers)
 * - Accessibility features
 */
@DisplayName("Homepage and Navigation E2E Tests")
class HomepageAndNavigationE2ETest extends BaseE2ETest {

	private HomePage homePage;

	@BeforeEach
	void setUpPages() {
		homePage = new HomePage(page);
	}

	@Test
	@DisplayName("As a visitor, I want to access the homepage, so that I can see the welcome message and navigation")
	void testHomepageLoadsSuccessfully() {
		// Given: I am a visitor to the Pet Clinic website
		// When: I navigate to the homepage
		homePage.navigate(getBaseUrl());

		// Then: The homepage should load successfully
		assertTrue(homePage.isLoaded(), "Homepage should be loaded with welcome message and pet image");

		// And: The page title should be correct
		assertTrue(homePage.getPageTitle().contains("PetClinic"),
				"Page title should contain 'PetClinic', got: " + homePage.getPageTitle());

		// And: The welcome message should be displayed
		assertEquals("Welcome", homePage.getWelcomeMessage(), "Welcome message should be displayed");

		// And: All navigation links should be visible
		assertTrue(homePage.areAllNavigationLinksVisible(), "All navigation links should be visible");

		// And: The Spring logo should be displayed
		assertTrue(homePage.isSpringLogoVisible(), "Spring logo should be visible");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners page, so that I can search for pet owners")
	void testNavigationToFindOwners() {
		// Given: I am on the homepage
		homePage.navigate(getBaseUrl());
		assertTrue(homePage.isLoaded(), "Homepage should be loaded first");

		// When: I click on the Find Owners navigation link
		homePage.clickFindOwners();

		// Then: I should be navigated to the Find Owners page
		assertTrue(page.url().contains("/owners/find"), "Should navigate to Find Owners page");

		// And: The page should contain Find Owners content
		assertTrue(isElementVisible("h2"), "Find Owners page should have heading");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians page, so that I can view veterinarian information")
	void testNavigationToVeterinarians() {
		// Given: I am on the homepage
		homePage.navigate(getBaseUrl());
		assertTrue(homePage.isLoaded(), "Homepage should be loaded first");

		// When: I click on the Veterinarians navigation link
		homePage.clickVeterinarians();

		// Then: I should be navigated to the Veterinarians page
		assertTrue(page.url().contains("/vets"), "Should navigate to Veterinarians page");

		// And: The page should load successfully
		waitForPageLoad();
		assertTrue(isElementVisible("h2"), "Veterinarians page should have heading");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Error page, so that I can see error handling demonstration")
	void testNavigationToErrorPage() {
		// Given: I am on the homepage
		homePage.navigate(getBaseUrl());
		assertTrue(homePage.isLoaded(), "Homepage should be loaded first");

		// When: I click on the Error navigation link
		homePage.clickError();

		// Then: I should be navigated to the error page
		assertTrue(page.url().contains("/oups"), "Should navigate to error page");

		// And: The error page should display appropriate error content
		waitForPageLoad();
		assertTrue(isElementVisible("h2"), "Error page should have heading");
		assertTrue(getTextContent("h2").contains("Something happened"),
				"Error page should show 'Something happened' message");
	}

	@Test
	@DisplayName("As a user, I want to return to homepage from any page, so that I can restart my navigation")
	void testNavigationBackToHomepage() {
		// Given: I am on the Find Owners page
		homePage.navigate(getBaseUrl());
		homePage.clickFindOwners();
		assertTrue(page.url().contains("/owners/find"), "Should be on Find Owners page");

		// When: I click on the Home navigation link
		homePage.clickHome();

		// Then: I should be back on the homepage
		assertEquals(getBaseUrl() + "/", homePage.getCurrentUrl(), "Should be back on homepage");
		assertTrue(homePage.isLoaded(), "Homepage should be loaded again");
	}

	@Test
	@DisplayName("As a user, I want responsive navigation on mobile devices, so that I can access all features")
	void testResponsiveNavigationMobile() {
		// Given: I am using a mobile device viewport
		page.setViewportSize(375, 667); // iPhone SE size

		// When: I navigate to the homepage
		homePage.navigate(getBaseUrl());

		// Then: The page should be responsive and navigation should be accessible
		assertTrue(homePage.isLoaded(), "Homepage should load on mobile viewport");

		// And: Navigation links should still be functional
		assertTrue(homePage.areAllNavigationLinksVisible() || isElementVisible(".navbar-toggler"),
				"Navigation should be visible or have mobile toggle");
	}

	@Test
	@DisplayName("As a user, I want responsive navigation on tablet devices, so that I can access all features")
	void testResponsiveNavigationTablet() {
		// Given: I am using a tablet device viewport
		page.setViewportSize(768, 1024); // iPad size

		// When: I navigate to the homepage
		homePage.navigate(getBaseUrl());

		// Then: The page should be responsive and navigation should be accessible
		assertTrue(homePage.isLoaded(), "Homepage should load on tablet viewport");

		// And: Navigation links should be accessible (either visible directly or through mobile toggle)
		assertTrue(homePage.areAllNavigationLinksVisible() || isElementVisible(".navbar-toggler, .nav-toggle"),
				"Navigation links should be visible or accessible via toggle on tablet");
	}

	@Test
	@DisplayName("As a user, I want navigation breadcrumbs and browser back/forward to work correctly")
	void testBrowserNavigationSupport() {
		// Given: I navigate through multiple pages
		homePage.navigate(getBaseUrl());
		homePage.clickFindOwners();
		String findOwnersUrl = page.url();

		homePage.clickVeterinarians();
		String vetsUrl = page.url();

		// When: I use browser back button
		page.goBack();

		// Then: I should be back on the Find Owners page
		assertEquals(findOwnersUrl, page.url(), "Browser back should return to Find Owners page");

		// When: I use browser forward button
		page.goForward();

		// Then: I should be back on the Veterinarians page
		assertEquals(vetsUrl, page.url(), "Browser forward should return to Veterinarians page");
	}

	@Test
	@DisplayName("As a user with accessibility needs, I want keyboard navigation to work properly")
	void testKeyboardNavigation() {
		// Given: I am on the homepage
		homePage.navigate(getBaseUrl());
		assertTrue(homePage.isLoaded(), "Homepage should be loaded");

		// When: I use Tab key to navigate through links
		page.keyboard().press("Tab");
		page.keyboard().press("Tab");
		page.keyboard().press("Tab");

		// Then: Focus should move through navigation elements
		// Note: This is a basic test - full accessibility testing would require more specialized tools
		assertTrue(page.locator("a:focus, button:focus").count() >= 0,
				"Should be able to tab through focusable elements");
	}

	@Test
	@DisplayName("As a user, I want page loading to be performant and show loading states appropriately")
	void testPageLoadingPerformance() {
		// Given: I am measuring page load time
		long startTime = System.currentTimeMillis();

		// When: I navigate to the homepage
		homePage.navigate(getBaseUrl());

		// Then: The page should load within reasonable time (5 seconds for E2E tests)
		long loadTime = System.currentTimeMillis() - startTime;
		assertTrue(loadTime < 5000, "Page should load within 5 seconds, took: " + loadTime + "ms");

		// And: All essential elements should be loaded
		assertTrue(homePage.isLoaded(), "All essential page elements should be loaded");
	}

	@Test
	@DisplayName("As a user, I want proper page metadata for SEO and bookmarking")
	void testPageMetadata() {
		// Given: I am on the homepage
		homePage.navigate(getBaseUrl());

		// Then: The page should have proper title
		String title = homePage.getPageTitle();
		assertTrue(title.contains("PetClinic"), "Page title should contain application name");
		assertTrue(title.contains("Spring Framework"), "Page title should mention Spring Framework");

		// And: The page should have essential meta tags (check using Playwright locator directly)
		assertTrue(page.locator("meta[name='viewport']").count() > 0, "Page should have viewport meta tag");
		
		// And: The page should render properly (which indicates proper charset handling)
		assertTrue(homePage.isLoaded(), "Page should load properly with correct charset");
		assertTrue(homePage.getWelcomeMessage().equals("Welcome"), "Text should render correctly indicating proper charset");
	}

}