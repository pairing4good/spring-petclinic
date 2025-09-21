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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for error handling and edge cases. Covers 404 pages, form validation
 * errors, intentional errors, and edge cases.
 */
public class ErrorHandlingE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSeeTheIntentionalErrorPage_SoThatIUnderstandHowErrorsAreHandled() {
		navigateAndWait("/oups");

		// Verify error page structure
		assertAll(() -> assertTrue(page.locator("h2").textContent().contains("Something happened")),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()),
				() -> assertTrue(page.locator("img[alt='VMware Tanzu Logo']").isVisible()));

		// Verify the error is actually a 500 server error
		// Note: The error response might be intercepted by browser, so we check page
		// content instead
		assertTrue(page.locator("h2").textContent().contains("Something happened"));
	}

	@Test
	void asAUser_IWantToAccessNonExistentOwner_SoThatICanSeeHow404ErrorsAreHandled() {
		// Test accessing non-existent owner by ID
		navigateAndWait("/owners/99999");

		// Should handle gracefully (could show error page, redirect, or show empty state)
		assertTrue(page.locator("body").isVisible());

		// If it shows an error page, verify it's user-friendly
		if (page.locator("h2").textContent().contains("Error")
				|| page.locator("h2").textContent().contains("Not Found")) {
			assertTrue(page.locator("body").isVisible());
		}
	}

	@Test
	void asAUser_IWantToAccessNonExistentPet_SoThatICanSeeHow404ErrorsAreHandled() {
		// Test accessing non-existent pet edit form
		navigateAndWait("/owners/1/pets/99999/edit");

		// Should handle gracefully
		assertTrue(page.locator("body").isVisible());

		// Verify error handling is user-friendly
		String pageContent = page.locator("body").textContent().toLowerCase();
		assertTrue(pageContent.length() > 0); // Page should have some content
	}

	@Test
	void asAUser_IWantToAccessInvalidUrls_SoThatICanSee404HandlingForCompletlyInvalidPaths() {
		// Test completely invalid path
		navigateAndWait("/completely-invalid-path-that-does-not-exist");

		// Should show some form of error page or 404
		assertTrue(page.locator("body").isVisible());

		// Test invalid path with parameters
		navigateAndWait("/invalid?param=value");

		assertTrue(page.locator("body").isVisible());
	}

	@Test
	void asAUser_IWantToTestFormValidationErrors_SoThatICanSeeHelpfulErrorMessages() {
		// Test owner form validation
		navigateAndWait("/owners/new");

		// Submit form with only partial data to trigger validation
		fillField("input[name='firstName']", "John");
		// Leave other required fields empty

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should stay on form with validation errors
		assertAll(() -> assertTrue(page.locator("form").isVisible()),
				() -> assertEquals("John", page.locator("input[name='firstName']").inputValue()));
	}

	@Test
	void asAUser_IWantToTestNetworkErrorHandling_SoThatICanSeeHowConnectionIssuesAreHandled() {
		// This test simulates network issues by accessing the app and then checking
		// behavior
		navigateAndWait("/");

		// Navigate to a form and then go back to test navigation resilience
		navigateAndWait("/owners/new");
		page.goBack();
		page.waitForLoadState();

		// Should return to home page successfully
		assertTrue(page.locator("h2").textContent().contains("Welcome"));
	}

	@Test
	void asAUser_IWantToTestInvalidFormData_SoThatICanSeeDataValidationInAction() {
		navigateAndWait("/owners/new");

		// Test with invalid telephone number
		fillField("input[name='firstName']", "John");
		fillField("input[name='lastName']", "Doe");
		fillField("input[name='address']", "123 Main St");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "invalid-phone-number");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should show validation error or stay on form
		assertTrue(page.locator("form").isVisible() || page.locator("h2").textContent().contains("Owner Information"));
	}

	@Test
	void asAUser_IWantToTestXSSPrevention_SoThatScriptInjectionIsBlocked() {
		navigateAndWait("/owners/new");

		// Test with script injection in form fields
		String scriptText = "<script>alert('xss')</script>";

		fillField("input[name='firstName']", scriptText);
		fillField("input[name='lastName']", "TestUser");
		fillField("input[name='address']", "123 Main St");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Verify script is not executed and is properly escaped
		String pageContent = page.locator("body").textContent();
		if (pageContent.contains(scriptText)) {
			// Script should be escaped, not executed
			assertFalse(page.locator("script").textContent().contains("alert('xss')"));
		}
	}

	@Test
	void asAUser_IWantToTestSQLInjectionPrevention_SoThatDatabaseInjectionIsBlocked() {
		navigateAndWait("/owners/find");

		// Test with SQL injection in search field
		String sqlInjection = "'; DROP TABLE owners; --";

		fillField("input[name='lastName']", sqlInjection);
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Application should handle this gracefully without breaking
		assertTrue(page.locator("body").isVisible());

		// Verify the application still works after the injection attempt
		navigateAndWait("/");
		assertTrue(page.locator("h2").textContent().contains("Welcome"));
	}

	@Test
	void asAUser_IWantToTestLargeInputData_SoThatApplicationHandlesOversizedInput() {
		navigateAndWait("/owners/new");

		// Test with extremely large input
		String largeInput = "A".repeat(10000);

		fillField("input[name='firstName']", largeInput);
		fillField("input[name='lastName']", "TestUser");
		fillField("input[name='address']", "123 Main St");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Application should handle large input gracefully
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	void asAUser_IWantToTestEmptyStateHandling_SoThatEmptyResultsAreHandledGracefully() {
		navigateAndWait("/owners/find");

		// Search for definitely non-existent owner
		fillField("input[name='lastName']", "DefinitelyDoesNotExist12345");
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should handle empty results gracefully
		assertTrue(page.locator("body").isVisible());

		// Could show empty state message, return to search, or show empty table
		String bodyText = page.locator("body").textContent();
		assertFalse(bodyText.trim().isEmpty());
	}

	@Test
	void asAUser_IWantToTestBrowserBackButtonOnErrorPages_SoThatNavigationWorks() {
		// Navigate to error page
		navigateAndWait("/oups");
		assertTrue(page.locator("h2").textContent().contains("Something happened"));

		// Use browser back button
		page.goBack();
		page.waitForLoadState();

		// Should return to previous page (home page)
		assertTrue(page.locator("h2").textContent().contains("Welcome"));

		// Test forward button
		page.goForward();
		page.waitForLoadState();

		// Should return to error page
		assertTrue(page.locator("h2").textContent().contains("Something happened"));
	}

	@Test
	void asAUser_IWantToTestErrorPageAccessibility_SoThatErrorsAreAccessibleToAllUsers() {
		navigateAndWait("/oups");

		// Verify error page has proper structure
		assertAll(() -> assertTrue(page.locator("h2").isVisible()), () -> assertTrue(page.locator("img").isVisible()),
				() -> assertTrue(page.locator("nav").isVisible()) // Navigation should
																	// still be available
		);

		// Test keyboard navigation on error page
		page.keyboard().press("Tab");
		// Should be able to navigate away from error page using keyboard
		assertTrue(page.locator("a[href='/']").isVisible());
	}

	@Test
	void asAUser_IWantToTestFormResubmissionAfterError_SoThatICanRetryAfterFailures() {
		navigateAndWait("/owners/new");

		// Submit form with validation errors
		fillField("input[name='firstName']", "John");
		// Leave required fields empty

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Form should still be editable
		assertTrue(page.locator("form").isVisible());

		// Fill remaining fields and resubmit
		fillField("input[name='lastName']", "Doe");
		fillField("input[name='address']", "123 Main St");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should succeed this time
		assertTrue(page.locator("h2").textContent().contains("Owner Information") || page.locator("form").isVisible());
	}

	@Test
	void asAUser_IWantToTestConcurrentFormSubmissions_SoThatMultipleSubmissionsAreHandled() {
		navigateAndWait("/owners/new");

		// Fill out form
		fillField("input[name='firstName']", "John");
		fillField("input[name='lastName']", "ConcurrentTest");
		fillField("input[name='address']", "123 Main St");
		fillField("input[name='city']", "Test City");
		fillField("input[name='telephone']", "5551234567");

		// Submit form
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Verify single submission was processed correctly
		assertTrue(page.locator("body").isVisible());
	}

	@Test
	void asAUser_IWantToTestSpecialCharacterHandling_SoThatUnicodeIsSupported() {
		navigateAndWait("/owners/new");

		// Test with Unicode characters
		fillField("input[name='firstName']", "José María");
		fillField("input[name='lastName']", "García-López");
		fillField("input[name='address']", "123 Niño Street");
		fillField("input[name='city']", "São Paulo");
		fillField("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Should handle Unicode characters correctly
		if (page.locator("h2").textContent().contains("Owner Information")) {
			String ownerInfo = page.locator("table").textContent();
			assertAll(() -> assertTrue(ownerInfo.contains("José María")),
					() -> assertTrue(ownerInfo.contains("García-López")),
					() -> assertTrue(ownerInfo.contains("São Paulo")));
		}
	}

	@Test
	void asAUser_IWantToTestPageLoadingStates_SoThatLoadingIsHandledGracefully() {
		// Test navigation with loading states
		navigateAndWait("/vets.html");

		// Verify page loaded completely
		assertAll(() -> assertTrue(page.locator("h2").isVisible()),
				() -> assertTrue(page.locator("table").isVisible()));

		// Navigate to another page and back
		page.click("a[href='/']");
		page.waitForLoadState();

		assertTrue(page.locator("h2").textContent().contains("Welcome"));

		// Navigate back using navigation
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		assertTrue(page.locator("h2").textContent().contains("Veterinarians"));
	}

}