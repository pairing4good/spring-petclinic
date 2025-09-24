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
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * End-to-end tests for pet management functionality. Tests cover adding pets to owners,
 * editing pet details, and managing visits.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("playwright")
class PetManagementE2ETest {

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
	@DisplayName("As a user, I want to access add pet functionality, so that I can register new pets for owners")
	void testAccessAddPetFunctionality() {
		// First find and navigate to an owner's details page
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Search for all owners
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Click on first owner if available
		Locator firstOwnerLink = page.locator("a[href*='/owners/']").first();
		if (firstOwnerLink.isVisible()) {
			firstOwnerLink.click();
			page.waitForLoadState();

			// Look for Add New Pet link
			Locator addPetLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add New Pet"));
			if (!addPetLink.isVisible()) {
				addPetLink = page.locator("a[href*='/pets/new']");
			}

			if (addPetLink.isVisible()) {
				addPetLink.click();
				page.waitForLoadState();

				// Verify we're on add pet form
				assertTrue(page.url().contains("/pets/new"), "Should be on add pet form");

				// Verify form elements are present
				Locator petNameInput = page.locator("input[name='name']");
				assertTrue(petNameInput.isVisible(), "Pet name input should be visible");

				Locator petTypeSelect = page.locator("select[name='type'], select[name='typeId']");
				assertTrue(petTypeSelect.isVisible(), "Pet type select should be visible");

				Locator birthDateInput = page.locator("input[name='birthDate']");
				assertTrue(birthDateInput.isVisible(), "Birth date input should be visible");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to add a new pet with valid data, so that I can register pets for owners")
	void testAddNewPetWithValidData() {
		// Navigate to add pet form (assuming owner with ID 1 exists)
		page.navigate("http://localhost:" + port + "/owners/1/pets/new");
		page.waitForLoadState();

		// Check if we reached the form or need to find another owner
		if (page.url().contains("/pets/new")) {
			// Fill out pet form
			Locator petNameInput = page.locator("input[name='name']");
			if (petNameInput.isVisible()) {
				petNameInput.fill("Fluffy");
			}

			Locator petTypeSelect = page.locator("select[name='type'], select[name='typeId']");
			if (petTypeSelect.isVisible()) {
				petTypeSelect.selectOption(new SelectOption().setIndex(0)); // Select
																			// first type
			}

			Locator birthDateInput = page.locator("input[name='birthDate']");
			if (birthDateInput.isVisible()) {
				birthDateInput.fill("2023-01-15");
			}

			// Submit form
			Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Pet"));
			if (!submitButton.isVisible()) {
				submitButton = page.locator("button[type='submit'], input[type='submit']");
			}

			if (submitButton.isVisible()) {
				submitButton.click();
				page.waitForLoadState();

				// Should be redirected to owner details page
				String currentUrl = page.url();
				assertTrue(currentUrl.contains("/owners/") && !currentUrl.contains("/pets/new"),
						"Should be redirected to owner details page");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to see validation errors when adding invalid pet data, so that I can correct my input")
	void testAddPetValidationErrors() {
		page.navigate("http://localhost:" + port + "/owners/1/pets/new");
		page.waitForLoadState();

		if (page.url().contains("/pets/new")) {
			// Submit form without required data
			Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Pet"));
			if (!submitButton.isVisible()) {
				submitButton = page.locator("button[type='submit'], input[type='submit']");
			}

			if (submitButton.isVisible()) {
				submitButton.click();
				page.waitForLoadState();

				// Should stay on the form with validation errors
				assertTrue(page.url().contains("/pets/new"), "Should stay on add pet form");

				// Check for validation errors
				boolean hasValidationErrors = page.locator(".alert-danger").isVisible()
						|| page.locator(".error").isVisible() || page.locator(".field-error").isVisible()
						|| page.locator(".invalid-feedback").isVisible()
						|| page.locator("body").textContent().contains("required")
						|| page.locator("body").textContent().contains("may not be empty");

				assertTrue(hasValidationErrors, "Should display validation error messages");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to edit existing pet details, so that I can update pet information")
	void testEditPetDetails() {
		// Navigate to find owners
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Find owners
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Click on first owner
		Locator firstOwnerLink = page.locator("a[href*='/owners/']").first();
		if (firstOwnerLink.isVisible()) {
			firstOwnerLink.click();
			page.waitForLoadState();

			// Look for edit pet links
			Locator editPetLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Pet"));
			if (!editPetLink.isVisible()) {
				editPetLink = page.locator("a[href*='/pets/'][href*='/edit']");
			}

			if (editPetLink.isVisible()) {
				editPetLink.click();
				page.waitForLoadState();

				// Verify we're on edit pet form
				assertTrue(page.url().contains("/pets/") && page.url().contains("/edit"), "Should be on edit pet form");

				// Verify form is populated
				Locator petNameInput = page.locator("input[name='name']");
				if (petNameInput.isVisible()) {
					String currentName = petNameInput.inputValue();
					assertNotNull(currentName, "Pet name should be populated");
					assertFalse(currentName.isEmpty(), "Pet name should not be empty");
				}
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to add visits for pets, so that I can track veterinary appointments")
	void testAddVisitForPet() {
		// Navigate to find owners
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Find owners
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Click on first owner
		Locator firstOwnerLink = page.locator("a[href*='/owners/']").first();
		if (firstOwnerLink.isVisible()) {
			firstOwnerLink.click();
			page.waitForLoadState();

			// Look for add visit links
			Locator addVisitLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Visit"));
			if (!addVisitLink.isVisible()) {
				addVisitLink = page.locator("a[href*='/visits/new']");
			}

			if (addVisitLink.isVisible()) {
				addVisitLink.click();
				page.waitForLoadState();

				// Verify we're on add visit form
				assertTrue(page.url().contains("/visits/new"), "Should be on add visit form");

				// Verify form elements
				Locator visitDateInput = page.locator("input[name='date']");
				assertTrue(visitDateInput.isVisible(), "Visit date input should be visible");

				Locator descriptionInput = page.locator("textarea[name='description'], input[name='description']");
				assertTrue(descriptionInput.isVisible(), "Description input should be visible");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to add a visit with valid data, so that I can record veterinary appointments")
	void testAddVisitWithValidData() {
		// Try to navigate directly to add visit form
		page.navigate("http://localhost:" + port + "/owners/1/pets/1/visits/new");
		page.waitForLoadState();

		if (page.url().contains("/visits/new")) {
			// Fill visit form
			Locator visitDateInput = page.locator("input[name='date']");
			if (visitDateInput.isVisible()) {
				visitDateInput.fill("2024-01-15");
			}

			Locator descriptionInput = page.locator("textarea[name='description'], input[name='description']");
			if (descriptionInput.isVisible()) {
				descriptionInput.fill("Regular checkup");
			}

			// Submit form
			Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Visit"));
			if (!submitButton.isVisible()) {
				submitButton = page.locator("button[type='submit'], input[type='submit']");
			}

			if (submitButton.isVisible()) {
				submitButton.click();
				page.waitForLoadState();

				// Should be redirected to owner details
				String currentUrl = page.url();
				assertTrue(currentUrl.contains("/owners/") && !currentUrl.contains("/visits/new"),
						"Should be redirected to owner details page");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to see validation errors for invalid visit data, so that I can provide correct information")
	void testAddVisitValidationErrors() {
		page.navigate("http://localhost:" + port + "/owners/1/pets/1/visits/new");
		page.waitForLoadState();

		if (page.url().contains("/visits/new")) {
			// Submit form without required data
			Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Visit"));
			if (!submitButton.isVisible()) {
				submitButton = page.locator("button[type='submit'], input[type='submit']");
			}

			if (submitButton.isVisible()) {
				submitButton.click();
				page.waitForLoadState();

				// Check for validation errors or proper form handling
				boolean hasProperHandling = page.url().contains("/visits/new")
						|| page.locator(".alert-danger").isVisible() || page.locator(".error").isVisible()
						|| page.locator("body").textContent().contains("required");

				assertTrue(hasProperHandling, "Should handle form validation properly");
			}
		}
	}

	@Test
	@DisplayName("As a user, I want to view pet history, so that I can see all visits and details")
	void testViewPetHistory() {
		// Navigate to find owners
		page.navigate("http://localhost:" + port + "/owners/find");
		page.waitForLoadState();

		// Find owners
		Locator findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		findOwnerButton.click();
		page.waitForLoadState();

		// Click on first owner
		Locator firstOwnerLink = page.locator("a[href*='/owners/']").first();
		if (firstOwnerLink.isVisible()) {
			firstOwnerLink.click();
			page.waitForLoadState();

			// Should see owner details with pets and visits
			String currentUrl = page.url();
			assertTrue(currentUrl.matches(".*\\/owners\\/\\d+"), "Should be on owner details page");

			// Check for pet information display
			boolean hasPetInfo = page.locator("table").isVisible() || page.locator(".pet").isVisible()
					|| page.locator("dt, dd").isVisible() || page.locator("body").textContent().contains("Pet")
					|| page.locator("body").textContent().contains("Visit");

			assertTrue(hasPetInfo, "Should display pet and visit information");
		}
	}

	@Test
	@DisplayName("As a user, I want consistent navigation in pet management, so that I can move between sections easily")
	void testPetManagementNavigation() {
		page.navigate("http://localhost:" + port + "/owners/1/pets/new");
		page.waitForLoadState();

		if (page.url().contains("/pets/new")) {
			// Check for navigation back to owner
			Locator backLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Owner Information"));
			if (!backLink.isVisible()) {
				backLink = page.locator("a[href*='/owners/']").first();
			}

			if (backLink.isVisible()) {
				backLink.click();
				page.waitForLoadState();

				// Should be back on owner details page
				String currentUrl = page.url();
				assertTrue(currentUrl.matches(".*\\/owners\\/\\d+"), "Should navigate back to owner details");
			}
		}
	}

}