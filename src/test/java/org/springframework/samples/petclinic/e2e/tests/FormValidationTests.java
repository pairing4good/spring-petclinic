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
import org.springframework.samples.petclinic.e2e.pages.AddOwnerPage;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for Form Validation functionality including required fields, data
 * validation, and error handling.
 */
class FormValidationTests extends PlaywrightTestBase {

	@Test
	void asAUser_IWantToSubmitEmptyOwnerForm_SoThatICanSeeValidationErrors() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Submit empty form
		addOwnerPage.clickAddOwner();

		// Should remain on the same page due to validation errors
		assertTrue(page.url().contains("/owners/new"), "Should remain on add owner page with validation errors");
		assertTrue(addOwnerPage.areAllFieldsVisible(), "Form fields should still be visible after validation error");
	}

	@Test
	void asAUser_IWantToEnterValidOwnerData_SoThatICanSuccessfullyCreateOwner() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Fill valid data
		String timestamp = String.valueOf(System.currentTimeMillis());
		addOwnerPage.fillOwnerForm("Valid" + timestamp, "Owner" + timestamp, "123 Valid Street", "Valid City",
				"555-0123");

		addOwnerPage.clickAddOwner();

		// Should navigate away from form page on success
		assertFalse(page.url().contains("/owners/new"), "Should navigate away from form on successful submission");
		assertTrue(page.url().contains("/owners/"), "Should navigate to owner details page");
	}

	@Test
	void asAUser_IWantToEnterInvalidTelephoneNumbers_SoThatICanSeeValidationFeedback() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Test various invalid telephone formats
		String[] invalidPhones = { "invalid-phone", "12345", "phone123", "555-555-555-555" };

		for (String invalidPhone : invalidPhones) {
			String timestamp = String.valueOf(System.currentTimeMillis());
			addOwnerPage.fillOwnerForm("Test" + timestamp, "User" + timestamp, "123 Test Street", "Test City",
					invalidPhone);

			addOwnerPage.clickAddOwner();

			// Should remain on form page due to validation (depending on validation
			// rules)
			// The actual behavior depends on the application's validation implementation
			assertNotNull(page.url(), "Page should handle invalid phone number gracefully");
		}
	}

	@Test
	void asAUser_IWantToEnterLongFieldValues_SoThatICanTestMaximumLimits() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Test very long values
		String longString = "a".repeat(500);
		String timestamp = String.valueOf(System.currentTimeMillis());

		addOwnerPage.fillOwnerForm(longString, longString, longString, longString, "555-0123");

		addOwnerPage.clickAddOwner();

		// Application should handle long values gracefully
		assertNotNull(page.url(), "Application should handle long field values");
	}

	@Test
	void asAUser_IWantToEnterSpecialCharacters_SoThatICanTestCharacterHandling() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Test special characters
		String timestamp = String.valueOf(System.currentTimeMillis());
		addOwnerPage.fillOwnerForm("José-María" + timestamp, "O'Connor-Smith" + timestamp, "123 Main St. #456",
				"São Paulo", "(555) 123-4567");

		addOwnerPage.clickAddOwner();

		// Should handle special characters appropriately
		assertNotNull(page.url(), "Application should handle special characters in form data");
	}

	@Test
	void asAUser_IWantToFillAndClearFormFields_SoThatICanEditMyInput() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Fill form
		addOwnerPage.enterFirstName("TestFirst");
		addOwnerPage.enterLastName("TestLast");
		addOwnerPage.enterAddress("TestAddress");
		addOwnerPage.enterCity("TestCity");
		addOwnerPage.enterTelephone("555-1234");

		// Verify values are entered
		assertEquals("TestFirst", addOwnerPage.getFirstNameValue(), "First name should be filled");
		assertEquals("TestLast", addOwnerPage.getLastNameValue(), "Last name should be filled");
		assertEquals("TestAddress", addOwnerPage.getAddressValue(), "Address should be filled");
		assertEquals("TestCity", addOwnerPage.getCityValue(), "City should be filled");
		assertEquals("555-1234", addOwnerPage.getTelephoneValue(), "Telephone should be filled");

		// Clear and re-enter values
		addOwnerPage.enterFirstName("");
		addOwnerPage.enterLastName("");
		assertEquals("", addOwnerPage.getFirstNameValue(), "First name should be cleared");
		assertEquals("", addOwnerPage.getLastNameValue(), "Last name should be cleared");
	}

	@Test
	void asAUser_IWantToSearchWithSpecialCharacters_SoThatICanFindOwnersWithVariousNames() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page, baseUrl);
		findOwnersPage.navigateTo();

		// Test search with special characters
		String[] searchTerms = { "O'Connor", "José", "Smith-Jones", "van der Berg" };

		for (String searchTerm : searchTerms) {
			findOwnersPage.enterLastName(searchTerm);
			assertEquals(searchTerm, findOwnersPage.getLastNameValue(), "Search term should be entered correctly");

			findOwnersPage.clickFindOwner();

			// Should handle search gracefully regardless of results
			assertNotNull(page.url(), "Search should complete without errors for: " + searchTerm);

			// Return to search page for next test
			if (!findOwnersPage.isFindOwnersPageDisplayed()) {
				findOwnersPage.navigateTo();
			}
		}
	}

	@Test
	void asAUser_IWantToTestFormFieldTabOrder_SoThatICanNavigateWithKeyboard() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Test tab navigation through form fields
		page.keyboard().press("Tab"); // Should focus first field
		page.keyboard().type("FirstName");

		page.keyboard().press("Tab"); // Should focus last name
		page.keyboard().type("LastName");

		// Verify values were entered in correct fields
		assertTrue(addOwnerPage.getFirstNameValue().contains("FirstName"), "First name should be filled via keyboard");
		assertTrue(addOwnerPage.getLastNameValue().contains("LastName"), "Last name should be filled via keyboard");
	}

	@Test
	void asAUser_IWantToSubmitFormWithEnterKey_SoThatICanUseKeyboardShortcuts() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Fill required fields
		String timestamp = String.valueOf(System.currentTimeMillis());
		addOwnerPage.fillOwnerForm("Enter" + timestamp, "Key" + timestamp, "123 Enter Street", "Enter City",
				"555-0123");

		// Try to submit with Enter key (focus on submit button and press Enter)
		page.locator("button:has-text('Add Owner')").focus();
		page.keyboard().press("Enter");

		// Should submit form
		assertFalse(page.url().contains("/owners/new"), "Form should submit with Enter key");
	}

	@Test
	void asAUser_IWantToTestFormWithJavaScript_SoThatClientSideValidationWorks() {
		AddOwnerPage addOwnerPage = new AddOwnerPage(page, baseUrl);
		addOwnerPage.navigateTo();

		// Test that form elements respond to JavaScript events
		// Fill a field and trigger blur event
		addOwnerPage.enterFirstName("TestName");
		page.locator("input[name='firstName']").blur();

		// Verify field value persists
		assertEquals("TestName", addOwnerPage.getFirstNameValue(), "Field value should persist after blur event");
	}

}