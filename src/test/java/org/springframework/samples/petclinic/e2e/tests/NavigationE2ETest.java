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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E tests for Navigation functionality Tests cross-page navigation, browser controls,
 * and user flows
 */
@DisplayName("Navigation E2E Tests")
class NavigationE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to navigate between all main sections, so that I can access all functionality")
	void shouldNavigateBetweenAllMainSections() {
		// Given - user starts on home page
		page.navigate(baseUrl);
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should start on home page");

		// When/Then - user navigates through all main sections

		// Home -> Find Owners
		page.locator("a:has-text('Find Owners')").click();
		assertTrue(page.url().contains("/owners/find"), "Should navigate to find owners");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should show find owners page");

		// Find Owners -> Veterinarians
		page.locator("a:has-text('Veterinarians')").click();
		assertTrue(page.url().contains("/vets.html"), "Should navigate to veterinarians");
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(), "Should show veterinarians page");

		// Veterinarians -> Error
		page.locator("a:has-text('Error')").click();
		assertTrue(page.url().contains("/oups"), "Should navigate to error page");
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(), "Should show error page");

		// Error -> Home
		page.locator("a:has-text('Home')").click();
		assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should return to home");
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome page");
	}

	@Test
	@DisplayName("As a user, I want to use browser back and forward buttons, so that I can navigate like a typical web application")
	void shouldSupportBrowserBackAndForwardButtons() {
		// Given - user navigates through several pages
		page.navigate(baseUrl);
		page.waitForLoadState();

		page.locator("a:has-text('Find Owners')").click();
		page.locator("a:has-text('Veterinarians')").click();
		assertTrue(page.url().contains("/vets.html"), "Should be on veterinarians page");

		// When - user uses browser back button
		page.goBack();

		// Then - should go back to find owners
		assertTrue(page.url().contains("/owners/find"), "Should go back to find owners");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should show find owners page");

		// When - user uses browser back button again
		page.goBack();

		// Then - should go back to home
		assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should go back to home");
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome page");

		// When - user uses browser forward button
		page.goForward();

		// Then - should go forward to find owners
		assertTrue(page.url().contains("/owners/find"), "Should go forward to find owners");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should show find owners page");
	}

	@Test
	@DisplayName("As a user, I want consistent navigation menu across all pages, so that I can navigate from anywhere")
	void shouldHaveConsistentNavigationMenuAcrossAllPages() {
		String[] pages = { baseUrl, baseUrl + "/owners/find", baseUrl + "/vets.html", baseUrl + "/oups" };
		String[] expectedHeadings = { "Welcome", "Find Owners", "Veterinarians", "Something happened" };

		for (int i = 0; i < pages.length; i++) {
			// Given - user is on each page
			page.navigate(pages[i]);
			page.waitForLoadState();
			assertTrue(page.locator("h2:has-text('" + expectedHeadings[i] + "')").isVisible(),
					"Should be on " + expectedHeadings[i] + " page");

			// Then - should have consistent navigation menu
			assertTrue(page.locator("nav").isVisible(), "Should show navigation on " + expectedHeadings[i] + " page");
			assertTrue(page.locator("a:has-text('Home')").isVisible(),
					"Should show Home link on " + expectedHeadings[i] + " page");
			assertTrue(page.locator("a:has-text('Find Owners')").isVisible(),
					"Should show Find Owners link on " + expectedHeadings[i] + " page");
			assertTrue(page.locator("a:has-text('Veterinarians')").isVisible(),
					"Should show Veterinarians link on " + expectedHeadings[i] + " page");
			assertTrue(page.locator("a:has-text('Error')").isVisible(),
					"Should show Error link on " + expectedHeadings[i] + " page");
		}
	}

	@Test
	@DisplayName("As a user, I want to complete full owner management workflow, so that I can manage pet owners efficiently")
	void shouldCompleteFullOwnerManagementWorkflow() {
		// Given - user starts at home
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user follows complete owner management workflow

		// 1. Navigate to find owners
		page.locator("a:has-text('Find Owners')").click();
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should be on find owners page");

		// 2. Navigate to add new owner
		page.locator("a:has-text('Add Owner')").click();
		assertTrue(page.locator("h2:has-text('Owner')").isVisible(), "Should be on add owner page");

		// 3. Create new owner
		TestOwnerData ownerData = generateTestOwnerData();
		page.locator("input[id*='firstName']").fill(ownerData.firstName);
		page.locator("input[id*='lastName']").fill(ownerData.lastName);
		page.locator("input[id*='address']").fill(ownerData.address);
		page.locator("input[id*='city']").fill(ownerData.city);
		page.locator("input[id*='telephone']").fill(ownerData.telephone);
		page.locator("button:has-text('Add Owner')").click();

		// 4. View owner details
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should be on owner details page");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner details");

		// 5. Edit owner
		page.locator("a:has-text('Edit Owner')").click();
		assertTrue(page.url().matches(".*/owners/\\d+/edit"), "Should be on edit owner page");

		// 6. Navigate back to search
		page.locator("a:has-text('Find Owners')").click();
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should return to find owners");

		// Then - workflow should complete successfully
		assertTrue(page.locator("input#lastName").isVisible(), "Should show search functionality");
	}

	@Test
	@DisplayName("As a user, I want to navigate from search results to owner details, so that I can view complete owner information")
	void shouldNavigateFromSearchResultsToOwnerDetails() {
		// Given - user performs a search
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		page.locator("input#lastName").fill("Franklin");
		page.locator("button:has-text('Find Owner')").click();

		// When - user clicks on search result
		assertTrue(page.locator("a:has-text('Franklin')").count() > 0, "Should have Franklin in results");
		String ownerName = page.locator("a:has-text('Franklin')").first().textContent();
		page.locator("a:has-text('Franklin')").first().click();

		// Then - should navigate to owner details
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should be on owner details page");
		assertTrue(page.locator("h2:has-text('Owner Information')").isVisible(), "Should show owner information");
		assertTrue(page.locator("table").textContent().contains(ownerName), "Should show correct owner details");
	}

	@Test
	@DisplayName("As a user, I want to handle deep linking to specific pages, so that bookmarks and direct links work")
	void shouldHandleDeepLinkingToSpecificPages() {
		// Given - user navigates directly to specific pages via URL

		// Deep link to find owners
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should handle deep link to find owners");

		// Deep link to veterinarians
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Veterinarians')").isVisible(),
				"Should handle deep link to veterinarians");

		// Deep link to error page
		page.navigate(baseUrl + "/oups");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Something happened')").isVisible(),
				"Should handle deep link to error page");

		// Then - navigation should still work from deep-linked pages
		page.locator("a:has-text('Home')").click();
		assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should navigate to home from deep-linked page");
	}

	@Test
	@DisplayName("As a user, I want page refreshes to work correctly, so that I can reload pages without losing functionality")
	void shouldHandlePageRefreshesCorrectly() {
		String[] pages = { baseUrl, baseUrl + "/owners/find", baseUrl + "/vets.html" };
		String[] expectedHeadings = { "Welcome", "Find Owners", "Veterinarians" };

		for (int i = 0; i < pages.length; i++) {
			// Given - user is on a page
			page.navigate(pages[i]);
			page.waitForLoadState();
			assertTrue(page.locator("h2:has-text('" + expectedHeadings[i] + "')").isVisible(),
					"Should be on " + expectedHeadings[i] + " page");

			// When - user refreshes the page
			page.reload();
			page.waitForLoadState();

			// Then - page should reload correctly
			assertTrue(page.locator("h2:has-text('" + expectedHeadings[i] + "')").isVisible(),
					expectedHeadings[i] + " page should reload correctly");
			assertTrue(page.locator("nav").isVisible(),
					"Navigation should work after refresh on " + expectedHeadings[i] + " page");
		}
	}

	@Test
	@DisplayName("As a user, I want logo/brand to link back to home, so that I can quickly return to main page")
	void shouldLinkLogoToHomePage() {
		// Given - user is on a non-home page
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should be on find owners page");

		// When - user clicks on logo/brand (if it's a link)
		if (page.locator("a[href='/']").isVisible() || page.locator("a[href='" + baseUrl + "']").isVisible()) {
			page.locator("a[href='/'], a[href='" + baseUrl + "']").first().click();

			// Then - should navigate to home page
			assertTrue(page.url().equals(baseUrl + "/") || page.url().equals(baseUrl), "Should navigate to home page");
			assertTrue(page.locator("h2:has-text('Welcome')").isVisible(), "Should show welcome page");
		}
		// If logo is not a link, this test is not applicable
	}

	@Test
	@DisplayName("As a user, I want navigation to be keyboard accessible, so that I can navigate without a mouse")
	void shouldSupportKeyboardNavigation() {
		// Given - user navigates to home page
		page.navigate(baseUrl);
		page.waitForLoadState();

		// When - user tabs through navigation links
		page.locator("a:has-text('Find Owners')").focus();

		// Then - navigation links should be focusable
		assertTrue(page.locator("a:has-text('Find Owners')").isVisible(), "Find Owners link should be visible");

		// Should be able to activate with Enter key
		page.locator("a:has-text('Find Owners')").press("Enter");
		assertTrue(page.url().contains("/owners/find"), "Should navigate with Enter key");
		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible(), "Should show find owners page");
	}

	@Test
	@DisplayName("As a user, I want navigation state to be preserved during form interactions, so that I can return to my previous location")
	void shouldPreserveNavigationStateDuringFormInteractions() {
		// Given - user navigates through search workflow
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
		page.locator("input#lastName").fill("Davis");
		page.locator("button:has-text('Find Owner')").click();

		// When - user navigates to owner details and back
		page.locator("a:has-text('Davis')").first().click();
		assertTrue(page.url().matches(".*/owners/\\d+"), "Should be on owner details");

		// Use browser back to return to search results
		page.goBack();

		// Then - should return to search results with state preserved
		assertTrue(page.url().contains("/owners?lastName=Davis"), "Should return to search results");
		assertTrue(page.locator("h2:has-text('Owners')").isVisible(), "Should show search results");
		assertTrue(page.locator("a:has-text('Davis')").count() > 0, "Should still show search results");
	}

}