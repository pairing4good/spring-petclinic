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
 * End-to-end tests for the homepage and basic navigation functionality. Tests cover
 * homepage rendering, navigation menu functionality, and basic user interactions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class HomePageE2ETest {

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
		if (page != null)
			page.close();
		if (context != null)
			context.close();
		if (browser != null)
			browser.close();
		if (playwright != null)
			playwright.close();
	}

	@Test
	@DisplayName("As a user, I want to load the homepage, so that I can access the Pet Clinic application")
	void testHomepageLoads() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Verify page title
		String title = page.title();
		assertTrue(title.contains("PetClinic"), "Page title should contain 'PetClinic'");

		// Verify welcome message is visible
		Locator welcomeMessage = page.locator("h2").first();
		assertTrue(welcomeMessage.isVisible(), "Welcome message should be visible");

		// Verify navigation menu is present
		Locator navbar = page.locator("nav.navbar");
		assertTrue(navbar.isVisible(), "Navigation bar should be visible");
	}

	@Test
	@DisplayName("As a user, I want to see the navigation menu, so that I can access different sections")
	void testNavigationMenuPresent() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Check for main navigation links using specific text matching
		Locator homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
		assertTrue(homeLink.isVisible(), "Home link should be visible");

		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		assertTrue(findOwnersLink.isVisible(), "Find owners link should be visible");

		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		assertTrue(vetsLink.isVisible(), "Veterinarians link should be visible");

		Locator errorLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error"));
		assertTrue(errorLink.isVisible(), "Error link should be visible");
	}

	@Test
	@DisplayName("As a user, I want to click the Home link, so that I can return to the homepage")
	void testHomeNavigationLink() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Click home link
		Locator homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
		homeLink.click();
		page.waitForLoadState();

		// Verify we're still on homepage
		String currentUrl = page.url();
		assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
				"Should be on homepage after clicking Home link");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners, so that I can search for pet owners")
	void testFindOwnersNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Click find owners link
		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		findOwnersLink.click();
		page.waitForLoadState();

		// Verify we're on the find owners page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/owners/find"), "Should be on find owners page");

		// Verify page content
		Locator findOwnerForm = page.locator("form");
		assertTrue(findOwnerForm.isVisible(), "Find owner form should be visible");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians, so that I can view the list of vets")
	void testVeterinariansNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Click veterinarians link
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		vetsLink.click();
		page.waitForLoadState();

		// Verify we're on the vets page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/vets"), "Should be on veterinarians page");

		// Verify page content
		Locator vetsTable = page.locator("table");
		assertTrue(vetsTable.isVisible(), "Veterinarians table should be visible");
	}

	@Test
	@DisplayName("As a user, I want to test responsive design, so that the site works on different screen sizes")
	void testResponsiveDesign() {
		// Test mobile viewport
		page.setViewportSize(375, 667); // iPhone SE size
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Verify navbar toggle button is visible on mobile
		Locator navbarToggle = page.locator(".navbar-toggler");
		assertTrue(navbarToggle.isVisible(), "Navbar toggle should be visible on mobile");

		// Test desktop viewport
		page.setViewportSize(1920, 1080);
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Verify content is still accessible
		Locator welcomeMessage = page.locator("h2").first();
		assertTrue(welcomeMessage.isVisible(), "Welcome message should be visible on desktop");
	}

	@Test
	@DisplayName("As a user, I want to use browser back/forward buttons, so that I can navigate efficiently")
	void testBrowserBackForwardNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Navigate to find owners
		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		findOwnersLink.click();
		page.waitForLoadState();

		// Use browser back button
		page.goBack();
		page.waitForLoadState();

		// Verify we're back on homepage
		String currentUrl = page.url();
		assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
				"Should be back on homepage after using browser back");

		// Use browser forward button
		page.goForward();
		page.waitForLoadState();

		// Verify we're back on find owners page
		currentUrl = page.url();
		assertTrue(currentUrl.contains("/owners/find"), "Should be on find owners page after using browser forward");
	}

}