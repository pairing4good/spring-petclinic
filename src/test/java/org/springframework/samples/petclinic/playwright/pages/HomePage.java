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
package org.springframework.samples.petclinic.playwright.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object Model for the PetClinic Home Page. Represents the main landing page with
 * navigation and welcome content.
 */
public class HomePage extends BasePage {

	// Specific locators for home page elements
	private static final String SPRING_LOGO_SELECTOR = "img[alt*='Logo']";

	private static final String WELCOME_IMAGE_SELECTOR = "img[src*='pets.png']";

	public HomePage(Page page) {
		super(page);
	}

	/**
	 * Navigate to Find Owners page via navigation menu
	 */
	public FindOwnersPage navigateToFindOwners() {
		clickNavigationItem("Find owners");
		return new FindOwnersPage(page);
	}

	/**
	 * Navigate to veterinarians page via navigation menu
	 */
	public VeterinariansPage navigateToVeterinarians() {
		clickNavigationItem("Veterinarians");
		return new VeterinariansPage(page);
	}

	/**
	 * Navigate to error page via navigation menu
	 */
	public ErrorPage navigateToErrorPage() {
		clickNavigationItem("Error");
		return new ErrorPage(page);
	}

	/**
	 * Navigate back to home via navigation menu or logo
	 */
	public HomePage navigateToHome() {
		clickNavigationItem("Home");
		return this;
	}

	/**
	 * Check if the welcome image is visible
	 */
	public boolean isWelcomeImageVisible() {
		return isElementVisible(WELCOME_IMAGE_SELECTOR);
	}

	/**
	 * Check if the Spring logo is visible
	 */
	public boolean isSpringLogoVisible() {
		return isElementVisible(SPRING_LOGO_SELECTOR);
	}

	/**
	 * Verify this is the home page by checking URL and title
	 */
	public boolean isHomePage() {
		String url = getCurrentUrl();
		String title = getTitle();
		return url.endsWith("/") || url.contains("localhost") && title.contains("PetClinic");
	}

}