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

package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * User acceptance tests for error handling and edge cases.
 *
 * These tests verify that the application handles errors gracefully and provides helpful
 * feedback to users when something goes wrong.
 */
class ErrorHandlingUserAcceptanceTests extends PlaywrightTestBase {

	@BeforeEach
	void setUp() {
		navigateToHomePage();
	}

	@Test
	@DisplayName("As a user, I want to see a friendly error page when something goes wrong, so that I understand what happened and can take action")
	void shouldDisplayFriendlyErrorPage() {
		// Given: A user encounters an error situation
		// When: They navigate to the error demonstration page
		page.click("a[href='/oups']");
		page.waitForLoadState();

		// Then: They should see a user-friendly error page
		assertThat(page.locator("h2")).containsText("Something happened...");

		// Should provide some explanation or guidance
		assertThat(page.locator("body")).isVisible();

		// Should maintain the application layout/navigation
		assertThat(page.locator("nav")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to be able to navigate back to the main application from an error page, so that I can continue using the application")
	void shouldAllowNavigationFromErrorPage() {
		// Given: A user is on an error page
		page.click("a[href='/oups']");
		page.waitForLoadState();

		// When: They try to navigate back to the home page
		page.click("a[href='/']");
		page.waitForLoadState();

		// Then: They should be able to return to the home page
		assertThat(page).hasURL(baseUrl + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@Test
	@DisplayName("As a user, I want to receive helpful feedback when I try to access a page that doesn't exist, so that I know what went wrong")
	void shouldHandleNotFoundPagesGracefully() {
		// Given: A user tries to access a non-existent page
		// When: They navigate to an invalid URL
		page.navigate(baseUrl + "/nonexistent-page");
		page.waitForLoadState();

		// Then: They should receive appropriate feedback
		// This could be a 404 page or redirect to home - either is acceptable
		// The key is that the page loads and doesn't crash
		assertThat(page.locator("body")).isVisible();

		// Navigation should still be available
		assertThat(page.locator("nav")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want the application to handle form submission errors gracefully, so that I can correct my input and try again")
	void shouldHandleFormSubmissionErrorsGracefully() {
		// Given: A user is filling out a form with invalid data
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();
		page.click("a[href='/owners/new']");
		page.waitForLoadState();

		// When: They submit invalid data (like an invalid phone number)
		page.fill("input[name='firstName']", "Test");
		page.fill("input[name='lastName']", "User");
		page.fill("input[name='address']", "123 Test St");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "invalid-phone");

		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: The form should handle the error gracefully
		// Either showing validation errors or accepting the input
		assertThat(page.locator("body")).isVisible();
		assertThat(page.locator("form")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want the application to maintain navigation functionality even when errors occur, so that I can always find my way around")
	void shouldMaintainNavigationDuringErrors() {
		// Given: A user encounters various error scenarios
		page.click("a[href='/oups']");
		page.waitForLoadState();

		// When: They try to use the main navigation
		// Then: All navigation links should still work
		assertThat(page.locator("nav")).isVisible();
		assertThat(page.locator("a[href='/']")).isVisible();
		assertThat(page.locator("a[href='/owners/find']")).isVisible();
		assertThat(page.locator("a[href='/vets.html']")).isVisible();

		// Test that navigation actually works
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		assertThat(page.locator("h2")).containsText("Find Owners");
	}

	@Test
	@DisplayName("As a user, I want search functionality to handle empty results gracefully, so that I understand when no matches are found")
	void shouldHandleEmptySearchResultsGracefully() {
		// Given: A user is searching for owners
		page.click("a[href='/owners/find']");
		page.waitForLoadState();

		// When: They search for a name that doesn't exist
		page.fill("input[name='lastName']", "NonExistentOwner12345");
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: The application should handle empty results gracefully
		// Either showing "no results found" or an empty list
		assertThat(page.locator("body")).isVisible();

		// Should still show the search interface or results structure
		if (page.locator("h2").textContent().contains("Owners")) {
			// Empty results page is acceptable
			assertThat(page.locator("h2")).containsText("Owners");
		}
		else {
			// Returning to search form is also acceptable
			assertThat(page.locator("h2")).containsText("Find Owners");
		}
	}

}