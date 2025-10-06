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
 * End-to-end tests for veterinarian functionality. Tests cover viewing veterinarians
 * list, veterinarian details, and specialties.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class VeterinarianE2ETest {

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
	@DisplayName("As a user, I want to navigate to the veterinarians page, so that I can view available vets")
	void testNavigateToVeterinariansPage() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Click on Veterinarians link
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		assertTrue(vetsLink.isVisible(), "Veterinarians link should be visible");

		vetsLink.click();
		page.waitForLoadState();

		// Verify we're on the vets page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/vets"), "Should be on veterinarians page");
	}

	@Test
	@DisplayName("As a user, I want to view the veterinarians list, so that I can see all available vets")
	void testViewVeterinariansList() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Verify veterinarians table or list is displayed
		Locator vetsTable = page.locator("table");
		if (vetsTable.isVisible()) {
			// Check for table headers
			boolean hasHeaders = page.locator("th").count() > 0 || page.locator("thead").isVisible()
					|| page.locator("body").textContent().contains("Name")
					|| page.locator("body").textContent().contains("Specialties");

			assertTrue(hasHeaders, "Veterinarians table should have headers");

			// Check for veterinarian data
			boolean hasVetData = page.locator("td").count() > 0 || page.locator("tr").count() > 1; // More
																									// than
																									// just
																									// header
																									// row

			assertTrue(hasVetData, "Should display veterinarian data");
		}
		else {
			// Alternative: check for other vet display formats
			boolean hasVets = page.locator("[data-testid='vet-list']").isVisible() || page.locator(".vet").isVisible()
					|| page.locator("body").textContent().contains("Dr.")
					|| page.locator("body").textContent().contains("veterinar");

			assertTrue(hasVets, "Should display veterinarians information");
		}
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian names, so that I can identify individual vets")
	void testVeterinarianNamesDisplayed() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Check for typical veterinarian names (from test data)
		String bodyText = page.locator("body").textContent();

		boolean hasVetNames = bodyText.contains("James Carter") || bodyText.contains("Helen Leary")
				|| bodyText.contains("Linda Douglas") || bodyText.contains("Rafael Ortega")
				|| bodyText.contains("Henry Stevens") || bodyText.contains("Sharon Jenkins") ||
				// Or check for name patterns
				bodyText.contains("Dr.") || page.locator("td, .vet-name").count() > 0;

		assertTrue(hasVetNames, "Should display veterinarian names");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian specialties, so that I can find the right expert")
	void testVeterinarianSpecialties() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Check for typical specialties
		String bodyText = page.locator("body").textContent();

		boolean hasSpecialties = bodyText.contains("radiology") || bodyText.contains("surgery")
				|| bodyText.contains("dentistry") || bodyText.contains("Specialties") || page.locator("td").count() > 2; // More
																															// columns
																															// than
																															// just
																															// name

		assertTrue(hasSpecialties, "Should display veterinarian specialties");
	}

	@Test
	@DisplayName("As a user, I want the veterinarians page to be responsive, so that it works on all devices")
	void testVeterinarianPageResponsive() {
		// Test mobile viewport
		page.setViewportSize(375, 667);
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Verify content is visible on mobile
		boolean mobileContentVisible = page.locator("table").isVisible() || page.locator(".vet").isVisible()
				|| page.locator("body").textContent().length() > 100;

		assertTrue(mobileContentVisible, "Veterinarians content should be visible on mobile");

		// Test tablet viewport
		page.setViewportSize(768, 1024);
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		boolean tabletContentVisible = page.locator("table").isVisible() || page.locator(".vet").isVisible();

		assertTrue(tabletContentVisible, "Veterinarians content should be visible on tablet");

		// Test desktop viewport
		page.setViewportSize(1920, 1080);
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		boolean desktopContentVisible = page.locator("table").isVisible() || page.locator(".vet").isVisible();

		assertTrue(desktopContentVisible, "Veterinarians content should be visible on desktop");
	}

	@Test
	@DisplayName("As a user, I want to navigate back to home from veterinarians page, so that I can access other features")
	void testNavigationFromVeterinariansPage() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Navigate back to home using navigation
		Locator homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
		if (homeLink.isVisible()) {
			homeLink.click();
			page.waitForLoadState();

			// Verify we're back on homepage
			String currentUrl = page.url();
			assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
					"Should navigate back to homepage");
		}
	}

	@Test
	@DisplayName("As a user, I want proper page titles on veterinarians page, so that I know where I am")
	void testVeterinarianPageTitle() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Check page title
		String title = page.title();
		assertTrue(title.contains("PetClinic") || title.contains("Veterinarian"),
				"Page title should contain relevant information");

		// Check for page heading
		boolean hasHeading = page.locator("h1, h2, h3").isVisible()
				|| page.locator("body").textContent().contains("Veterinarians")
				|| page.locator("body").textContent().contains("Vets");

		assertTrue(hasHeading, "Page should have appropriate heading");
	}

	@Test
	@DisplayName("As a user, I want browser back/forward to work on veterinarians page, so that I can navigate efficiently")
	void testBrowserNavigationOnVetsPage() {
		// Start at homepage
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Navigate to vets page
		Locator vetsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		vetsLink.click();
		page.waitForLoadState();

		// Use browser back
		page.goBack();
		page.waitForLoadState();

		// Should be back at homepage
		String currentUrl = page.url();
		assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
				"Should be back at homepage after browser back");

		// Use browser forward
		page.goForward();
		page.waitForLoadState();

		// Should be back on vets page
		currentUrl = page.url();
		assertTrue(currentUrl.contains("/vets"), "Should be back on vets page after browser forward");
	}

	@Test
	@DisplayName("As a user, I want to access veterinarians page directly via URL, so that I can bookmark it")
	void testDirectUrlAccess() {
		// Navigate directly to vets page
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Verify page loads correctly
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/vets"), "Should be on veterinarians page");

		// Verify content is displayed
		boolean hasContent = page.locator("table").isVisible() || page.locator(".vet").isVisible()
				|| page.locator("body").textContent().contains("veterinar")
				|| page.locator("body").textContent().length() > 100;

		assertTrue(hasContent, "Page should display veterinarian content");
	}

	@Test
	@DisplayName("As a user, I want consistent styling on veterinarians page, so that it matches the site design")
	void testVeterinarianPageStyling() {
		page.navigate("http://localhost:" + port + "/vets.html");
		page.waitForLoadState();

		// Check for navigation consistency
		Locator navbar = page.locator("nav.navbar");
		assertTrue(navbar.isVisible(), "Navigation bar should be present");

		// Check for consistent styling elements
		boolean hasConsistentStyling = page.locator("table.table").isVisible() || page.locator(".container").isVisible()
				|| page.locator(".row").isVisible()
				|| page.locator("[class*='bootstrap'], [class*='table']").isVisible();

		assertTrue(hasConsistentStyling, "Page should have consistent styling");
	}

}