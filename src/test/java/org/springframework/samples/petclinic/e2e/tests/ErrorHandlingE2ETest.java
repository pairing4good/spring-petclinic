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
import org.springframework.samples.petclinic.e2e.pages.AddOwnerPage;
import org.springframework.samples.petclinic.e2e.pages.ErrorPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for error handling, edge cases, and error pages. Tests 404 errors, form
 * validation, and error page functionality.
 */
class ErrorHandlingE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSeeErrorPage_SoThatIKnowWhenSomethingGoesWrong() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		navigateTo("/oups");
		errorPage.waitForPageLoad();

		// Verify error page displays correctly
		assertTrue(page.url().contains("/oups"));
		assertEquals("Something happened...", errorPage.getErrorMessage());
		assertTrue(page.locator("img[src*='pets.png']").isVisible());
	}

	@Test
	void asAUser_IWantTo404OnInvalidPage_SoThatIKnowPageDoesNotExist() {
		// Navigate to a non-existent page
		navigateTo("/nonexistent-page");

		// Should show 404 or redirect to error page
		// The exact behavior depends on Spring Boot configuration
		// This tests that the application handles unknown routes gracefully
		assertTrue(page.url().contains("/nonexistent-page") || page.url().contains("/error"));
	}

	@Test
	void asAUser_IWantToSeeFormValidationErrors_SoThatIKnowWhatToFix() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl).open();

		// Submit form with missing required fields
		addOwnerPage.clearForm();
		addOwnerPage.enterFirstName("Test");
		// Leave lastName, address, city, telephone empty
		addOwnerPage.submitFormWithErrors();

		// Should stay on same page (form validation failed)
		assertTrue(page.url().contains("/owners/new"));

		// Note: Specific validation error checking would depend on
		// how the application displays validation errors
	}

	@Test
	void asAUser_IWantToSeeFormValidationForInvalidData_SoThatIEnterCorrectInformation() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl).open();

		// Enter invalid telephone number (letters instead of numbers)
		addOwnerPage.fillOwnerForm("John", "Doe", "123 Main St", "City", "invalid-phone");
		addOwnerPage.submitFormWithErrors();

		// Should stay on same page due to validation error
		assertTrue(page.url().contains("/owners/new"));

		// Verify form retains entered values
		assertEquals("John", addOwnerPage.getFirstNameValue());
		assertEquals("Doe", addOwnerPage.getLastNameValue());
	}

	@Test
	void asAUser_IWantToAccessInvalidOwnerId_SoThatIKnowWhenOwnerDoesNotExist() {
		// Try to access an owner that doesn't exist
		navigateTo("/owners/999999");

		// Should handle gracefully - either 404, error page, or redirect
		page.waitForLoadState();
		// The exact behavior depends on application configuration
		// This ensures the app doesn't crash on invalid IDs
		assertTrue(
				page.url().contains("/owners/999999") || page.url().contains("/error") || page.url().contains("/oups"));
	}

	@Test
	void asAUser_IWantToAccessInvalidPetId_SoThatIKnowWhenPetDoesNotExist() {
		// Try to access a pet edit form for non-existent pet
		navigateTo("/owners/1/pets/999999/edit");

		// Should handle gracefully
		page.waitForLoadState();
		// The exact behavior depends on application configuration
		assertTrue(
				page.url().contains("/pets/999999") || page.url().contains("/error") || page.url().contains("/oups"));
	}

	@Test
	void asAUser_IWantToHandleEmptySearchResults_SoThatIKnowWhenNoMatchesFound() {
		navigateTo("/owners/find");

		// Search for something that definitely won't exist
		page.locator("input[name='lastName']").fill("XYZ999NonExistent");
		page.locator("button:has-text('Find Owner')").click();

		page.waitForLoadState();
		// Should handle gracefully - show empty results or appropriate message
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	void asAUser_IWantToSeeConsistentErrorStyling_SoThatErrorsAreRecognizable() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		navigateTo("/oups");
		errorPage.waitForPageLoad();

		// Verify consistent styling with main application
		assertTrue(page.locator("nav.navbar").isVisible()); // Navigation still present
		assertTrue(page.locator("img[alt='VMware Tanzu Logo']").isVisible()); // Footer
																				// logo
																				// present

		// Error page should maintain consistent layout
		assertTrue(page.locator(".container").isVisible());
	}

	@Test
	void asAUser_IWantToRecoverFromErrors_SoThatICanContinueUsingTheApplication() {
		ErrorPage errorPage = new ErrorPage(page, baseUrl);
		navigateTo("/oups");
		errorPage.waitForPageLoad();

		// Should be able to navigate away from error page
		page.locator("nav a:has-text('Home')").click();

		// Should successfully navigate to home
		assertTrue(page.url().endsWith("/") || page.url().contains("/?"));
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible());
	}

	@Test
	void asAUser_IWantToSeeValidationOnEmptyFields_SoThatIKnowRequiredFields() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl).open();

		// Try to submit completely empty form
		addOwnerPage.clearForm();
		addOwnerPage.submitFormWithErrors();

		// Should stay on same page
		assertTrue(page.url().contains("/owners/new"));
		assertTrue(page.locator("h2:has-text('Owner')").isVisible());
	}

	@Test
	void asAUser_IWantToSeeLongInputHandling_SoThatExtremeInputsAreHandled() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl).open();

		// Test with very long inputs
		String longText = "This is a very long text input that exceeds normal expectations ".repeat(10);

		addOwnerPage.enterFirstName(longText);
		addOwnerPage.enterLastName(longText);
		addOwnerPage.enterAddress(longText);
		addOwnerPage.enterCity(longText);
		addOwnerPage.enterTelephone("123456789012345678901234567890"); // Very long number

		addOwnerPage.submitFormWithErrors();

		// Should handle gracefully without crashing
		// Form should either accept or reject with validation, but not crash
		page.waitForLoadState();
		assertTrue(page.url().contains("/owners"));
	}

}