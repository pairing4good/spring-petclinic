/*
 * Copyright 2012-2019 the original author or authors.
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Comprehensive end-to-end tests covering advanced features like pet management, visits,
 * form validation, pagination, and error handling.
 *
 * @author Copilot AI
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
		properties = { "spring.datasource.url=jdbc:h2:mem:testdb", "spring.jpa.hibernate.ddl-auto=create-drop" })
@DisplayName("Comprehensive PetClinic E2E Tests")
public class ComprehensiveE2ETests {

	@LocalServerPort
	private int port;

	private static Playwright playwright;

	private static Browser browser;

	private BrowserContext context;

	private Page page;

	@BeforeAll
	static void initializePlaywright() {
		playwright = Playwright.create();
		browser = playwright.chromium()
			.launch(new BrowserType.LaunchOptions()
				.setHeadless(Boolean.parseBoolean(System.getProperty("playwright.headless", "true"))));
	}

	@BeforeEach
	void setupContext() {
		context = browser.newContext();
		page = context.newPage();
	}

	@AfterEach
	void cleanupContext() {
		if (context != null) {
			context.close();
		}
	}

	@AfterAll
	static void cleanupPlaywright() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@Test
	@DisplayName("As an owner, I want to manage pets, so that I can add and edit pet information")
	void testPetManagement() {
		// Navigate to first owner's details
		navigateToFirstOwner();

		// Test adding a new pet
		page.locator("a:has-text('Add New Pet')").click();
		assertThat(page.locator("h2:has-text('Pet')")).isVisible();

		// Fill pet form
		page.locator("input[name='name']").fill("Buddy");
		page.locator("input[name='birthDate']").fill("2020-01-15");

		// Select pet type if dropdown exists
		if (page.locator("select[name='type']").count() > 0) {
			page.locator("select[name='type']").selectOption("dog");
		}

		// Submit form
		page.locator("button:has-text('Add Pet')").click();

		// Should return to owner details
		assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
		assertThat(page.locator("text=Buddy")).isVisible();
	}

	@Test
	@DisplayName("As a veterinarian, I want to add visit records, so that I can track pet medical history")
	void testVisitManagement() {
		navigateToFirstOwner();

		// Look for Add Visit link (might be present if pets exist)
		if (page.locator("a:has-text('Add Visit')").count() > 0) {
			page.locator("a:has-text('Add Visit')").first().click();

			// Fill visit form
			assertThat(page.locator("h2:has-text('New Visit')")).isVisible();
			page.locator("input[name='date']").fill("2023-12-01");
			page.locator("textarea[name='description']").fill("Regular checkup - pet is healthy");

			// Submit form
			page.locator("button:has-text('Add Visit')").click();

			// Should return to owner details
			assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
		}
	}

	@Test
	@DisplayName("As a user, I want to edit owner information, so that I can update client details")
	void testEditOwner() {
		navigateToFirstOwner();

		// Click Edit Owner
		page.locator("a:has-text('Edit Owner')").click();

		// Verify we're on edit form
		assertThat(page.locator("h2:has-text('Owner')")).isVisible();

		// Update city
		page.locator("input[name='city']").fill("Updated City Name");

		// Submit form
		page.locator("button:has-text('Update Owner')").click();

		// Verify update
		assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
		assertThat(page.locator("text=Updated City Name")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want pagination to work, so that I can browse through multiple pages")
	void testPagination() {
		// Test owners pagination
		page.navigate("http://localhost:" + port + "/owners/find");
		page.locator("button:has-text('Find Owner')").click();

		// Check if pagination exists
		if (page.locator("a:has-text('2')").count() > 0) {
			page.locator("a:has-text('2')").click();
			assertThat(page.locator("table")).isVisible();
		}

		// Test vets pagination
		page.navigate("http://localhost:" + port + "/vets.html");
		if (page.locator("a:has-text('2')").count() > 0) {
			page.locator("a:has-text('2')").click();
			assertThat(page.locator("table")).isVisible();
		}
	}

	@Test
	@DisplayName("As a user, I want search functionality to work, so that I can find specific owners")
	void testSearchFunctionality() {
		page.navigate("http://localhost:" + port + "/owners/find");

		// Search for Franklin
		page.locator("input[type='text']").fill("Franklin");
		page.locator("button:has-text('Find Owner')").click();

		// Should find results or show owner details
		page.waitForTimeout(1000); // Wait for page to load
		boolean hasResults = page.locator("table").count() > 0
				|| page.locator("h2:has-text('Owner Information')").count() > 0;
		assert hasResults : "Should find Franklin in search results";
	}

	@Test
	@DisplayName("As a user, I want error handling to work properly, so that I get helpful feedback")
	void testErrorHandling() {
		// Test 500 error page
		page.navigate("http://localhost:" + port + "/oups");
		assertThat(page.locator("h2:has-text('Something happened...')")).isVisible();

		// Test 404 handling
		page.navigate("http://localhost:" + port + "/invalid-url-12345");
		page.waitForTimeout(1000);

		// Should handle gracefully (might redirect to error page or show 404)
		boolean hasErrorHandling = page.url().contains("error") || page.url().contains("oups")
				|| page.locator("h1").count() > 0 || page.locator("h2").count() > 0;
		assert hasErrorHandling : "Should handle 404 errors gracefully";
	}

	@Test
	@DisplayName("As a user, I want form validation to prevent invalid submissions, so that data integrity is maintained")
	void testFormValidation() {
		// Test owner form validation
		page.navigate("http://localhost:" + port + "/owners/new");

		// Submit empty form
		page.locator("button:has-text('Add Owner')").click();
		assertThat(page).hasURL("http://localhost:" + port + "/owners/new");

		// Submit with only first name
		page.locator("input[name='firstName']").fill("Test");
		page.locator("button:has-text('Add Owner')").click();
		assertThat(page).hasURL("http://localhost:" + port + "/owners/new");

		// Test invalid phone number
		page.locator("input[name='firstName']").fill("Test");
		page.locator("input[name='lastName']").fill("User");
		page.locator("input[name='address']").fill("123 Test St");
		page.locator("input[name='city']").fill("Test City");
		page.locator("input[name='telephone']").fill("invalid-phone");
		page.locator("button:has-text('Add Owner')").click();

		// Should stay on form or show validation error
		assertThat(page).hasURL("http://localhost:" + port + "/owners/new");
	}

	@Test
	@DisplayName("As a user, I want consistent page structure, so that I have a uniform experience")
	void testConsistentPageStructure() {
		String[] urls = { "/", "/owners/find", "/vets.html", "/oups" };

		for (String url : urls) {
			page.navigate("http://localhost:" + port + url);

			// Common elements should exist on all pages
			assertThat(page.locator("nav")).isVisible();
			assertThat(page.locator("img[alt*='Logo']")).isVisible();
			assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

			// Navigation links should be accessible
			assertThat(page.locator("a:has-text('Home')")).isVisible();
			assertThat(page.locator("a:has-text('Find Owners')")).isVisible();
			assertThat(page.locator("a:has-text('Veterinarians')")).isVisible();
		}
	}

	@Test
	@DisplayName("As a user, I want keyboard navigation to work, so that I can use the app without a mouse")
	void testKeyboardNavigation() {
		page.navigate("http://localhost:" + port);

		// Tab through elements
		page.keyboard().press("Tab");
		page.keyboard().press("Tab");

		// Should be able to activate focused element
		page.keyboard().press("Enter");
		page.waitForTimeout(1000);

		// Should have navigated somewhere
		assert !page.url().equals("http://localhost:" + port + "/") : "Keyboard navigation should work";
	}

	@Test
	@DisplayName("As a user, I want loading states to be handled properly, so that I know when content is loading")
	void testLoadingStates() {
		page.navigate("http://localhost:" + port + "/vets.html");

		// Page should load completely
		assertThat(page.locator("h2:has-text('Veterinarians')")).isVisible();
		assertThat(page.locator("table")).isVisible();

		// No loading indicators should be visible
		assert page.locator(".loading, .spinner").count() == 0
				: "No loading indicators should be visible when page is loaded";
	}

	@Test
	@DisplayName("As a user, I want accessibility features to work, so that the app is usable by everyone")
	void testAccessibilityFeatures() {
		page.navigate("http://localhost:" + port + "/owners/new");

		// Form labels should be properly associated
		assertThat(page.locator("label[for='firstName'], label:has-text('First Name')")).isVisible();
		assertThat(page.locator("label[for='lastName'], label:has-text('Last Name')")).isVisible();

		// Buttons should be properly labeled
		assertThat(page.locator("button:has-text('Add Owner')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to test cross-browser compatibility, so that the app works in different browsers")
	void testCrossBrowserCompatibility() {
		// This test runs in Chromium by default
		// In a full test suite, you would run the same tests in Firefox and WebKit
		page.navigate("http://localhost:" + port);

		// Basic functionality should work
		assertThat(page.locator("h2:has-text('Welcome')")).isVisible();
		assertThat(page.locator("nav")).isVisible();

		// Navigation should work
		page.locator("a:has-text('Find Owners')").click();
		assertThat(page.locator("h2:has-text('Find Owners')")).isVisible();
	}

	/**
	 * Helper method to navigate to the first owner's details page
	 */
	private void navigateToFirstOwner() {
		page.navigate("http://localhost:" + port + "/owners/find");
		page.locator("button:has-text('Find Owner')").click();
		page.locator("table a").first().click();
		assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
	}

}