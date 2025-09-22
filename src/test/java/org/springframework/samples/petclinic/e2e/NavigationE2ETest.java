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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * As a user, I want to navigate through the application, so that I can access all
 * features
 */
@DisplayName("Navigation and Routing E2E Tests")
class NavigationE2ETest extends BaseE2ETest {

	@Test
	@DisplayName("As a user, I want to access the home page, so that I can see the welcome message")
	void testHomePageAccessAndContent() {
		navigateToHome();

		assertPageTitle("PetClinic :: a Spring Framework demonstration");
		assertElementVisible("h2");
		assertElementContainsText("h2", "Welcome");
		assertElementVisible("img[src*='pets.png']");
		assertElementVisible("img[alt='VMware Tanzu Logo']");
	}

	@Test
	@DisplayName("As a user, I want to navigate using the main menu, so that I can access all sections")
	void testMainNavigationMenu() {
		navigateToHome();

		// Verify all navigation links are present and visible
		assertElementVisible("a[href='/']");
		assertElementVisible("a[href='/owners/find']");
		assertElementVisible("a[href='/vets.html']");
		assertElementVisible("a[href='/oups']");

		// Test navigation to Find Owners
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();
		assertUrlContains("/owners/find");
		assertElementContainsText("h2", "Find Owners");

		// Test navigation to Veterinarians
		page.locator("a[href='/vets.html']").click();
		waitForPageLoad();
		assertUrlContains("/vets.html");
		assertElementContainsText("h2", "Veterinarians");

		// Test navigation back to Home
		page.locator("a[href='/']").first().click();
		waitForPageLoad();
		assertUrlContains("/");
		assertElementContainsText("h2", "Welcome");
	}

	@Test
	@DisplayName("As a user, I want to use browser back/forward buttons, so that I can navigate efficiently")
	void testBrowserBackForwardNavigation() {
		navigateToHome();

		// Navigate to Find Owners
		page.locator("a[href='/owners/find']").click();
		waitForPageLoad();
		assertUrlContains("/owners/find");

		// Navigate to Veterinarians
		page.locator("a[href='/vets.html']").click();
		waitForPageLoad();
		assertUrlContains("/vets.html");

		// Use browser back button
		page.goBack();
		waitForPageLoad();
		assertUrlContains("/owners/find");
		assertElementContainsText("h2", "Find Owners");

		// Use browser forward button
		page.goForward();
		waitForPageLoad();
		assertUrlContains("/vets.html");
		assertElementContainsText("h2", "Veterinarians");

		// Go back to home
		page.goBack();
		page.goBack();
		waitForPageLoad();
		assertUrlContains("/");
		assertElementContainsText("h2", "Welcome");
	}

	@Test
	@DisplayName("As a user, I want consistent navigation on all pages, so that I can always access the menu")
	void testConsistentNavigationAcrossPages() {
		String[] testUrls = { "/", "/owners/find", "/vets.html", "/owners/new" };

		for (String url : testUrls) {
			page.navigate(baseUrl + url);
			waitForPageLoad();

			// Verify navigation menu is present on every page
			assertElementVisible("nav");
			assertElementVisible("a[href='/']");
			assertElementVisible("a[href='/owners/find']");
			assertElementVisible("a[href='/vets.html']");
			assertElementVisible("a[href='/oups']");

			// Verify VMware Tanzu logo is present
			assertElementVisible("img[alt='VMware Tanzu Logo']");
		}
	}

	@Test
	@DisplayName("As a user, I want the logo to link to home page, so that I can quickly return to the start")
	void testLogoLinksToHomePage() {
		navigateToFindOwners();

		// Click on the logo/brand link
		page.locator("nav a[href='/']").first().click();
		waitForPageLoad();

		assertUrlContains("/");
		assertElementContainsText("h2", "Welcome");
	}

	@Test
	@DisplayName("As a user, I want proper page titles, so that I can identify pages in browser tabs")
	void testPageTitles() {
		// Test home page title
		navigateToHome();
		assertPageTitle("PetClinic :: a Spring Framework demonstration");

		// Test find owners page title
		navigateToFindOwners();
		assertPageTitle("PetClinic :: a Spring Framework demonstration");

		// Test veterinarians page title
		navigateToVeterinarians();
		assertPageTitle("PetClinic :: a Spring Framework demonstration");

		// Test add owner page title
		navigateToAddOwner();
		assertPageTitle("PetClinic :: a Spring Framework demonstration");
	}

	@Test
	@DisplayName("As a user, I want fast page loading, so that I can navigate efficiently")
	void testPageLoadPerformance() {
		long startTime = System.currentTimeMillis();
		navigateToHome();
		long loadTime = System.currentTimeMillis() - startTime;

		// Page should load within 5 seconds
		assert loadTime < 5000 : "Home page took too long to load: " + loadTime + "ms";

		startTime = System.currentTimeMillis();
		navigateToFindOwners();
		loadTime = System.currentTimeMillis() - startTime;

		// Find owners page should load within 5 seconds
		assert loadTime < 5000 : "Find owners page took too long to load: " + loadTime + "ms";
	}

}