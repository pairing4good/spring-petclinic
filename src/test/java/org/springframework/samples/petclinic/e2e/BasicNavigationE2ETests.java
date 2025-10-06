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
 * Simple end-to-end tests for basic navigation functionality.
 *
 * @author Copilot AI
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
		properties = { "spring.datasource.url=jdbc:h2:mem:testdb", "spring.jpa.hibernate.ddl-auto=create-drop" })
@DisplayName("Basic Navigation E2E Tests")
public class BasicNavigationE2ETests {

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
	@DisplayName("As a user, I want to load the homepage, so that I can access the PetClinic application")
	void testHomepageLoading() {
		page.navigate("http://localhost:" + port);

		// Verify page title
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

		// Verify main heading is visible
		assertThat(page.locator("h2:has-text('Welcome')")).isVisible();

		// Verify navigation menu exists
		assertThat(page.locator("nav")).isVisible();

		// Verify key navigation links
		assertThat(page.locator("a:has-text('Home')")).isVisible();
		assertThat(page.locator("a:has-text('Find Owners')")).isVisible();
		assertThat(page.locator("a:has-text('Veterinarians')")).isVisible();
		assertThat(page.locator("a:has-text('Error')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners page, so that I can search for owners")
	void testNavigateToFindOwners() {
		page.navigate("http://localhost:" + port);

		// Click Find Owners link
		page.locator("a:has-text('Find Owners')").click();

		// Verify we're on the Find Owners page
		assertThat(page).hasURL("http://localhost:" + port + "/owners/find");
		assertThat(page.locator("h2:has-text('Find Owners')")).isVisible();

		// Verify form elements exist
		assertThat(page.locator("input[type='text']")).isVisible();
		assertThat(page.locator("button:has-text('Find Owner')")).isVisible();
		assertThat(page.locator("a:has-text('Add Owner')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians page, so that I can view available vets")
	void testNavigateToVeterinarians() {
		page.navigate("http://localhost:" + port);

		// Click Veterinarians link
		page.locator("a:has-text('Veterinarians')").click();

		// Verify we're on the Veterinarians page
		assertThat(page).hasURL("http://localhost:" + port + "/vets.html");
		assertThat(page.locator("h2:has-text('Veterinarians')")).isVisible();

		// Verify table exists
		assertThat(page.locator("table")).isVisible();

		// Verify table headers
		assertThat(page.locator("th:has-text('Name')")).isVisible();
		assertThat(page.locator("th:has-text('Specialties')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to view the error page, so that I can see error handling")
	void testNavigateToErrorPage() {
		page.navigate("http://localhost:" + port);

		// Click Error link
		page.locator("a:has-text('Error')").click();

		// Verify we're on the error page
		assertThat(page).hasURL("http://localhost:" + port + "/oups");
		assertThat(page.locator("h2:has-text('Something happened...')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to search for all owners, so that I can see the complete owners list")
	void testFindAllOwners() {
		page.navigate("http://localhost:" + port + "/owners/find");

		// Click Find Owner without entering search criteria
		page.locator("button:has-text('Find Owner')").click();

		// Should show owners list
		assertThat(page).hasURL("http://localhost:" + port + "/owners");
		assertThat(page.locator("h2:has-text('Owners')")).isVisible();
		assertThat(page.locator("table")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to add a new owner, so that I can register new clients")
	void testAddNewOwner() {
		page.navigate("http://localhost:" + port + "/owners/find");

		// Click Add Owner
		page.locator("a:has-text('Add Owner')").click();

		// Verify we're on the new owner form
		assertThat(page).hasURL("http://localhost:" + port + "/owners/new");
		assertThat(page.locator("h2:has-text('Owner')")).isVisible();

		// Fill out the form
		page.locator("input[name='firstName']").fill("John");
		page.locator("input[name='lastName']").fill("Doe");
		page.locator("input[name='address']").fill("123 Main St");
		page.locator("input[name='city']").fill("Springfield");
		page.locator("input[name='telephone']").fill("5551234567");

		// Submit the form
		page.locator("button:has-text('Add Owner')").click();

		// Should redirect to owner details page
		assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
		assertThat(page.locator("text=John Doe")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to view owner details, so that I can see comprehensive owner information")
	void testViewOwnerDetails() {
		// First find all owners
		page.navigate("http://localhost:" + port + "/owners/find");
		page.locator("button:has-text('Find Owner')").click();

		// Click on first owner link
		page.locator("table a").first().click();

		// Verify owner details page
		assertThat(page.locator("h2:has-text('Owner Information')")).isVisible();
		assertThat(page.locator("table")).isVisible();

		// Verify action buttons
		assertThat(page.locator("a:has-text('Edit Owner')")).isVisible();
		assertThat(page.locator("a:has-text('Add New Pet')")).isVisible();

		// Verify pets section
		assertThat(page.locator("h2:has-text('Pets and Visits')")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want browser navigation to work properly, so that I can use back/forward buttons")
	void testBrowserNavigation() {
		page.navigate("http://localhost:" + port);

		// Navigate to Find Owners
		page.locator("a:has-text('Find Owners')").click();
		assertThat(page).hasURL("http://localhost:" + port + "/owners/find");

		// Navigate to Veterinarians
		page.locator("a:has-text('Veterinarians')").click();
		assertThat(page).hasURL("http://localhost:" + port + "/vets.html");

		// Use browser back
		page.goBack();
		assertThat(page).hasURL("http://localhost:" + port + "/owners/find");

		// Use browser forward
		page.goForward();
		assertThat(page).hasURL("http://localhost:" + port + "/vets.html");
	}

	@Test
	@DisplayName("As a user, I want form validation to work, so that I can see errors for invalid input")
	void testFormValidation() {
		page.navigate("http://localhost:" + port + "/owners/new");

		// Try to submit empty form
		page.locator("button:has-text('Add Owner')").click();

		// Should stay on the same page due to validation
		assertThat(page).hasURL("http://localhost:" + port + "/owners/new");
	}

	@Test
	@DisplayName("As a user, I want responsive design to work, so that I can use the app on different devices")
	void testResponsiveDesign() {
		// Test mobile viewport
		page.setViewportSize(375, 667);
		page.navigate("http://localhost:" + port);

		// Navigation should still be visible
		assertThat(page.locator("nav")).isVisible();
		assertThat(page.locator("a:has-text('Find Owners')")).isVisible();

		// Test tablet viewport
		page.setViewportSize(768, 1024);
		assertThat(page.locator("nav")).isVisible();

		// Reset to desktop
		page.setViewportSize(1280, 720);
		assertThat(page.locator("nav")).isVisible();
	}

}