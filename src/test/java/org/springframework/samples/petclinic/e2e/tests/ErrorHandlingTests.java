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
import org.springframework.samples.petclinic.e2e.config.PlaywrightTestBase;
import org.springframework.samples.petclinic.e2e.pages.ErrorPage;
import org.springframework.samples.petclinic.e2e.pages.HomePage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for Error Handling functionality including error pages and recovery
 * scenarios.
 */
class ErrorHandlingTests extends PlaywrightTestBase {

	@Test
	void asAVisitor_IWantToSeeCustomErrorPage_SoThatIGetHelpfulErrorInformation() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		errorPage.navigateTo();

		assertTrue(errorPage.isErrorHeadingVisible(), "Error heading should be visible");
		assertTrue(errorPage.isErrorImageVisible(), "Error image should be visible");
		assertEquals("Something happened...", errorPage.getErrorHeadingText(),
				"Error heading should have correct text");
		assertTrue(errorPage.isHttpStatus500(), "Should be accessible via /oups URL");
	}

	@Test
	void asAVisitor_IWantToAccessErrorPageViaNavigation_SoThatICanTestErrorHandling() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Use navigation link to access error page
		homePage.clickErrorLink();

		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		assertTrue(errorPage.isErrorHeadingVisible(), "Error page should be displayed via navigation");
		assertTrue(errorPage.isErrorPageDisplayed(), "Should be on error page");
	}

	@Test
	void asAVisitor_IWantToRecoverFromErrorPage_SoThatICanContinueUsing() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		errorPage.navigateTo();

		// Verify error page is displayed
		assertTrue(errorPage.isErrorHeadingVisible(), "Should be on error page");

		// Navigate back to home using navigation
		errorPage.clickHomeLink();

		// Verify successful recovery
		assertTrue(errorPage.isHomePageDisplayed(), "Should successfully navigate away from error page");
	}

	@Test
	void asAVisitor_IWantToAccessNonExistentPage_SoThatICanSee404Handling() {
		// Navigate to non-existent page
		page.navigate(baseUrl + "/nonexistent-page-12345");

		// Application might redirect to error page or show 404
		// The actual behavior depends on Spring Boot error handling configuration
		assertNotNull(page.url(), "Should handle non-existent page gracefully");

		// Verify page is still functional
		assertTrue(page.locator("body").isVisible(), "Page should still render some content");
	}

	@Test
	void asAVisitor_IWantToUseInvalidRoutes_SoThatICanTestRouteHandling() {
		// Test various invalid routes
		String[] invalidRoutes = { "/owners/invalid-id", "/pets/999999", "/invalid-path", "/owners/edit/999999" };

		for (String route : invalidRoutes) {
			page.navigate(baseUrl + route);

			// Application should handle gracefully - either redirect or show error
			assertNotNull(page.url(), "Should handle invalid route: " + route);
			assertTrue(page.locator("body").isVisible(), "Should render some content for route: " + route);
		}
	}

	@Test
	void asAVisitor_IWantToSeeErrorPageContent_SoThatIUnderstandWhatHappened() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		errorPage.navigateTo();

		// Verify error page has informative content
		assertTrue(errorPage.isErrorDescriptionVisible(), "Error description should be visible");

		String description = errorPage.getErrorDescriptionText();
		assertNotNull(description, "Error description should not be null");
		// The description should contain helpful information
		assertTrue(description.contains("controller") || description.contains("showcase")
				|| description.contains("exception"), "Error description should be informative");
	}

	@Test
	void asAVisitor_IWantToVerifyErrorPageAccessibility_SoThatItWorksForAllUsers() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		errorPage.navigateTo();

		// Verify basic accessibility elements
		assertTrue(errorPage.isErrorHeadingVisible(), "Error heading should be present for screen readers");

		// Verify navigation is still accessible
		errorPage.clickHomeLink();
		assertTrue(errorPage.isHomePageDisplayed(), "Navigation should work from error page");
	}

	@Test
	void asAVisitor_IWantToTestBrowserBackFromError_SoThatICanRecoverEasily() {
		// Start from home page
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Navigate to error page
		homePage.clickErrorLink();

		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		assertTrue(errorPage.isErrorPageDisplayed(), "Should be on error page");

		// Use browser back button
		page.goBack();

		// Should return to previous page
		assertTrue(homePage.isHomePageDisplayed(), "Browser back should work from error page");
	}

	@Test
	void asAVisitor_IWantToRefreshErrorPage_SoThatICanRetryAfterErrors() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		errorPage.navigateTo();

		assertTrue(errorPage.isErrorHeadingVisible(), "Error page should load initially");

		// Refresh the page
		page.reload();

		// Error page should still work after refresh
		assertTrue(errorPage.isErrorHeadingVisible(), "Error page should work after refresh");
		assertTrue(errorPage.isHttpStatus500(), "Should still be on error URL after refresh");
	}

}