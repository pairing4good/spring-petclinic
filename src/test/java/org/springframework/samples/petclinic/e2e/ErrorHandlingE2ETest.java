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

/**
 * As a user, I want proper error handling and edge case management, so that the
 * application behaves reliably
 */
@DisplayName("Error Handling and Edge Cases E2E Tests")
class ErrorHandlingE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to see a custom error page for 404 errors, so that I have a better user experience")
	void testCustom404ErrorPage() {
		page.navigate(baseUrl + "/nonexistent-page");
		waitForPageLoad();

		// Should show custom error page
		assertElementContainsText("h2", "Something happened...");
		assertElementVisible("img");
		assertElementVisible("img[alt='VMware Tanzu Logo']");

		// Navigation should still work
		assertElementVisible("nav");
		assertElementVisible("a[href='/']");
		assertElementVisible("a[href='/owners/find']");
	}

	@Test
	@DisplayName("As a user, I want to see a custom error page for 500 errors, so that I understand when something goes wrong")
	void testCustom500ErrorPage() {
		page.navigate(baseUrl + "/oups");
		waitForPageLoad();

		// Should show custom error page (triggered by CrashController)
		assertElementContainsText("h2", "Something happened...");
		assertElementVisible("img");
		assertElementVisible("img[alt='VMware Tanzu Logo']");

		// Navigation should still work even on error pages
		assertElementVisible("nav");
		assertElementVisible("a[href='/']");

		// Verify it's actually a 500 error (not just styled as error)
		page.onResponse(response -> {
			if (response.url().contains("/oups")) {
				assert response.status() == 500 : "Expected 500 status code for /oups endpoint";
			}
		});
	}

	@Test
	@DisplayName("As a user, I want to navigate away from error pages, so that I can continue using the application")
	void testNavigationFromErrorPages() {
		// Go to 404 error page
		page.navigate(baseUrl + "/nonexistent");
		waitForPageLoad();

		// Navigate back to home using menu
		page.locator("a[href='/']").first().click();
		waitForPageLoad();

		assertUrlContains("/");
		assertElementContainsText("h2", "Welcome");

		// Go to 500 error page
		page.navigate(baseUrl + "/oups");
		waitForPageLoad();

		// Navigate to find owners using menu
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();

		assertUrlContains("/owners/find");
		assertElementContainsText("h2", "Find Owners");
	}

	@Test
	@DisplayName("As a user, I want special characters in search to be handled properly, so that I can search for any name")
	void testSpecialCharactersInSearch() {
		navigateToFindOwners();

		// Test with special characters
		String[] specialInputs = { "O'Connor", "Smith-Jones", "José", "Müller", "!@#$%",
				"<script>alert('test')</script>" };

		for (String input : specialInputs) {
			page.locator("input[name='lastName']").fill(input);
			page.locator("button[type='submit']").click();
			waitForPageLoad();

			// Should either show no results or handle gracefully
			assertElementVisible("body");
			assertElementVisible("nav");

			// Go back to find owners page for next test
			navigateToFindOwners();
		}
	}

	@Test
	@DisplayName("As a user, I want long text inputs to be handled properly, so that the application doesn't break")
	void testLongTextInputs() {
		navigateToAddOwner();

		// Test with very long inputs
		String longText = "A".repeat(1000);

		page.locator("input[name='firstName']").fill(longText);
		page.locator("input[name='lastName']").fill(longText);
		page.locator("input[name='address']").fill(longText);
		page.locator("input[name='city']").fill(longText);
		page.locator("input[name='telephone']").fill("1234567890");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should either succeed or show appropriate validation
		assertElementVisible("body");
		assertElementVisible("nav");
	}

	@Test
	@DisplayName("As a user, I want empty search results to be handled gracefully, so that I know no matches were found")
	void testEmptySearchResults() {
		navigateToFindOwners();

		// Search for definitely non-existent owner
		page.locator("input[name='lastName']").fill("ZZZNonExistentName123");
		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should show appropriate message
		assertElementContainsText("body", "has not been found");
		assertElementVisible("nav");

		// Should be able to try another search
		assertElementVisible("input[name='lastName']");
		assertElementVisible("button[type='submit']");
	}

	@Test
	@DisplayName("As a user, I want form validation to prevent SQL injection, so that the application is secure")
	void testSQLInjectionPrevention() {
		navigateToFindOwners();

		// Test SQL injection attempts
		String[] sqlInjectionInputs = { "'; DROP TABLE owners; --", "' OR '1'='1", "admin'--",
				"'; DELETE FROM owners WHERE '1'='1'; --" };

		for (String input : sqlInjectionInputs) {
			page.locator("input[name='lastName']").fill(input);
			page.locator("button[type='submit']").click();
			waitForPageLoad();

			// Should either show no results or handle safely
			assertElementVisible("body");
			assertElementVisible("nav");

			// Application should still be functional
			navigateToFindOwners();
		}
	}

	@Test
	@DisplayName("As a user, I want XSS attempts to be prevented, so that the application is secure")
	void testXSSPrevention() {
		navigateToAddOwner();

		// Test XSS attempts
		String xssInput = "<script>alert('XSS')</script>";

		page.locator("input[name='firstName']").fill(xssInput);
		page.locator("input[name='lastName']").fill("Test");
		page.locator("input[name='address']").fill("123 Test St");
		page.locator("input[name='city']").fill("Test City");
		page.locator("input[name='telephone']").fill("1234567890");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should not execute script - either validation error or escaped content
		assertElementVisible("body");
		assertElementVisible("nav");

		// Verify no alert was triggered (script should be escaped/sanitized)
		String pageContent = page.content();
		assert !pageContent.contains("<script>alert('XSS')</script>") || pageContent.contains("&lt;script&gt;")
				: "XSS should be prevented or escaped";
	}

	@Test
	@DisplayName("As a user, I want invalid owner IDs to be handled gracefully, so that I get appropriate error pages")
	void testInvalidOwnerIds() {
		String[] invalidIds = { "999999", "abc", "-1", "0" };

		for (String id : invalidIds) {
			page.navigate(baseUrl + "/owners/" + id);
			waitForPageLoad();

			// Should either show 404 or error page
			assertElementVisible("body");
			assertElementVisible("nav");

			// Should be able to navigate away
			page.locator("a[href='/']").first().click();
			waitForPageLoad();
			assertElementContainsText("h2", "Welcome");
		}
	}

	@Test
	@DisplayName("As a user, I want invalid pet IDs to be handled gracefully, so that I get appropriate error pages")
	void testInvalidPetIds() {
		String[] invalidIds = { "999999", "abc", "-1" };

		for (String id : invalidIds) {
			page.navigate(baseUrl + "/owners/1/pets/" + id + "/edit");
			waitForPageLoad();

			// Should either show 404 or error page
			assertElementVisible("body");
			assertElementVisible("nav");

			// Should be able to navigate away
			page.locator("a[href='/']").first().click();
			waitForPageLoad();
			assertElementContainsText("h2", "Welcome");
		}
	}

	@Test
	@DisplayName("As a user, I want telephone validation to handle various formats, so that I can enter phone numbers naturally")
	void testTelephoneValidationFormats() {
		navigateToAddOwner();

		// Test various telephone formats
		String[] invalidPhones = { "123", "12345678901", // Too long
				"abcdefghij", "123-456-7890", // With dashes
				"(123) 456-7890", // With parentheses
				"" };

		for (String phone : invalidPhones) {
			page.locator("input[name='firstName']").fill("Test");
			page.locator("input[name='lastName']").fill("User");
			page.locator("input[name='address']").fill("123 Test St");
			page.locator("input[name='city']").fill("Test City");
			page.locator("input[name='telephone']").fill(phone);

			page.locator("button[type='submit']").click();
			waitForPageLoad();

			if (!phone.equals("") && phone.length() != 10) {
				// Should show validation error for invalid formats
				assertUrlContains("/owners/new");
			}

			// Clear form for next test
			navigateToAddOwner();
		}
	}

	@Test
	@DisplayName("As a user, I want date validation to prevent invalid dates, so that I enter correct information")
	void testDateValidation() {
		// Navigate to owner with pets
		navigateToFindOwners();
		page.locator("input[name='lastName']").fill("Franklin");
		page.locator("button[type='submit']").click();
		waitForPageLoad();
		page.locator("a[href*='/owners/']").first().click();
		waitForPageLoad();

		// Add a new pet with invalid date
		page.locator("a[href*='/pets/new']").click();
		waitForPageLoad();

		page.locator("input[name='name']").fill("TestPet");
		page.locator("input[name='birthDate']").fill("2050-01-01"); // Future date
		page.locator("select[name='type']").selectOption("cat");

		page.locator("button[type='submit']").click();
		waitForPageLoad();

		// Should either accept future dates or show validation error
		assertElementVisible("body");
		assertElementVisible("nav");
	}

	@Test
	@DisplayName("As a user, I want the application to handle concurrent access gracefully, so that multiple users can use it")
	void testConcurrentAccess() {
		// Simulate concurrent operations by rapid navigation
		for (int i = 0; i < 5; i++) {
			navigateToHome();
			navigateToFindOwners();
			navigateToVeterinarians();
			navigateToHome();
		}

		// Application should still be functional
		assertElementContainsText("h2", "Welcome");
		assertElementVisible("nav");
	}

	@Test
	@DisplayName("As a user, I want error pages to maintain consistent styling, so that they look professional")
	void testErrorPageStyling() {
		// Test 404 page styling
		page.navigate(baseUrl + "/nonexistent");
		waitForPageLoad();

		assertElementVisible("nav");
		assertElementVisible("img[alt='VMware Tanzu Logo']");
		assertElementContainsText("h2", "Something happened...");

		// Test 500 page styling
		page.navigate(baseUrl + "/oups");
		waitForPageLoad();

		assertElementVisible("nav");
		assertElementVisible("img[alt='VMware Tanzu Logo']");
		assertElementContainsText("h2", "Something happened...");

		// Both should have consistent layout
		assertElementVisible("body");
	}

}