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
package org.springframework.samples.petclinic.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object Model for the PetClinic home page.
 *
 * @author Copilot
 */
public class HomePage extends BasePage {

	// Unique locators for home page elements
	private final Locator welcomeImage;

	private final Locator welcomeHeading;

	private final Locator findOwnersLink;

	private final Locator veterinariansLink;

	private final Locator errorPageLink;

	public HomePage(Page page, String baseUrl) {
		super(page, baseUrl);
		// Use specific locators to avoid ambiguity
		this.welcomeImage = page.locator("img[src*='pets.png']");
		this.welcomeHeading = page.locator("h2").first(); // First h2 on home page
		this.findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find owners"));
		this.veterinariansLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
		this.errorPageLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error"));
	}

	/**
	 * Navigate to the home page.
	 */
	public void navigateToHome() {
		navigateTo("/");
	}

	/**
	 * Check if the welcome image is displayed.
	 * @return true if welcome image is visible
	 */
	public boolean isWelcomeImageDisplayed() {
		return isVisible(welcomeImage);
	}

	/**
	 * Get the welcome heading text.
	 * @return welcome heading text
	 */
	public String getWelcomeHeading() {
		return getText(welcomeHeading);
	}

	/**
	 * Click on "Find owners" navigation link.
	 * @return OwnersPage for chaining
	 */
	public OwnersPage clickFindOwners() {
		click(findOwnersLink);
		return new OwnersPage(page, baseUrl);
	}

	/**
	 * Click on "Veterinarians" navigation link.
	 * @return VetsPage for chaining
	 */
	public VetsPage clickVeterinarians() {
		click(veterinariansLink);
		return new VetsPage(page, baseUrl);
	}

	/**
	 * Click on "Error" navigation link to trigger error page.
	 * @return ErrorPage for chaining
	 */
	public ErrorPage clickErrorLink() {
		click(errorPageLink);
		return new ErrorPage(page, baseUrl);
	}

	/**
	 * Verify that the home page is properly loaded.
	 * @return true if home page elements are present
	 */
	public boolean isHomePageLoaded() {
		return isVisible(welcomeImage) && isVisible(welcomeHeading);
	}

}