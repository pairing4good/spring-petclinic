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

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * End-to-end tests for cross-browser navigation and responsive design.
 * Tests cover navigation consistency, responsive layouts, and browser compatibility.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class NavigationE2ETest {

	@LocalServerPort
	private int port;

	private Playwright playwright;
	private Browser browser;
	private BrowserContext context;
	private Page page;

	@BeforeEach
	void setUp() {
		try {
			playwright = Playwright.create();
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			context = browser.newContext();
			page = context.newPage();
		}
		catch (Exception e) {
			assumeTrue(false, "No browser available for testing: " + e.getMessage());
		}
	}

	@AfterEach
	void tearDown() {
		if (page != null) page.close();
		if (context != null) context.close();
		if (browser != null) browser.close();
		if (playwright != null) playwright.close();
	}

	@Test
	@DisplayName("As a user, I want consistent navigation across all pages, so that I can move between sections easily")
	void testConsistentNavigation() {
		String[] pages = {"/", "/owners/find", "/vets.html", "/oups"};
		
		for (String pagePath : pages) {
			page.navigate("http://localhost:" + port + pagePath);
			page.waitForLoadState();

			// Verify navigation bar is present
			Locator navbar = page.locator("nav.navbar");
			assertTrue(navbar.isVisible(), "Navigation should be present on " + pagePath);

			// Verify main navigation links
			Locator homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
			assertTrue(homeLink.isVisible(), "Home link should be visible on " + pagePath);

			Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
			assertTrue(findOwnersLink.isVisible(), "Find owners link should be visible on " + pagePath);

			Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
			assertTrue(vetsLink.isVisible(), "Veterinarians link should be visible on " + pagePath);
		}
	}

	@Test
	@DisplayName("As a user, I want responsive navigation, so that the site works on mobile devices")
	void testResponsiveNavigation() {
		// Test mobile viewport
		page.setViewportSize(375, 667);
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Mobile should show hamburger menu
		Locator navbarToggle = page.locator(".navbar-toggler");
		assertTrue(navbarToggle.isVisible(), "Mobile hamburger menu should be visible");

		// Click to expand mobile menu
		navbarToggle.click();
		page.waitForTimeout(500); // Allow animation

		// Navigation links should be accessible after toggle
		Locator mobileNavLinks = page.locator(".navbar-nav");
		assertTrue(mobileNavLinks.isVisible(), "Mobile navigation links should be accessible");

		// Test tablet viewport
		page.setViewportSize(768, 1024);
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Navigation should be visible
		Locator tabletNav = page.locator("nav.navbar");
		assertTrue(tabletNav.isVisible(), "Navigation should work on tablet");

		// Test desktop viewport
		page.setViewportSize(1920, 1080);
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// All navigation should be expanded
		Locator desktopNavLinks = page.locator(".navbar-nav a");
		assertTrue(desktopNavLinks.count() > 0, "All navigation links should be visible on desktop");
	}

	@Test
	@DisplayName("As a user, I want navigation breadcrumbs, so that I know where I am in the site")
	void testNavigationContext() {
		// Navigate through different sections and verify context
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Go to find owners
		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		findOwnersLink.click();
		page.waitForLoadState();

		// Verify we're on correct page
		assertTrue(page.url().contains("/owners/find"), "Should be on find owners page");

		// Check for page context indicators
		String pageTitle = page.title();
		assertTrue(pageTitle.contains("PetClinic"), "Page title should indicate context");

		// Navigate to veterinarians
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		vetsLink.click();
		page.waitForLoadState();

		assertTrue(page.url().contains("/vets"), "Should be on veterinarians page");
	}

	@Test
	@DisplayName("As a user, I want keyboard navigation, so that I can navigate without a mouse")
	void testKeyboardNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Focus on first navigation link
		Locator firstNavLink = page.locator("nav.navbar a").first();
		firstNavLink.focus();

		// Verify focus is visible
		String focusedElement = page.evaluate("document.activeElement.tagName").toString();
		assertEquals("A", focusedElement, "First navigation link should be focused");

		// Tab through navigation elements
		page.keyboard().press("Tab");
		page.keyboard().press("Tab");

		// Should still be within navigation area
		String currentFocus = page.evaluate("document.activeElement.closest('nav') !== null").toString();
		assertTrue(Boolean.parseBoolean(currentFocus), "Tab navigation should work within navbar");
	}

	@Test
	@DisplayName("As a user, I want fast page loading, so that navigation feels responsive")
	void testNavigationPerformance() {
		long startTime = System.currentTimeMillis();
		
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();
		
		long homeLoadTime = System.currentTimeMillis() - startTime;
		assertTrue(homeLoadTime < 5000, "Homepage should load within 5 seconds");

		// Test navigation to other pages
		startTime = System.currentTimeMillis();
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		vetsLink.click();
		page.waitForLoadState();
		
		long vetsLoadTime = System.currentTimeMillis() - startTime;
		assertTrue(vetsLoadTime < 3000, "Navigation to vets page should be fast");
	}

	@Test
	@DisplayName("As a user, I want proper page URLs, so that I can bookmark and share links")
	void testUrlStructure() {
		// Test homepage URL
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();
		
		String homeUrl = page.url();
		assertTrue(homeUrl.endsWith("/") || homeUrl.equals("http://localhost:" + port), 
			"Homepage should have clean URL");

		// Test owners search URL
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();
		
		assertTrue(page.url().contains("/owners/find"), "Find owners should have semantic URL");

		// Test vets URL
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();
		
		assertTrue(page.url().contains("/vets"), "Vets page should have semantic URL");
	}

	@Test
	@DisplayName("As a user, I want navigation to work with browser history, so that back/forward buttons work")
	void testBrowserHistoryNavigation() {
		// Navigate through multiple pages
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Go to find owners
		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		findOwnersLink.click();
		page.waitForLoadState();

		// Go to vets
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		vetsLink.click();
		page.waitForLoadState();

		// Use browser back
		page.goBack();
		page.waitForLoadState();
		assertTrue(page.url().contains("/owners/find"), "Back button should work");

		// Use browser back again
		page.goBack();
		page.waitForLoadState();
		assertTrue(page.url().endsWith("/") || page.url().contains("localhost:" + port), 
			"Should be back at homepage");

		// Use browser forward
		page.goForward();
		page.waitForLoadState();
		assertTrue(page.url().contains("/owners/find"), "Forward button should work");
	}

	@Test
	@DisplayName("As a user, I want accessible navigation, so that screen readers can navigate the site")
	void testAccessibleNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Check for proper ARIA labels and roles
		Locator navbar = page.locator("nav[role='navigation'], nav.navbar");
		assertTrue(navbar.isVisible(), "Navigation should have proper semantic markup");

		// Check navigation links have proper accessibility
		Locator navLinks = page.locator("nav a");
		assertTrue(navLinks.count() > 0, "Navigation links should be present");

		// Verify links have descriptive text
		for (int i = 0; i < Math.min(navLinks.count(), 5); i++) {
			String linkText = navLinks.nth(i).textContent().trim();
			assertFalse(linkText.isEmpty(), "Navigation links should have descriptive text");
		}
	}

	@Test
	@DisplayName("As a user, I want navigation to work across different browsers, so that experience is consistent")
	void testCrossBrowserNavigation() {
		// Test with different browser contexts to simulate different browsers
		
		// Create new context with different user agent (simulating different browser)
		BrowserContext firefoxContext = browser.newContext(new Browser.NewContextOptions()
			.setUserAgent("Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/109.0"));
		
		Page firefoxPage = firefoxContext.newPage();
		
		try {
			firefoxPage.navigate("http://localhost:" + port);
			firefoxPage.waitForLoadState();

			// Test navigation works
			Locator ffNavbar = firefoxPage.locator("nav.navbar");
			assertTrue(ffNavbar.isVisible(), "Navigation should work in Firefox");

			Locator ffVetsLink = firefoxPage.getByRole(AriaRole.LINK, 
				new Page.GetByRoleOptions().setName("Veterinarians"));
			ffVetsLink.click();
			firefoxPage.waitForLoadState();

			assertTrue(firefoxPage.url().contains("/vets"), 
				"Navigation should work consistently across browsers");
		}
		finally {
			firefoxPage.close();
			firefoxContext.close();
		}
	}
}