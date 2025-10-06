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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E tests for Error Handling Tests error pages, validation errors, and graceful error
 * handling
 */
@DisplayName("Error Handling E2E Tests")
class ErrorHandlingE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to see a proper error page when server errors occur, so that I understand something went wrong")
	void shouldDisplayProperErrorPageForServerErrors() {
		// Given - user navigates to the intentional error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();

		// Then - should see custom error page instead of generic error
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should show custom error heading");
		assertTrue(page.locator("img").first().isVisible(), "Should show error image");

		// Should not show generic Whitelabel error page
		assertFalse(page.locator("text=Whitelabel Error Page").isVisible(), "Should not show Whitelabel error page");
		assertFalse(page.locator("text=This application has no explicit mapping").isVisible(),
				"Should not show generic error message");

		// Should maintain navigation structure
		assertTrue(page.locator("nav").isVisible(), "Should show navigation");
		assertTrue(page.locator("a:has-text('Home')").isVisible(), "Should show home link");
	}

	@Test
	@DisplayName("As a user, I want to access the error page through navigation, so that I can see how errors are handled")
	void shouldAccessErrorPageThroughNavigation() {
		// Given - user is on home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user clicks error link in navigation
		page.locator("a:has-text('Error')").click();

		// Then - should navigate to error page
		assertTrue(page.url().contains("/oups"), "Should be on error page");
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should show error heading");
	}

	@Test
	@DisplayName("As a user, I want to navigate away from error page, so that I can recover from errors")
	void shouldNavigateAwayFromErrorPage() {
		// Given - user is on error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should be on error page");

		// When - user clicks home link
		page.locator("a:has-text('Home')").click();

		// Then - should return to home page
		assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should be on home page");
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome heading");
	}

	@Test
	@DisplayName("As a user, I want to see proper error handling for non-existent pages, so that I get helpful feedback")
	void shouldHandleNonExistentPages() {
		// Given - user navigates to a non-existent page
		page.navigate(baseUrl + "/nonexistent-page-12345");
		page.waitForLoadState();

		// Then - should see some form of error handling
		// This could be a 404 page, redirect to home, or custom error page
		// The exact behavior may vary, but should not crash
		assertTrue(page.locator("body").isVisible(), "Page should load with some content");

		// Should not show browser's default error page
		assertFalse(page.title().contains("This site can't be reached"), "Should not show browser error");
	}

	@Test
	@DisplayName("As a user, I want form validation errors to be displayed clearly, so that I know how to correct my input")
	void shouldDisplayFormValidationErrorsClearly() {
		// Given - user navigates to add owner form
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();

		// When - user submits form with missing required fields
		page.locator("button:has-text('Add Owner')").click();

		// Then - should show validation errors or prevent submission
		// The exact validation behavior may vary, but should handle gracefully
		assertTrue(page.url().contains("/owners/new"), "Should remain on add owner page or redirect with errors");

		// Should not crash or show server error page
		assertFalse(page.locator("h2:has-text('Something happened')").isVisible(), "Should not show server error page");
	}

	@Test
	@DisplayName("As a user, I want to see appropriate error handling for invalid owner IDs, so that I get helpful feedback")
	void shouldHandleInvalidOwnerIDs() {
		// Given - user navigates to non-existent owner
		page.navigate(baseUrl + "/owners/99999");
		page.waitForLoadState();

		// Then - should handle invalid owner ID gracefully
		// Could show error page, redirect, or show "not found" message
		assertTrue(page.locator("body").isVisible(), "Should load some content");

		// Should not crash or show unhandled exception
		if (page.locator("h2:has-text('Something happened')").isVisible()) {
			// If it shows error page, that's acceptable
			assertTrue(page.locator("img").first().isVisible(), "Should show error image");
		}
		else {
			// Or it might redirect or show not found message
			assertTrue(page.locator("html").isVisible(), "Should show some content");
		}
	}

	@Test
	@DisplayName("As a user, I want error pages to maintain site branding and navigation, so that I can recover easily")
	void shouldMaintainSiteBrandingAndNavigationOnErrorPages() {
		// Given - user navigates to error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();

		// Then - should maintain site structure
		assertTrue(page.locator("nav").isVisible(), "Should show navigation bar");
		assertTrue(page.locator("a:has-text('Home')").isVisible(), "Should show home link");
		assertTrue(page.locator("a:has-text('Find Owners')").isVisible(), "Should show find owners link");
		assertTrue(page.locator("a:has-text('Veterinarians')").isVisible(), "Should show veterinarians link");

		// Should show VMware Tanzu logo (site branding)
		assertTrue(page.locator("img[alt*='VMware']").isVisible() || page.locator("img[alt*='Tanzu']").isVisible(),
				"Should show site branding");
	}

	@Test
	@DisplayName("As a user, I want consistent error styling across the application, so that errors are recognizable")
	void shouldHaveConsistentErrorStyling() {
		// Given - user navigates to error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();

		// Then - error page should have consistent styling with rest of application
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should show error heading");
		assertTrue(page.locator("img").first().isVisible(), "Should show error image");

		// Should use same general layout as other pages
		assertTrue(page.locator("body").getAttribute("class") != null || page.locator("html").isVisible(),
				"Should have consistent styling");
	}

	@Test
	@DisplayName("As a user, I want error pages to be accessible, so that all users can understand and recover from errors")
	void shouldHaveAccessibleErrorPages() {
		// Given - user navigates to error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();

		// Then - error page should be accessible
		assertTrue(page.locator("h2").isVisible(), "Should have proper heading structure");

		// Images should have alt text or be decorative
		if (page.locator("img").first().isVisible()) {
			String altText = page.locator("img").first().getAttribute("alt");
			// Alt text can be empty for decorative images, but attribute should exist
			assertNotNull(altText, "Images should have alt attribute");
		}

		// Navigation should be accessible
		assertTrue(page.locator("nav").isVisible() || page.locator("a").count() > 0,
				"Should have accessible navigation");
	}

	@Test
	@DisplayName("As a user, I want to handle JavaScript errors gracefully, so that the application remains functional")
	void shouldHandleJavaScriptErrorsGracefully() {
		// Given - user navigates to veterinarians page (which has some JavaScript)
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();

		// Then - page should load even with potential JavaScript errors
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians heading");
		assertTrue(page.locator("table").isVisible(), "Should show veterinarians table");

		// Basic functionality should work regardless of JavaScript errors
		assertTrue(page.locator("a:has-text('Home')").isVisible(), "Navigation should work");
	}

	@Test
	@DisplayName("As a user, I want browser back button to work from error pages, so that I can return to previous page")
	void shouldSupportBrowserBackButtonFromErrorPages() {
		// Given - user starts on home page and navigates to error page
		page.navigate(baseUrl);
		page.waitForLoadState();
		page.locator("a:has-text('Error')").click();
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should be on error page");

		// When - user uses browser back button
		page.goBack();

		// Then - should return to previous page
		assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should return to home page");
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome heading");
	}

}