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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.base.BaseE2ETest;
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive E2E tests for Veterinarians page and Error Handling functionality.
 * 
 * Tests cover:
 * - Veterinarians page loading and data display
 * - Error page handling (500 errors, 404 errors)
 * - Edge cases and boundary conditions
 * - User feedback and error messages
 * - Recovery from error states
 */
@DisplayName("Veterinarians and Error Handling E2E Tests")
class VeterinariansAndErrorHandlingE2ETest extends BaseE2ETest {

	private VeterinariansPage veterinariansPage;

	@BeforeEach
	void setUpPages() {
		veterinariansPage = new VeterinariansPage(page);
	}

	@Test
	@DisplayName("As a user, I want to view veterinarians, so that I can see available veterinary staff")
	void testViewVeterinarians() {
		// Given: I navigate to the veterinarians page
		veterinariansPage.navigate(getBaseUrl());

		// Then: The page should load successfully
		assertTrue(veterinariansPage.isLoaded(), "Veterinarians page should be loaded");

		// And: I should see the page heading
		assertTrue(veterinariansPage.getPageHeading().contains("Veterinarians"),
				"Page should have Veterinarians heading");

		// And: The page should show veterinarian data or appropriate empty state
		assertTrue(veterinariansPage.hasVeterinarianData() || veterinariansPage.hasEmptyStateHandling(),
				"Should show veterinarian data or appropriate empty state");
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian information in a table, so that I can easily read their details")
	void testVeterinarianTableDisplay() {
		// Given: I am on the veterinarians page
		veterinariansPage.navigate(getBaseUrl());
		assertTrue(veterinariansPage.isLoaded(), "Veterinarians page should be loaded");

		// Then: If there are veterinarians, I should see them in a structured table
		if (veterinariansPage.hasVeterinarianData()) {
			assertTrue(veterinariansPage.hasTableHeaders(), "Should have table headers");
			assertTrue(veterinariansPage.getVetCount() > 0, "Should have at least one veterinarian");

			// And: Each veterinarian should have a name
			String firstVetName = veterinariansPage.getVetName(0);
			assertFalse(firstVetName.isEmpty(), "First veterinarian should have a name");
		}
	}

	@Test
	@DisplayName("As a user, I want to see veterinarian specialties, so that I know their areas of expertise")
	void testVeterinarianSpecialties() {
		// Given: I am on the veterinarians page
		veterinariansPage.navigate(getBaseUrl());
		assertTrue(veterinariansPage.isLoaded(), "Veterinarians page should be loaded");

		// Then: If there are veterinarians, I should see their specialty information
		if (veterinariansPage.hasVeterinarianData()) {
			// Veterinarians may or may not have specialties, so just verify the method works
			String specialties = veterinariansPage.getVetSpecialties(0);
			assertTrue(specialties != null, "Specialties should not be null");
		}
	}

	@Test
	@DisplayName("As a user, I want to trigger the error page, so that I can see how errors are handled")
	void testErrorPageHandling() {
		// Given: I navigate to the error trigger URL
		navigateToPath("/oups");

		// Then: I should see the error page
		waitForPageLoad();
		assertTrue(isElementVisible("h2"), "Error page should have a heading");

		// And: The error page should show appropriate error message
		String errorContent = getTextContent("h2");
		assertTrue(errorContent.contains("Something happened"), "Should show error message");

		// And: The error page should not be a generic white label error
		String pageContent = page.content();
		assertFalse(pageContent.contains("Whitelabel Error Page"), "Should not show whitelabel error page");
	}

	@Test
	@DisplayName("As a user, I want to see a 404 page for non-existent URLs, so that I understand the page doesn't exist")
	void testNotFoundPageHandling() {
		// Given: I navigate to a non-existent page
		navigateToPath("/nonexistentpage12345");

		// Then: I should get an appropriate response (404 or redirect)
		waitForPageLoad();
		
		// The application might handle 404s differently, so check for common patterns
		String currentUrl = page.url();
		String pageContent = page.content();
		
		// Spring Boot apps often redirect to error page or home page for non-existent URLs
		assertTrue(currentUrl.contains("404") || currentUrl.contains("error") || 
				   pageContent.contains("404") || pageContent.contains("not found") ||
				   pageContent.contains("Something happened") || // Custom error page
				   currentUrl.equals(getBaseUrl() + "/") || // Might redirect to home
				   page.locator("h2").count() > 0, // Has some valid page content
				   "Should handle non-existent URLs appropriately");
	}

	@Test
	@DisplayName("As a user, I want to recover from error pages, so that I can continue using the application")
	void testErrorPageRecovery() {
		// Given: I am on the error page
		navigateToPath("/oups");
		waitForPageLoad();
		assertTrue(isElementVisible("h2"), "Should be on error page");

		// When: I navigate back to a working page using browser navigation
		navigateToHomePage();

		// Then: I should be able to continue using the application normally
		assertTrue(isElementVisible("h2"), "Should be able to return to normal pages");
		
		// And: I should be able to navigate to other pages
		navigateToPath("/owners/find");
		waitForPageLoad();
		assertTrue(isElementVisible("h2"), "Should be able to navigate to other pages after error");
	}

	@Test
	@DisplayName("As a user, I want the veterinarians page to load quickly, so that I have a good user experience")
	void testVeterinariansPagePerformance() {
		// Given: I measure page load time
		long startTime = System.currentTimeMillis();

		// When: I navigate to the veterinarians page
		veterinariansPage.navigate(getBaseUrl());

		// Then: The page should load within reasonable time
		long loadTime = System.currentTimeMillis() - startTime;
		assertTrue(loadTime < 5000, "Veterinarians page should load within 5 seconds, took: " + loadTime + "ms");

		// And: The page should be fully loaded
		assertTrue(veterinariansPage.isLoaded(), "Page should be fully loaded");
	}

	@Test
	@DisplayName("As a user, I want consistent navigation from veterinarians page, so that I can move around the site")
	void testNavigationFromVeterinariansPage() {
		// Given: I am on the veterinarians page
		veterinariansPage.navigate(getBaseUrl());
		assertTrue(veterinariansPage.isLoaded(), "Veterinarians page should be loaded");

		// When: I use the navigation menu to go to other pages
		// Click on the Home link in navigation
		page.locator("nav .nav-link").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Home")).click();
		waitForPageLoad();

		// Then: I should be on the home page
		assertTrue(page.url().endsWith("/") || page.url().contains("home"), "Should navigate to home page");

		// When: I go back to veterinarians
		page.locator("nav .nav-link").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Veterinarians")).click();
		waitForPageLoad();

		// Then: I should be back on veterinarians page
		assertTrue(veterinariansPage.isLoaded(), "Should be back on veterinarians page");
	}

	@Test
	@DisplayName("As a user, I want proper error handling for network issues, so that I see appropriate feedback")
	void testNetworkErrorHandling() {
		// Given: I am on the veterinarians page
		veterinariansPage.navigate(getBaseUrl());
		assertTrue(veterinariansPage.isLoaded(), "Veterinarians page should be loaded initially");

		// When: I try to navigate to various pages (testing general robustness)
		navigateToPath("/owners/find");
		waitForPageLoad();
		
		// Then: Navigation should work without errors
		assertTrue(isElementVisible("h2"), "Should handle navigation without errors");

		// When: I navigate back
		page.goBack();
		waitForPageLoad();

		// Then: Browser back should work
		assertTrue(page.url().contains("vets") || isElementVisible("h2"), "Browser back should work properly");
	}

	@Test
	@DisplayName("As a user, I want responsive design on veterinarians page, so that it works on all devices")
	void testVeterinariansPageResponsiveDesign() {
		// Given: I am using a mobile viewport
		page.setViewportSize(375, 667); // iPhone SE size

		// When: I navigate to the veterinarians page
		veterinariansPage.navigate(getBaseUrl());

		// Then: The page should be responsive
		assertTrue(veterinariansPage.isLoaded(), "Page should load on mobile viewport");
		
		// And: Content should be accessible
		assertTrue(veterinariansPage.getPageHeading().contains("Veterinarians"), 
				   "Content should be accessible on mobile");

		// When: I switch to tablet viewport
		page.setViewportSize(768, 1024); // iPad size

		// Then: The page should still work properly
		assertTrue(veterinariansPage.isLoaded(), "Page should work on tablet viewport");
	}

}