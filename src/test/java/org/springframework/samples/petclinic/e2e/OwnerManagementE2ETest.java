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

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * End-to-end tests for owner management functionality. Tests cover finding owners,
 * creating new owners, updating owner details, and viewing owner information.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class OwnerManagementE2ETest {

	@LocalServerPort
	private int port;

	private Playwright playwright;

	private Browser browser;

	private BrowserContext context;

	private Page page;

	@BeforeEach
	void setUp() {
		try {
			playwright = Playwright.create();
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			context = browser.newContext();
			page = context.newPage();
		}
		catch (Exception e) {
			assumeTrue(false, "No browser available for testing: " + e.getMessage());
		}
	}

	@AfterEach
	void tearDown() {
		if (page != null)
			page.close();
		if (context != null)
			context.close();
		if (browser != null)
			browser.close();
		if (playwright != null)
			playwright.close();
	}

	@Test
	@DisplayName("As a user, I want to access the find owners page, so that I can search for pet owners")
	void testFindOwnersPageAccess() {
		page.navigate("http://localhost:" + port);
		page.waitForLoadState();

		// Navigate to find owners
		Locator findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		findOwnersLink.click();
		page.waitForLoadState();

		// Verify we're on the find owners page
		assertTrue(page.url().contains("/owners/find"), "Should be on find owners page");

		// Verify page elements are present
		Locator searchForm = page.locator("form");
		assertTrue(searchForm.isVisible(), "Search form should be visible");

		Locator lastNameInput = page.locator("input[name='lastName']");
		assertTrue(lastNameInput.isVisible(), "Last name input should be visible");

		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		assertTrue(findOwnerButton.isVisible(), "Find Owner button should be visible");
	}

	@Test
	@DisplayName("As a user, I want to search for owners with empty criteria, so that I can see all owners")
	void testSearchAllOwners() {
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Click Find Owner button without entering criteria (should show all owners)
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Should be redirected to owners list
		assertTrue(page.url().contains("/owners"), "Should be on owners list page");

		// Check for owners table or list
		Locator ownersTable = page.locator("table, .table, [data-testid='owners-list']");
		if (!ownersTable.isVisible()) {
			// Alternative: check for owner entries
			Locator ownerEntries = page.locator("a[href*='/owners/']").first();
			assertTrue(ownerEntries.isVisible(), "Should show owner entries");
		}
	}

	@Test
	@DisplayName("As a user, I want to search for owners by last name, so that I can find specific owners")
	void testSearchOwnersByLastName() {
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Enter a search term (common last name from test data)
		Locator lastNameInput = page.locator("input[name='lastName']");
		lastNameInput.fill("Davis");

		// Submit search
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Verify search results or owner details page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/owners"), "Should be on owners page after search");

		// Check if we found results (either list or direct match)
		boolean hasResults = page.locator("table").isVisible() || page.locator("a[href*='/owners/']").isVisible()
				|| page.locator("dt, dd").isVisible(); // Owner details page format

		assertTrue(hasResults, "Should show search results or owner details");
	}

	@Test
	@DisplayName("As a user, I want to access the add owner form, so that I can register new pet owners")
	void testAddOwnerFormAccess() {
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Look for "Add Owner" link or button
		Locator addOwnerLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Owner"));
		if (!addOwnerLink.isVisible()) {
			// Alternative locator strategies
			addOwnerLink = page.locator("a[href*='/owners/new'], a[href='/owners/new']");
		}
		assertTrue(addOwnerLink.isVisible(), "Add Owner link should be visible");

		addOwnerLink.click();
		page.waitForLoadState();

		// Verify we're on the new owner form
		assertTrue(page.url().contains("/owners/new"), "Should be on new owner form page");

		// Verify form fields are present
		Locator firstNameInput = page.locator("input[name='firstName']");
		assertTrue(firstNameInput.isVisible(), "First name input should be visible");

		Locator lastNameInput = page.locator("input[name='lastName']");
		assertTrue(lastNameInput.isVisible(), "Last name input should be visible");

		Locator addressInput = page.locator("input[name='address']");
		assertTrue(addressInput.isVisible(), "Address input should be visible");

		Locator cityInput = page.locator("input[name='city']");
		assertTrue(cityInput.isVisible(), "City input should be visible");

		Locator telephoneInput = page.locator("input[name='telephone']");
		assertTrue(telephoneInput.isVisible(), "Telephone input should be visible");
	}

	@Test
	@DisplayName("As a user, I want to create a new owner with valid data, so that I can register new clients")
	void testCreateNewOwnerWithValidData() {
		page.navigate("http://localhost:" + port + "/owners/new");
		page.waitForLoadState();

		// Fill out the form with valid data
		page.locator("input[name='firstName']").fill("John");
		page.locator("input[name='lastName']").fill("Doe");
		page.locator("input[name='address']").fill("123 Main Street");
		page.locator("input[name='city']").fill("Springfield");
		page.locator("input[name='telephone']").fill("5551234567");

		// Submit the form
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
		if (!submitButton.isVisible()) {
			submitButton = page.locator("button[type='submit'], input[type='submit']");
		}
		submitButton.click();
		page.waitForLoadState();

		// Should be redirected to owner details page
		String currentUrl = page.url();
		assertTrue(currentUrl.contains("/owners/") && !currentUrl.contains("/new"),
				"Should be redirected to owner details page");

		// Verify owner details are displayed
		assertTrue(page.locator("dt, dd").isVisible() || page.locator("body").textContent().contains("John Doe"),
				"Owner details should be displayed");
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when creating an owner with invalid data, so that I can correct my input")
	void testCreateOwnerValidationErrors() {
		page.navigate("http://localhost:" + port + "/owners/new");
		page.waitForLoadState();

		// Submit form with missing required fields
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
		if (!submitButton.isVisible()) {
			submitButton = page.locator("button[type='submit'], input[type='submit']");
		}
		submitButton.click();
		page.waitForLoadState();

		// Should stay on the form page with validation errors
		assertTrue(page.url().contains("/owners/new"), "Should stay on new owner form");

		// Check for validation error messages
		boolean hasValidationErrors = page.locator(".alert-danger, .error, .field-error, .invalid-feedback").isVisible()
				|| page.locator("body").textContent().contains("required")
				|| page.locator("body").textContent().contains("may not be empty");

		assertTrue(hasValidationErrors, "Should display validation error messages");
	}

	@Test
	@DisplayName("As a user, I want to view owner details, so that I can see complete information about a pet owner")
	void testViewOwnerDetails() {
		// First find an existing owner
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Search for all owners
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Click on the first owner link if available
		Locator firstOwnerLink = page.locator("a[href*='/owners/']").first();
		if (firstOwnerLink.isVisible()) {
			firstOwnerLink.click();
			page.waitForLoadState();

			// Verify we're on owner details page
			String currentUrl = page.url();
			assertTrue(currentUrl.matches(".*\\/owners\\/\\d+"), "Should be on owner details page");

			// Verify owner information is displayed
			boolean hasOwnerInfo = page.locator("dt, dd").isVisible() || page.locator(".dl-horizontal").isVisible()
					|| page.locator("table").isVisible();

			assertTrue(hasOwnerInfo, "Owner information should be displayed");

			// Check for edit and add pet functionality
			Locator editOwnerLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Owner"));
			if (!editOwnerLink.isVisible()) {
				editOwnerLink = page.locator("a[href*='/edit']");
			}

			Locator addPetLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add New Pet"));
			if (!addPetLink.isVisible()) {
				addPetLink = page.locator("a[href*='/pets/new']");
			}

			assertTrue(editOwnerLink.isVisible() || addPetLink.isVisible(),
					"Edit Owner or Add Pet functionality should be available");
		}
	}

}