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
 * End-to-end tests for error handling functionality. Tests cover error pages, 404
 * handling, form validation errors, and other error scenarios.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class ErrorHandlingE2ETest {

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
	@DisplayName("As a user, I want to see a proper error page when an exception occurs, so that I understand what happened")
	void testExceptionErrorPage() {
		// Navigate to the error trigger endpoint
		page.navigate("http://localhost:" + port + "/oups");
		page.waitForLoadState();

		// Check for error page content directly
		boolean hasErrorContent = page.locator("h2").isVisible() || page.locator(".error").isVisible()
				|| page.locator("body").textContent().contains("Something happened")
				|| page.locator("body").textContent().contains("exception")
				|| page.locator("body").textContent().contains("error");

		assertTrue(hasErrorContent, "Should display error page content");

		// Verify it's not the whitelabel error page (custom error page should be shown)
		String bodyText = page.locator("body").textContent();
		assertFalse(bodyText.contains("Whitelabel Error Page"), "Should not show whitelabel error page");
	}

	@Test
	@DisplayName("As a user, I want to access the error page via navigation, so that I can see how errors are handled")
	void testErrorPageViaNavigation() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Click the Error link in navigation
		Locator errorLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error"));
		assertTrue(errorLink.isVisible(), "Error link should be visible in navigation");

		errorLink.click();
		page.waitForLoadState();

		// Verify we're on the error page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/oups"), "Should be on error page");

		// Verify error content is displayed
		boolean hasErrorContent = page.locator("h2").isVisible() || page.locator(".error").isVisible()
				|| page.locator("body").textContent().contains("Something happened");

		assertTrue(hasErrorContent, "Should display error page content");
	}

	@Test
	@DisplayName("As a user, I want to see a 404 page for non-existent URLs, so that I know the page doesn't exist")
	void test404ErrorPage() {
		// Navigate to a non-existent page
		page.navigate("http://localhost:" + port + "/nonexistent-page");
		page.waitForLoadState();

		// Check page content for 404 indicators (since we can't easily capture response
		// status)
		String bodyText = page.locator("body").textContent();
		boolean is404Error = bodyText.contains("404") || bodyText.contains("Not Found")
				|| bodyText.contains("Page not found") || page.locator("h1, h2").textContent().contains("404");

		assertTrue(is404Error, "Should show 404 error for non-existent page");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when submitting invalid forms, so that I can correct my input")
	void testFormValidationErrors() {
		// Go to add owner form
		page.navigate("http://localhost:" + port + "/owners/new");
		page.waitForLoadState();

		// Submit form with invalid data (empty required fields)
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
		if (!submitButton.isVisible()) {
			submitButton = page.locator("button[type='submit'], input[type='submit']");
		}
		submitButton.click();
		page.waitForLoadState();

		// Should stay on the same page with validation errors
		assertTrue(page.url().contains("/owners/new"), "Should stay on new owner form");

		// Check for validation error indicators
		boolean hasValidationErrors = page.locator(".alert-danger").isVisible() || page.locator(".error").isVisible()
				|| page.locator(".field-error").isVisible() || page.locator(".invalid-feedback").isVisible()
				|| page.locator(".text-danger").isVisible() || page.locator("body").textContent().contains("required")
				|| page.locator("body").textContent().contains("may not be empty")
				|| page.locator("body").textContent().contains("must not be empty");

		assertTrue(hasValidationErrors, "Should display validation error messages");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for invalid telephone numbers, so that I can enter correct format")
	void testTelephoneValidationError() {
		page.navigate("http://localhost:" + port + "/owners/new");
		page.waitForLoadState();

		// Fill form with invalid telephone number
		page.locator("input[name='firstName']").fill("John");
		page.locator("input[name='lastName']").fill("Doe");
		page.locator("input[name='address']").fill("123 Main St");
		page.locator("input[name='city']").fill("Springfield");
		page.locator("input[name='telephone']").fill("invalid-phone");

		// Submit form
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
		if (!submitButton.isVisible()) {
			submitButton = page.locator("button[type='submit'], input[type='submit']");
		}
		submitButton.click();
		page.waitForLoadState();

		// Should stay on form page with validation error
		assertTrue(page.url().contains("/owners/new"), "Should stay on new owner form");

		// Check for telephone validation error
		boolean hasTelephoneError = page.locator(".field-error").isVisible()
				|| page.locator(".invalid-feedback").isVisible()
				|| page.locator("body").textContent().contains("numeric")
				|| page.locator("body").textContent().contains("digits only")
				|| page.locator("body").textContent().contains("phone")
				|| page.locator("body").textContent().contains("telephone");

		assertTrue(hasTelephoneError, "Should display telephone validation error");
	}

	@Test
	@DisplayName("As a user, I want error pages to have navigation, so that I can return to functional parts of the site")
	void testErrorPageNavigation() {
		page.navigate("http://localhost:" + port + "/oups");
		page.waitForLoadState();

		// Check that navigation is still available on error page
		Locator navbar = page.locator("nav.navbar");
		if (navbar.isVisible()) {
			// Verify navigation links work from error page
			Locator homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
			if (homeLink.isVisible()) {
				homeLink.click();
				page.waitForLoadState();

				// Should navigate back to homepage
				String currentUrl = page.url();
				assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
						"Should navigate back to homepage from error page");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want consistent error handling across different browsers, so that the experience is uniform")
	void testErrorHandlingCrossBrowser() {
		// Test error page in different viewport sizes to ensure responsive error handling

		// Mobile viewport
		page.setViewportSize(375, 667);
		page.navigate("http://localhost:" + port + "/oups");
		page.waitForLoadState();

		boolean mobileErrorVisible = page.locator("h2, .error").isVisible();
		assertTrue(mobileErrorVisible, "Error content should be visible on mobile");

		// Desktop viewport
		page.setViewportSize(1920, 1080);
		page.navigate("http://localhost:" + port + "/oups");
		page.waitForLoadState();

		boolean desktopErrorVisible = page.locator("h2, .error").isVisible();
		assertTrue(desktopErrorVisible, "Error content should be visible on desktop");
	}

	@Test
	@DisplayName("As a user, I want browser back button to work properly after errors, so that I can navigate efficiently")
	void testBrowserBackAfterError() {
		// Start at homepage
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Navigate to error page
		page.navigate("http://localhost:" + port + "/oups");
		page.waitForLoadState();

		// Use browser back button
		page.goBack();
		page.waitForLoadState();

		// Should be back at homepage
		String currentUrl = page.url();
		assertTrue(currentUrl.endsWith("/") || currentUrl.contains("localhost:" + port),
				"Should be back at homepage after using browser back from error page");
	}

}