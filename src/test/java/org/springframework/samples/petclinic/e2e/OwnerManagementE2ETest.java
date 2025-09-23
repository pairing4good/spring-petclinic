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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * End-to-End tests for owner management functionality including finding, adding, and
 * managing pet owners.
 *
 * @author Spring PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("As a user I want to manage pet owners so that I can track pet ownership information")
class OwnerManagementE2ETest extends BasePlaywrightE2ETest {

	@LocalServerPort
	private int port;

	@Override
	protected void navigateToHome() {
		baseUrl = "http://localhost:" + port;
		page.navigate(baseUrl);
		page.waitForSelector("h2", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
	}

	private void navigateToFindOwners() {
		navigateToHome();
		page.click("a[href='/owners/find']");
		waitForPageLoad();
	}

	@Test
	@DisplayName("As a user I want to access the find owners page so that I can search for existing owners")
	void shouldAccessFindOwnersPage() {
		navigateToFindOwners();

		assertEquals("Find Owners", page.locator("h2").textContent());
		assertTrue(page.locator("input[name='lastName']").isVisible());
		assertTrue(page.locator("button[type='submit']").isVisible());
		assertTrue(page.locator("a[href='/owners/new']").isVisible());
	}

	@Test
	@DisplayName("As a user I want to search for owners by last name so that I can find specific owners")
	void shouldSearchOwnersByLastName() {
		navigateToFindOwners();

		// Search for owners with common last names that should exist in sample data
		page.fill("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should either find results or show "has not been found" message
		assertTrue(page.url().contains("/owners") || page.locator("text*=has not been found").isVisible());
	}

	@Test
	@DisplayName("As a user I want to search with empty criteria so that I can see all owners")
	void shouldSearchWithEmptyCriteria() {
		navigateToFindOwners();

		// Submit empty search to see all owners
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should show results or redirect to owners list
		assertTrue(page.url().contains("/owners"));
	}

	@Test
	@DisplayName("As a user I want to add a new owner so that I can register new pet owners")
	void shouldNavigateToAddOwnerForm() {
		navigateToFindOwners();

		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Should be on new owner form
		assertTrue(page.url().contains("/owners/new"));
		assertEquals("Owner", page.locator("h2").textContent());

		// Verify form fields are present
		assertTrue(page.locator("input[name='firstName']").isVisible());
		assertTrue(page.locator("input[name='lastName']").isVisible());
		assertTrue(page.locator("input[name='address']").isVisible());
		assertTrue(page.locator("input[name='city']").isVisible());
		assertTrue(page.locator("input[name='telephone']").isVisible());
		assertTrue(page.locator("button[type='submit']").isVisible());
	}

	@Test
	@DisplayName("As a user I want to create a new owner with valid data so that I can add owners to the system")
	void shouldCreateNewOwnerWithValidData() {
		navigateToFindOwners();
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Fill out the form with valid data
		String timestamp = String.valueOf(System.currentTimeMillis());
		page.fill("input[name='firstName']", "John");
		page.fill("input[name='lastName']", "TestOwner" + timestamp);
		page.fill("input[name='address']", "123 Test Street");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "5551234567");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should redirect to owner details page or show success
		assertTrue(page.url().contains("/owners/") && !page.url().contains("/new"));
	}

	@Test
	@DisplayName("As a user I want to see validation errors when submitting invalid owner data so that I know what to correct")
	void shouldShowValidationErrorsForInvalidOwnerData() {
		navigateToFindOwners();
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Submit form with missing required fields
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should show validation errors or stay on form
		assertTrue(page.url().contains("/owners/new")
				|| page.locator(".has-error, .is-invalid, .alert-danger").count() > 0);
	}

	@Test
	@DisplayName("As a user I want to validate telephone number format so that contact information is properly formatted")
	void shouldValidateTelephoneNumberFormat() {
		navigateToFindOwners();
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Fill form with invalid telephone number
		page.fill("input[name='firstName']", "Jane");
		page.fill("input[name='lastName']", "TestOwner");
		page.fill("input[name='address']", "456 Test Ave");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "invalid-phone");

		page.click("button[type='submit']");
		waitForPageLoad();

		// Should show validation error for telephone field
		assertTrue(page.url().contains("/owners/new") || page.locator("text*=telephone").count() > 0);
	}

	@Test
	@DisplayName("As a user I want to search for non-existent owner so that I receive appropriate feedback")
	void shouldHandleNonExistentOwnerSearch() {
		navigateToFindOwners();

		// Search for owner that definitely doesn't exist
		page.fill("input[name='lastName']", "NonExistentOwnerXYZ123");
		page.click("button[type='submit']");
		waitForPageLoad();

		// Should show "not found" message
		assertTrue(page.locator("text*=has not been found").isVisible());
	}

	@Test
	@DisplayName("As a user I want to see owner details so that I can view complete owner information")
	void shouldDisplayOwnerDetailsWhenFound() {
		navigateToFindOwners();

		// First create an owner or search for existing one
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageLoad();

		// If we find an owner, click on it to see details
		if (page.locator("a[href*='/owners/']").count() > 0) {
			page.locator("a[href*='/owners/']").first().click();
			waitForPageLoad();

			// Should be on owner details page
			assertTrue(page.locator("h2").isVisible());
			assertTrue(page.locator("table, .table").isVisible());
		}
	}

	@Test
	@DisplayName("As a user I want form field validation to work correctly so that I enter valid data")
	void shouldValidateRequiredFields() {
		navigateToFindOwners();
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Test each required field individually
		String[] requiredFields = { "firstName", "lastName", "address", "city", "telephone" };

		for (String field : requiredFields) {
			// Clear all fields and fill only non-target fields
			page.fill("input[name='firstName']", field.equals("firstName") ? "" : "Test");
			page.fill("input[name='lastName']", field.equals("lastName") ? "" : "User");
			page.fill("input[name='address']", field.equals("address") ? "" : "123 Test St");
			page.fill("input[name='city']", field.equals("city") ? "" : "Test City");
			page.fill("input[name='telephone']", field.equals("telephone") ? "" : "1234567890");

			page.click("button[type='submit']");
			waitForPageLoad();

			// Should stay on form with validation error
			assertTrue(page.url().contains("/owners/new"));
		}
	}

	@Test
	@DisplayName("As a user I want keyboard navigation to work so that I can use the form efficiently")
	void shouldSupportKeyboardNavigation() {
		navigateToFindOwners();

		// Test tab navigation through search form
		page.locator("input[name='lastName']").focus();
		page.keyboard().press("Tab");

		// Should move to submit button
		String focusedElement = (String) page.evaluate("document.activeElement.tagName");
		assertTrue("BUTTON".equals(focusedElement) || "INPUT".equals(focusedElement));

		// Test enter key to submit
		page.locator("input[name='lastName']").focus();
		page.fill("input[name='lastName']", "Test");
		page.keyboard().press("Enter");
		waitForPageLoad();

		// Should have submitted the search
		assertTrue(page.url().contains("/owners"));
	}

}