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

import com.microsoft.playwright.Page;

/**
 * Page Object for the Home/Welcome page. Contains locators and actions specific to the
 * homepage.
 */
public class HomePage extends BasePage {

	// Specific locators for Home page elements using data-testid or unique identifiers
	private static final String WELCOME_HEADING = "h2:has-text('Welcome')";

	private static final String PET_IMAGE = "img[src*='pets.png']";

	private static final String SPRING_LOGO = "img[alt='VMware Tanzu Logo']";

	public HomePage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Navigate to the home page
	 */
	public HomePage open() {
		navigateTo("/");
		waitForPageLoad();
		return this;
	}

	/**
	 * Wait for home page specific elements to load
	 */
	public void waitForPageLoad() {
		waitForElement(WELCOME_HEADING);
		waitForElement(PET_IMAGE);
	}

	/**
	 * Get the welcome message text
	 */
	public String getWelcomeMessage() {
		return page.locator(WELCOME_HEADING).textContent();
	}

	/**
	 * Check if the pets image is visible
	 */
	public boolean isPetsImageVisible() {
		return page.locator(PET_IMAGE).isVisible();
	}

	/**
	 * Check if the Spring logo is visible
	 */
	public boolean isSpringLogoVisible() {
		return page.locator(SPRING_LOGO).isVisible();
	}

	/**
	 * Navigate to Find Owners page using navigation menu
	 */
	public FindOwnersPage goToFindOwners() {
		clickNavigationItem("Find Owners");
		return new FindOwnersPage(page, baseUrl);
	}

	/**
	 * Navigate to Veterinarians page using navigation menu
	 */
	public VeterinariansPage goToVeterinarians() {
		clickNavigationItem("Veterinarians");
		return new VeterinariansPage(page, baseUrl);
	}

	/**
	 * Navigate to Error page using navigation menu
	 */
	public ErrorPage goToErrorPage() {
		clickNavigationItem("Error");
		return new ErrorPage(page, baseUrl);
	}

}