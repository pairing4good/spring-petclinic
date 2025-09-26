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
package org.springframework.samples.petclinic.e2e.pages;

import com.microsoft.playwright.Locator;

/**
 * Page Object Model for the Home/Welcome page of the Pet Clinic application. Provides
 * methods to interact with homepage elements and navigate to other sections.
 */
public class HomePage extends BasePage {

	// Locators for home page elements
	private static final String WELCOME_HEADING = "h2";

	private static final String PETS_IMAGE = "img[src*='pets.png']";

	private static final String LOGO_IMAGE = "img[alt='VMware Tanzu Logo']";

	public HomePage(String baseUrl) {
		super(baseUrl);
		navigateTo("/");
	}

	/**
	 * Verify we're on the home page by checking the welcome heading
	 */
	public boolean isWelcomePageDisplayed() {
		try {
			return page.locator(WELCOME_HEADING).textContent().trim().equals("Welcome");
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the welcome heading text
	 */
	public String getWelcomeHeading() {
		return page.locator(WELCOME_HEADING).textContent().trim();
	}

	/**
	 * Check if the pets image is displayed
	 */
	public boolean isPetsImageDisplayed() {
		return page.locator(PETS_IMAGE).isVisible();
	}

	/**
	 * Check if the logo image is displayed
	 */
	public boolean isLogoImageDisplayed() {
		return page.locator(LOGO_IMAGE).isVisible();
	}

	/**
	 * Verify the page title is correct
	 */
	public boolean hasCorrectTitle() {
		return getTitle().equals("PetClinic :: a Spring Framework demonstration");
	}

	// Navigation methods using specific locators with clear disambiguation

	/**
	 * Navigate to Find Owners page using the navigation menu Uses specific href to avoid
	 * ambiguity
	 */
	public FindOwnersPage navigateToFindOwners() {
		page.locator("nav a[href='/owners/find']").click();
		return new FindOwnersPage(baseUrl);
	}

	/**
	 * Navigate to Veterinarians page using the navigation menu Uses specific href to
	 * avoid ambiguity
	 */
	public VeterinariansPage navigateToVeterinarians() {
		page.locator("nav a[href='/vets.html']").click();
		return new VeterinariansPage(baseUrl);
	}

	/**
	 * Navigate to Error page using the navigation menu Uses specific href to avoid
	 * ambiguity
	 */
	public ErrorPage navigateToError() {
		page.locator("nav a[href='/oups']").click();
		return new ErrorPage(baseUrl);
	}

	/**
	 * Get all navigation menu items
	 */
	public Locator getNavigationMenu() {
		return page.locator("nav ul li");
	}

	/**
	 * Verify all main navigation links are present
	 */
	public boolean areAllNavigationLinksPresent() {
		try {
			// Check for Home link
			page.locator("nav a[href='/']").isVisible();
			// Check for Find Owners link
			page.locator("nav a[href='/owners/find']").isVisible();
			// Check for Veterinarians link
			page.locator("nav a[href='/vets.html']").isVisible();
			// Check for Error link
			page.locator("nav a[href='/oups']").isVisible();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}