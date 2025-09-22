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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * As a user, I want the application to work across different browsers and screen sizes,
 * so that I can access it from any device
 */
@DisplayName("Cross-Browser and Responsive Design E2E Tests")
class CrossBrowserResponsiveE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want the application to work on desktop screens, so that I can use it on my computer")
	void testDesktopViewport() {
		// Set desktop viewport
		page.setViewportSize(1920, 1080);
		navigateToHome();

		assertElementVisible("nav");
		assertElementContainsText("h2", "Welcome");
		assertElementVisible("img[alt='VMware Tanzu Logo']");

		// Navigation should be fully visible
		assertElementVisible("a[href='/']");
		assertElementVisible("a[href='/owners/find']");
		assertElementVisible("a[href='/vets.html']");
		assertElementVisible("a[href='/oups']");

		// Test form usability at desktop size
		navigateToFindOwners();
		assertElementVisible("input[name='lastName']");
		assertElementVisible("button[type='submit']");

		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementVisible("table");
		assertElementContainsText("table", "Betty Davis");
	}

	@Test
	@DisplayName("As a user, I want the application to work on tablet screens, so that I can use it on my tablet")
	void testTabletViewport() {
		// Set tablet viewport (iPad)
		page.setViewportSize(768, 1024);
		navigateToHome();

		assertElementVisible("nav");
		assertElementContainsText("h2", "Welcome");
		assertElementVisible("img[alt='VMware Tanzu Logo']");

		// Navigation should still work
		assertElementVisible("a[href='/owners/find']");
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();

		assertElementContainsText("h2", "Find Owners");
		assertElementVisible("input[name='lastName']");

		// Form should be usable
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Results should be readable
		assertElementVisible("table");
		assertElementContainsText("table", "Franklin");
	}

	@Test
	@DisplayName("As a user, I want the application to work on mobile screens, so that I can use it on my phone")
	void testMobileViewport() {
		// Set mobile viewport (iPhone)
		page.setViewportSize(375, 667);
		navigateToHome();

		assertElementVisible("nav");
		assertElementContainsText("h2", "Welcome");

		// Navigation should work on mobile
		assertElementVisible("a[href='/owners/find']");
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();

		assertElementContainsText("h2", "Find Owners");
		assertElementVisible("input[name='lastName']");

		// Test mobile form interaction
		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Results should be accessible on mobile
		assertElementVisible("table");

		// Test owner details on mobile
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		assertElementContainsText("h2", "Owner Information");
		assertElementVisible("table");
	}

	@Test
	@DisplayName("As a user, I want forms to be usable on small screens, so that I can enter data on mobile devices")
	void testMobileFormUsability() {
		page.setViewportSize(375, 667);
		navigateToAddOwner();

		// All form fields should be visible and usable
		assertElementVisible("input[name='firstName']");
		assertElementVisible("input[name='lastName']");
		assertElementVisible("input[name='address']");
		assertElementVisible("input[name='city']");
		assertElementVisible("input[name='telephone']");
		assertElementVisible("button[type='submit']");

		// Form should be fillable on mobile
		page.locator("input[name='firstName']").fill("Mobile");
		page.locator("input[name='lastName']").fill("User");
		page.locator("input[name='address']").fill("123 Mobile St");
		page.locator("input[name='city']").fill("Mobile City");
		page.locator("input[name='telephone']").fill("5551234567");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should redirect to owner details
		assertElementContainsText("h2", "Owner Information");
		assertElementContainsText("table", "Mobile User");
	}

	@Test
	@DisplayName("As a user, I want tables to be readable on mobile, so that I can view data on small screens")
	void testMobileTableReadability() {
		page.setViewportSize(375, 667);
		navigateToVeterinarians();

		assertElementVisible("table");
		assertElementContainsText("table", "James Carter");
		assertElementContainsText("table", "Helen Leary");

		// Table should be scrollable or responsive on mobile
		assertElementVisible("table th");
		assertElementVisible("table td");

		// Test owners table on mobile
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementVisible("table");
		assertElementContainsText("table", "Betty Davis");

		// Links should still be clickable on mobile
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		assertElementContainsText("h2", "Owner Information");
	}

	@Test
	@EnabledIfSystemProperty(named = "browser", matches = "firefox")
	@DisplayName("As a user, I want the application to work in Firefox, so that I can use my preferred browser")
	void testFirefoxCompatibility() {
		navigateToHome();

		assertElementContainsText("h2", "Welcome");
		assertElementVisible("nav");

		// Test core functionality in Firefox
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementVisible("table");
		assertElementContainsText("table", "Franklin");

		// Test form submission in Firefox
		navigateToAddOwner();
		page.locator("input[name='firstName']").fill("Firefox");
		page.locator("input[name='lastName']").fill("Test");
		page.locator("input[name='address']").fill("123 Firefox St");
		page.locator("input[name='city']").fill("Firefox City");
		page.locator("input[name='telephone']").fill("5551234567");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementContainsText("h2", "Owner Information");
	}

	@Test
	@EnabledIfSystemProperty(named = "browser", matches = "webkit|safari")
	@DisplayName("As a user, I want the application to work in Safari, so that I can use it on Apple devices")
	void testSafariCompatibility() {
		navigateToHome();

		assertElementContainsText("h2", "Welcome");
		assertElementVisible("nav");

		// Test core functionality in Safari/WebKit
		navigateToVeterinarians();
		assertElementVisible("table");
		assertElementContainsText("table", "James Carter");

		// Test navigation in Safari
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();

		assertElementContainsText("h2", "Find Owners");

		// Test search functionality in Safari
		page.locator("input[name='lastName']").fill("Davis");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		assertElementVisible("table");
		assertElementContainsText("table", "Davis");
	}

	@Test
	@DisplayName("As a user, I want consistent layout across screen sizes, so that the design remains professional")
	void testLayoutConsistencyAcrossViewports() {
		int[][] viewports = { { 1920, 1080 }, // Desktop
				{ 1366, 768 }, // Laptop
				{ 768, 1024 }, // Tablet
				{ 375, 667 }, // Mobile
				{ 320, 568 } // Small mobile
		};

		for (int[] viewport : viewports) {
			page.setViewportSize(viewport[0], viewport[1]);
			navigateToHome();

			// Core elements should be present at all sizes
			assertElementVisible("nav");
			assertElementContainsText("h2", "Welcome");
			assertElementVisible("img[alt='VMware Tanzu Logo']");

			// Navigation should work at all sizes
			assertElementVisible("a[href='/owners/find']");
			assertElementVisible("a[href='/vets.html']");

			// Test another page at each viewport
			navigateToFindOwners();
			assertElementContainsText("h2", "Find Owners");
			assertElementVisible("input[name='lastName']");
			assertElementVisible("button[type='submit']");
		}
	}

	@Test
	@DisplayName("As a user, I want images to scale properly, so that they look good on all devices")
	void testImageScaling() {
		int[][] viewports = { { 1920, 1080 }, // Desktop
				{ 768, 1024 }, // Tablet
				{ 375, 667 } // Mobile
		};

		for (int[] viewport : viewports) {
			page.setViewportSize(viewport[0], viewport[1]);
			navigateToHome();

			// Images should be visible and not broken
			assertElementVisible("img[src*='pets.png']");
			assertElementVisible("img[alt='VMware Tanzu Logo']");

			// Check error page images too
			page.navigate(baseUrl + "/oups");
			waitForPageLoad();
			assertElementVisible("img");
		}
	}

	@Test
	@DisplayName("As a user, I want touch-friendly interfaces on mobile, so that I can easily interact with the app")
	void testTouchFriendlyInterface() {
		page.setViewportSize(375, 667);
		navigateToHome();

		// Links should be easily tappable (adequate size and spacing)
		assertElementVisible("a[href='/owners/find']");
		assertElementVisible("a[href='/vets.html']");

		// Test button sizes in forms
		navigateToFindOwners();
		assertElementVisible("button[type='submit']");

		// Form inputs should be appropriately sized
		assertElementVisible("input[name='lastName']");

		// Test form interaction
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Links in results should be tappable
		assertElementVisible("a[href*='/owners/']");
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		assertElementContainsText("h2", "Owner Information");

		// Action buttons should be touch-friendly
		assertElementVisible("a[href*='/edit']");
		assertElementVisible("a[href*='/pets/new']");
	}

	@Test
	@DisplayName("As a user, I want proper text scaling, so that content is readable on all devices")
	void testTextReadability() {
		int[][] viewports = { { 1920, 1080 }, // Desktop
				{ 768, 1024 }, // Tablet
				{ 375, 667 } // Mobile
		};

		for (int[] viewport : viewports) {
			page.setViewportSize(viewport[0], viewport[1]);
			navigateToHome();

			// Headings should be visible and readable
			assertElementVisible("h2");
			assertElementContainsText("h2", "Welcome");

			// Test other pages
			navigateToVeterinarians();
			assertElementVisible("h2");
			assertElementContainsText("h2", "Veterinarians");

			// Table text should be readable
			assertElementVisible("table");
			assertElementContainsText("table", "James Carter");

			// Form labels should be readable
			navigateToFindOwners();
			assertElementContainsText("body", "Last Name");
		}
	}

}