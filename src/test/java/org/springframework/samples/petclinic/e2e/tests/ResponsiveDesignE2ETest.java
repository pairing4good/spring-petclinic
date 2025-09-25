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
import org.springframework.samples.petclinic.e2e.pages.HomePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for responsive design across different viewport sizes. Tests mobile, tablet,
 * and desktop layouts.
 */
class ResponsiveDesignE2ETest extends BaseE2ETest {

	@Test
	void asAMobileUser_IWantToViewHomepage_SoThatItDisplaysCorrectlyOnMobile() {
		// Set mobile viewport
		page.setViewportSize(375, 667); // iPhone 6/7/8 size

		HomePage homePage = new HomePage(page, baseUrl).open();

		// Verify basic elements are visible on mobile
		assertTrue(homePage.getWelcomeMessage().contains("Welcome"));
		assertTrue(page.locator("nav.navbar").isVisible());
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible());

		// Check that navigation menu works on mobile
		assertTrue(page.locator("nav.navbar-nav").isVisible());
	}

	@Test
	void asATabletUser_IWantToViewHomepage_SoThatItDisplaysCorrectlyOnTablet() {
		// Set tablet viewport
		page.setViewportSize(768, 1024); // iPad size

		HomePage homePage = new HomePage(page, baseUrl).open();

		// Verify elements are properly arranged for tablet
		assertTrue(homePage.getWelcomeMessage().contains("Welcome"));
		assertTrue(page.locator("nav.navbar").isVisible());
		assertTrue(page.locator("img[src*='pets.png']").isVisible());
	}

	@Test
	void asADesktopUser_IWantToViewHomepage_SoThatItDisplaysCorrectlyOnDesktop() {
		// Set desktop viewport
		page.setViewportSize(1920, 1080); // Full HD desktop

		HomePage homePage = new HomePage(page, baseUrl).open();

		// Verify full desktop layout
		assertTrue(homePage.getWelcomeMessage().contains("Welcome"));
		assertTrue(page.locator("nav.navbar").isVisible());
		assertTrue(page.locator("img[src*='pets.png']").isVisible());
		assertTrue(page.locator("img[alt='VMware Tanzu Logo']").isVisible());
	}

	@Test
	void asAMobileUser_IWantToNavigate_SoThatMenuWorksOnMobileDevices() {
		// Set mobile viewport
		page.setViewportSize(375, 667);

		HomePage homePage = new HomePage(page, baseUrl).open();

		// Check if mobile menu button exists (Bootstrap navbar toggle)
		if (page.locator("button.navbar-toggler").isVisible()) {
			// Click mobile menu toggle if it exists
			page.locator("button.navbar-toggler").click();
		}

		// Navigation links should be accessible
		assertTrue(page.locator("nav a:has-text('Find Owners')").isVisible());

		// Test navigation on mobile
		homePage.goToFindOwners();
		assertTrue(page.url().contains("/owners/find"));
	}

	@Test
	void asAUser_IWantTablestoBeResponsive_SoThatTheyWorkOnAllDevices() {
		// Test with mobile first
		page.setViewportSize(375, 667);
		navigateTo("/vets.html");
		page.waitForLoadState();

		// Table should be visible and accessible on mobile
		assertTrue(page.locator("table").isVisible());
		assertTrue(page.locator("table th:has-text('Name')").isVisible());
		assertTrue(page.locator("table th:has-text('Specialties')").isVisible());

		// Switch to tablet
		page.setViewportSize(768, 1024);
		page.reload();
		page.waitForLoadState();

		// Table should still be properly displayed
		assertTrue(page.locator("table").isVisible());

		// Switch to desktop
		page.setViewportSize(1920, 1080);
		page.reload();
		page.waitForLoadState();

		// Table should have full layout on desktop
		assertTrue(page.locator("table").isVisible());
	}

	@Test
	void asAMobileUser_IWantFormsToBeUsable_SoThatICanAddOwnersOnMobile() {
		// Set mobile viewport
		page.setViewportSize(375, 667);

		navigateTo("/owners/new");
		page.waitForLoadState();

		// Form should be usable on mobile
		assertTrue(page.locator("input[name='firstName']").isVisible());
		assertTrue(page.locator("input[name='lastName']").isVisible());
		assertTrue(page.locator("input[name='address']").isVisible());
		assertTrue(page.locator("input[name='city']").isVisible());
		assertTrue(page.locator("input[name='telephone']").isVisible());
		assertTrue(page.locator("button:has-text('Add Owner')").isVisible());

		// Test form interaction on mobile
		page.locator("input[name='firstName']").fill("Mobile");
		page.locator("input[name='lastName']").fill("User");

		// Verify inputs work on mobile
		assertTrue(page.locator("input[name='firstName']").inputValue().equals("Mobile"));
		assertTrue(page.locator("input[name='lastName']").inputValue().equals("User"));
	}

	@Test
	void asAUser_IWantImagestoScale_SoThatTheyLookGoodOnAllDevices() {
		// Test image scaling across different viewport sizes

		// Mobile
		page.setViewportSize(375, 667);
		HomePage homePage = new HomePage(page, baseUrl).open();
		assertTrue(page.locator("img[src*='pets.png']").isVisible());

		// Tablet
		page.setViewportSize(768, 1024);
		page.reload();
		page.waitForLoadState();
		assertTrue(page.locator("img[src*='pets.png']").isVisible());

		// Desktop
		page.setViewportSize(1920, 1080);
		page.reload();
		page.waitForLoadState();
		assertTrue(page.locator("img[src*='pets.png']").isVisible());
		assertTrue(page.locator("img[alt='VMware Tanzu Logo']").isVisible());
	}

	@Test
	void asAUser_IWantConsistentStyling_SoThatTheAppLooksGoodOnAllDevices() {
		int[] viewportWidths = { 375, 768, 1024, 1920 }; // Mobile, tablet, desktop sizes

		for (int width : viewportWidths) {
			page.setViewportSize(width, 800);
			HomePage homePage = new HomePage(page, baseUrl).open();

			// Verify consistent elements across all sizes
			assertTrue(page.locator("nav.navbar").isVisible());
			assertTrue(page.locator("h2:has-text('Welcome')").isVisible());
			assertTrue(page.locator(".container").isVisible());

			// Test navigation works at this size
			page.locator("nav a:has-text('Veterinarians')").click();
			assertTrue(page.url().contains("/vets.html"));
			assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible());

			// Return to home for next iteration
			page.locator("nav a:has-text('Home')").click();
		}
	}

}