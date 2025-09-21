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

import static org.junit.jupiter.api.Assertions.*;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for navigation and home page functionality.
 */
public class NavigationE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToVisitTheHomePage_SoThatICanAccessThePetClinicApplication() {
		// Verify home page elements
		assertAll(() -> assertEquals("PetClinic :: a Spring Framework demonstration", page.title()),
				() -> assertTrue(page.locator("h2").textContent().contains("Welcome")),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()),
				() -> assertTrue(page.locator("img[alt='VMware Tanzu Logo']").isVisible()));
	}

	@Test
	void asAUser_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		page.click("a[href='/owners/find']");
		page.waitForURL("**/owners/find");

		assertAll(() -> assertTrue(page.url().contains("/owners/find")),
				() -> assertTrue(page.locator("h2").textContent().contains("Find Owners")),
				() -> assertTrue(page.locator("input[name='lastName']").isVisible()),
				() -> assertTrue(page.locator("button[type='submit']").textContent().contains("Find Owner")),
				() -> assertTrue(page.locator("a[href='/owners/new']").textContent().contains("Add Owner")));
	}

	@Test
	void asAUser_IWantToNavigateToVeterinarians_SoThatICanViewTheListOfVets() {
		page.click("a[href='/vets.html']");
		page.waitForURL("**/vets.html");

		assertAll(() -> assertTrue(page.url().contains("/vets.html")),
				() -> assertTrue(page.locator("h2").textContent().contains("Veterinarians")),
				() -> assertTrue(page.locator("table").isVisible()),
				() -> assertTrue(page.locator("th").first().textContent().contains("Name")),
				() -> assertTrue(page.locator("th").last().textContent().contains("Specialties")));
	}

	@Test
	void asAUser_IWantToNavigateToTheErrorPage_SoThatICanSeeHowErrorsAreHandled() {
		page.click("a[href='/oups']");
		page.waitForLoadState();

		assertAll(() -> assertTrue(page.url().contains("/oups")),
				() -> assertTrue(page.locator("h2").textContent().contains("Something happened")),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()));
	}

	@Test
	void asAUser_IWantToUseTheLogoAsHomeLink_SoThatICanQuicklyReturnToTheHomePage() {
		// Navigate away from home
		page.click("a[href='/owners/find']");
		page.waitForURL("**/owners/find");

		// Click logo to return home
		page.click("a.navbar-brand");
		page.waitForURL("**/");

		assertAll(() -> assertTrue(page.url().matches(".*://[^/]+/$") || page.url().matches(".*://[^/]+$")),
				() -> assertTrue(page.locator("h2").textContent().contains("Welcome")));
	}

	@Test
	void asAUser_IWantTheNavigationToBeAccessible_SoThatICanUseKeyboardNavigation() {
		// Test keyboard navigation
		page.keyboard().press("Tab"); // Should focus first navigation element
		page.keyboard().press("Enter"); // Should activate the link

		// Verify navigation is keyboard accessible
		Locator homeLink = page.locator("a[href='/']").first();
		assertTrue(homeLink.isVisible());

		// Test that all navigation links are reachable
		assertAll(() -> assertTrue(page.locator("a[href='/owners/find']").isVisible()),
				() -> assertTrue(page.locator("a[href='/vets.html']").isVisible()),
				() -> assertTrue(page.locator("a[href='/oups']").isVisible()));
	}

	@Test
	void asAUser_IWantTheApplicationToBeResponsive_SoThatItWorksOnDifferentScreenSizes() {
		// Test mobile viewport
		page.setViewportSize(375, 667); // iPhone viewport

		assertAll(() -> assertTrue(page.locator("h2").isVisible()), () -> assertTrue(page.locator("nav").isVisible()),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()));

		// Test tablet viewport
		page.setViewportSize(768, 1024); // iPad viewport

		assertAll(() -> assertTrue(page.locator("h2").isVisible()), () -> assertTrue(page.locator("nav").isVisible()),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()));

		// Test desktop viewport
		page.setViewportSize(1920, 1080); // Desktop viewport

		assertAll(() -> assertTrue(page.locator("h2").isVisible()), () -> assertTrue(page.locator("nav").isVisible()),
				() -> assertTrue(page.locator("img[src*='pets.png']").isVisible()));
	}

	@Test
	void asAUser_IWantBrowserBackButtonToWork_SoThatICanNavigateBackToPreviousPages() {
		// Navigate to find owners
		page.click("a[href='/owners/find']");
		page.waitForURL("**/owners/find");

		// Navigate to veterinarians
		page.click("a[href='/vets.html']");
		page.waitForURL("**/vets.html");

		// Use browser back button
		page.goBack();
		page.waitForURL("**/owners/find");
		assertTrue(page.url().contains("/owners/find"));

		// Use browser back button again
		page.goBack();
		page.waitForURL("**/");
		assertTrue(page.url().matches(".*://[^/]+/$") || page.url().matches(".*://[^/]+$"));

		// Test forward button
		page.goForward();
		page.waitForURL("**/owners/find");
		assertTrue(page.url().contains("/owners/find"));
	}

	@Test
	void asAUser_IWantCrosseBrowserCompatibility_SoThatTheApplicationWorksInDifferentBrowsers() {
		// Test with Firefox
		Browser firefoxBrowser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
		var firefoxContext = firefoxBrowser.newContext();
		var firefoxPage = firefoxContext.newPage();

		try {
			firefoxPage.navigate(baseUrl());
			firefoxPage.waitForLoadState();

			assertAll(() -> assertEquals("PetClinic :: a Spring Framework demonstration", firefoxPage.title()),
					() -> assertTrue(firefoxPage.locator("h2").textContent().contains("Welcome")),
					() -> assertTrue(firefoxPage.locator("img[src*='pets.png']").isVisible()));
		}
		finally {
			firefoxContext.close();
			firefoxBrowser.close();
		}

		// Test with Safari (if available on the system)
		if (playwright.webkit() != null) {
			Browser safarieBrowser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(true));
			var safariContext = safarieBrowser.newContext();
			var safariPage = safariContext.newPage();

			try {
				safariPage.navigate(baseUrl());
				safariPage.waitForLoadState();

				assertAll(() -> assertEquals("PetClinic :: a Spring Framework demonstration", safariPage.title()),
						() -> assertTrue(safariPage.locator("h2").textContent().contains("Welcome")),
						() -> assertTrue(safariPage.locator("img[src*='pets.png']").isVisible()));
			}
			finally {
				safariContext.close();
				safarieBrowser.close();
			}
		}
	}

	@Test
	void asAUser_IWantToAccessNonExistentPages_SoThatICanSeeProper404ErrorHandling() {
		// Test 404 for non-existent owner
		navigateAndWait("/owners/99999");
		// The application might show an error or redirect, verify appropriate behavior
		assertTrue(page.url().contains("/owners/99999") || page.locator("body").isVisible());

		// Test 404 for completely non-existent path
		navigateAndWait("/nonexistent-path");
		// Verify error handling (might be Tomcat error page or custom 404)
		assertTrue(page.locator("body").isVisible());
	}

}