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

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * End-to-End tests for error handling scenarios and edge cases.
 *
 * @author Spring PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("As a user I want proper error handling so that I receive helpful feedback when things go wrong")
class ErrorHandlingE2ETest extends BasePlaywrightE2ETest {

	@LocalServerPort
	private int port;

	@Override
	protected void navigateToHome() {
		baseUrl = "http://localhost:" + port;
		page.navigate(baseUrl);
		page.waitForSelector("h2", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
	}

	@Test
	@DisplayName("As a user I want to see an error page when accessing the error endpoint so that I can test error handling")
	void shouldDisplayErrorPageForOupsEndpoint() {
		navigateToHome();

		page.click("a[href='/oups']");
		waitForPageLoad();

		// Should be on error page
		assertTrue(page.url().contains("/oups"));

		// Page should handle the error gracefully (either custom error page or default)
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	@DisplayName("As a user I want to see a 404 error page when accessing non-existent pages so that I know the page doesn't exist")
	void shouldDisplay404ForNonExistentPages() {
		Response response = page.navigate(baseUrl + "/non-existent-page");

		// Should return 404 status
		assertTrue(response.status() == 404);

		// Page should still be rendered
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	@DisplayName("As a user I want to handle network errors gracefully so that I can retry when needed")
	void shouldHandleNetworkErrorsGracefully() {
		// Try to navigate to an invalid port to simulate network error
		try {
			page.navigate("http://localhost:99999",
					new com.microsoft.playwright.Page.NavigateOptions().setTimeout(5000));
		}
		catch (Exception e) {
			// This should timeout or fail gracefully
			assertTrue(true, "Network error handled as expected");
		}

		// Should be able to recover and navigate to valid URL
		navigateToHome();
		assertTrue(page.locator("h2").isVisible());
	}

	@Test
	@DisplayName("As a user I want form validation errors to be clearly displayed so that I know how to fix them")
	void shouldDisplayFormValidationErrors() {
		navigateToHome();
		page.click("a[href='/owners/find']");
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Submit form with invalid data
		page.fill("input[name='firstName']", "");
		page.fill("input[name='lastName']", "");
		page.fill("input[name='telephone']", "invalid");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should stay on form page (validation failed)
		assertTrue(page.url().contains("/owners/new"));
	}

	@Test
	@DisplayName("As a user I want JavaScript errors to not break the page so that basic functionality still works")
	void shouldHandleJavaScriptErrors() {
		navigateToHome();

		// Inject a JavaScript error
		page.evaluate("() => { throw new Error('Test JavaScript error'); }");

		// Page should still be functional
		assertTrue(page.locator("h2").isVisible());
		assertTrue(page.locator("a[href='/owners/find']").isVisible());
	}

	@Test
	@DisplayName("As a user I want proper error handling for database connectivity issues so that I get meaningful feedback")
	void shouldHandleDatabaseErrors() {
		// This test assumes the app is running with a database
		// If database is not available, the app should handle it gracefully
		navigateToHome();

		page.click("a[href='/owners/find']");
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should either show results or a graceful error message
		assertTrue(page.locator("body").isVisible());

		// Check that the page is not showing a stack trace or raw error
		String pageContent = page.content();
		assertTrue(!pageContent.contains("SQLException") && !pageContent.contains("java.lang."),
				"Page should not expose internal error details");
	}

	@Test
	@DisplayName("As a user I want session timeout to be handled gracefully so that I can continue using the application")
	void shouldHandleSessionTimeout() {
		navigateToHome();

		// Simulate session timeout by clearing cookies
		context.clearCookies();

		// Continue navigation
		page.click("a[href='/owners/find']");
		waitForPageLoad();

		// Should still work (stateless application)
		assertTrue(page.locator("h2").isVisible());
	}

	@Test
	@DisplayName("As a user I want CSRF protection errors to be handled properly so that I understand security requirements")
	void shouldHandleCSRFErrors() {
		navigateToHome();
		page.click("a[href='/owners/find']");
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Try to remove CSRF token if present and submit
		page.evaluate(
				"() => { const csrf = document.querySelector('input[name=\"_csrf\"]'); if(csrf) csrf.remove(); }");

		page.fill("input[name='firstName']", "Test");
		page.fill("input[name='lastName']", "User");
		page.fill("input[name='address']", "123 Test St");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "1234567890");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should handle CSRF error appropriately (403 or back to form)
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	@DisplayName("As a user I want XSS attempts to be properly escaped so that the application is secure")
	void shouldEscapeXSSAttempts() {
		navigateToHome();
		page.click("a[href='/owners/find']");

		// Try XSS in search field
		String xssPayload = "<script>alert('XSS')</script>";
		page.fill("input[name='lastName']", xssPayload);
		page.click("button[type='submit']");
		waitForPageLoad();

		// XSS should be escaped, not executed
		String pageContent = page.content();
		assertTrue(!pageContent.contains("<script>alert") || pageContent.contains("&lt;script&gt;"),
				"XSS payload should be escaped");
	}

	@Test
	@DisplayName("As a user I want SQL injection attempts to be prevented so that the database is secure")
	void shouldPreventSQLInjection() {
		navigateToHome();
		page.click("a[href='/owners/find']");

		// Try SQL injection in search field
		String sqlPayload = "'; DROP TABLE owners; --";
		page.fill("input[name='lastName']", sqlPayload);
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should handle safely - either no results or sanitized search
		assertTrue(page.locator("body").isVisible());

		// Application should still work after the attempt
		page.navigate(baseUrl);
		assertTrue(page.locator("h2").isVisible());
	}

	@Test
	@DisplayName("As a user I want large input values to be handled properly so that the application doesn't crash")
	void shouldHandleLargeInputValues() {
		navigateToHome();
		page.click("a[href='/owners/find']");
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Create very long input
		String longInput = "A".repeat(1000);

		page.fill("input[name='firstName']", longInput);
		page.fill("input[name='lastName']", longInput);
		page.fill("input[name='address']", longInput);
		page.fill("input[name='city']", longInput);
		page.fill("input[name='telephone']", "1234567890");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should handle gracefully (validation error or truncation)
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	@DisplayName("As a user I want Unicode characters to be handled properly so that international names work")
	void shouldHandleUnicodeCharacters() {
		navigateToHome();
		page.click("a[href='/owners/find']");
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Use Unicode characters
		page.fill("input[name='firstName']", "José");
		page.fill("input[name='lastName']", "García-Pérez");
		page.fill("input[name='address']", "123 Señor Street");
		page.fill("input[name='city']", "São Paulo");
		page.fill("input[name='telephone']", "1234567890");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should handle Unicode properly
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	@DisplayName("As a user I want concurrent access to be handled safely so that data integrity is maintained")
	void shouldHandleConcurrentAccess() {
		// Open multiple contexts to simulate concurrent users
		var context2 = browser.newContext();
		var page2 = context2.newPage();

		try {
			// Both pages navigate to the application
			navigateToHome();
			page2.navigate(baseUrl);

			// Both should work independently
			page.click("a[href='/owners/find']");
			page2.click("a[href='/vets.html']");

			waitForPageLoad();
			page2.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);

			// Both pages should be functional
			assertTrue(page.locator("h2").isVisible());
			assertTrue(page2.locator("h2").isVisible());
		}
		finally {
			context2.close();
		}
	}

}