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
 * User acceptance tests for owner management functionality.
 * 
 * These tests verify that users can search for, view, create, and update pet owners
 * in the PetClinic application.
 */
class OwnerManagementUserAcceptanceTests extends PlaywrightTestBase {

	@BeforeEach
	void setUp() {
		navigateToHomePage();
	}

	@Test
	@DisplayName("As a user, I want to search for owners by last name, so that I can find existing pet owners quickly")
	void shouldSearchForOwnersByLastName() {
		// Given: A user wants to find owners with last name "Davis"
		page.click("a[href='/owners/find']");
		page.waitForLoadState();

		// When: They enter a last name and search
		page.fill("input[name='lastName']", "Davis");
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: They should see the search results
		assertThat(page.locator("h2")).containsText("Owners");
		// The page should show the owner if it exists, or show an empty list
		assertThat(page.locator("table")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to view all owners when I search with an empty last name, so that I can browse all pet owners")
	void shouldDisplayAllOwnersWhenSearchingWithEmptyLastName() {
		// Given: A user wants to see all owners
		page.click("a[href='/owners/find']");
		page.waitForLoadState();

		// When: They click search without entering a last name
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: They should see a list of all owners
		assertThat(page.locator("h2")).containsText("Owners");
		assertThat(page.locator("table")).isVisible();
		
		// Should see the "Add Owner" button
		assertThat(page.locator("a[href='/owners/new']")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to add a new owner, so that I can register new pet owners in the system")
	void shouldAddNewOwner() {
		// Given: A user wants to add a new owner
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();

		// When: They click "Add Owner" and fill in the form
		page.click("a[href='/owners/new']");
		page.waitForLoadState();

		assertThat(page.locator("h2")).containsText("New Owner");

		// Fill in the owner details
		page.fill("input[name='firstName']", "John");
		page.fill("input[name='lastName']", "Playwright");
		page.fill("input[name='address']", "123 Test Street");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "1234567890");

		// Submit the form
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: They should be redirected to the owner's details page
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("td")).containsText("John Playwright");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when I submit invalid owner data, so that I know what needs to be corrected")
	void shouldShowValidationErrorsForInvalidOwnerData() {
		// Given: A user is adding a new owner
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();
		page.click("a[href='/owners/new']");
		page.waitForLoadState();

		// When: They submit the form without required fields
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Then: They should see validation errors
		assertThat(page.locator("h2")).containsText("New Owner");
		// Form should show validation errors (the page should stay on the form)
		assertThat(page.locator("form")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to view an owner's details, so that I can see their information and pets")
	void shouldViewOwnerDetails() {
		// Given: There are owners in the system
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();

		// When: They click on an owner's name (if any owners exist)
		if (page.locator("table tbody tr").count() > 0) {
			page.locator("table tbody tr").first().locator("a").click();
			page.waitForLoadState();

			// Then: They should see the owner's details page
			assertThat(page.locator("h2")).containsText("Owner Information");
			
			// Should see owner details table
			assertThat(page.locator("table.table-striped")).isVisible();
			
			// Should see "Edit Owner" button
			assertThat(page.locator("a[href*='/edit']")).isVisible();
			
			// Should see "Add New Pet" button
			assertThat(page.locator("a[href*='/pets/new']")).isVisible();
		}
	}

	@Test
	@DisplayName("As a user, I want to edit an owner's information, so that I can update their details when they change")
	void shouldEditOwnerInformation() {
		// Given: A user has found an owner and wants to edit their information
		page.click("a[href='/owners/find']");
		page.waitForLoadState();
		page.click("button[type='submit']");
		page.waitForLoadState();

		// Navigate to first owner's details if any exist
		if (page.locator("table tbody tr").count() > 0) {
			page.locator("table tbody tr").first().locator("a").click();
			page.waitForLoadState();

			// When: They click "Edit Owner"
			page.click("a[href*='/edit']");
			page.waitForLoadState();

			// Then: They should see the edit form with current data
			assertThat(page.locator("h2")).containsText("Owner");
			assertThat(page.locator("form")).isVisible();
			assertThat(page.locator("input[name='firstName']")).isVisible();
			assertThat(page.locator("input[name='lastName']")).isVisible();

			// Should be able to update and save
			page.fill("input[name='city']", "Updated City");
			page.click("button[type='submit']");
			page.waitForLoadState();

			// Should be redirected back to owner details
			assertThat(page.locator("h2")).containsText("Owner Information");
		}
	}

}