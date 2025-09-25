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
import com.microsoft.playwright.Page;

/**
 * Page Object Model for the Home/Welcome page.
 * 
 * Provides methods to interact with elements on the home page including:
 * - Navigation verification
 * - Welcome message validation
 * - Navigation menu interactions
 */
public class HomePage {

	private final Page page;

	// Locators using specific, unambiguous selectors
	private final Locator welcomeHeading;

	private final Locator petImage;

	private final Locator homeNavLink;

	private final Locator findOwnersNavLink;

	private final Locator vetsNavLink;

	private final Locator errorNavLink;

	private final Locator springLogo;

	public HomePage(Page page) {
		this.page = page;
		// Using specific CSS selectors and text content for disambiguation
		this.welcomeHeading = page.locator("h2").filter(new Locator.FilterOptions().setHasText("Welcome"));
		this.petImage = page.locator("img[src*='pets.png']");
		this.homeNavLink = page.locator("nav .nav-link").filter(new Locator.FilterOptions().setHasText("Home"));
		this.findOwnersNavLink = page.locator("nav .nav-link").filter(new Locator.FilterOptions().setHasText("Find owners"));
		this.vetsNavLink = page.locator("nav .nav-link").filter(new Locator.FilterOptions().setHasText("Veterinarians"));
		this.errorNavLink = page.locator("nav .nav-link").filter(new Locator.FilterOptions().setHasText("Error"));
		this.springLogo = page.locator("img[alt*='VMware Tanzu Logo']");
	}

	/**
	 * Navigate to the home page.
	 */
	public void navigate(String baseUrl) {
		page.navigate(baseUrl);
		page.waitForLoadState();
	}

	/**
	 * Verify the page is loaded correctly.
	 */
	public boolean isLoaded() {
		return welcomeHeading.isVisible() && petImage.isVisible();
	}

	/**
	 * Get the welcome message text.
	 */
	public String getWelcomeMessage() {
		welcomeHeading.waitFor();
		return welcomeHeading.textContent();
	}

	/**
	 * Verify the page title.
	 */
	public String getPageTitle() {
		return page.title();
	}

	/**
	 * Click on Find Owners navigation link.
	 */
	public void clickFindOwners() {
		findOwnersNavLink.click();
		page.waitForLoadState();
	}

	/**
	 * Click on Veterinarians navigation link.
	 */
	public void clickVeterinarians() {
		vetsNavLink.click();
		page.waitForLoadState();
	}

	/**
	 * Click on Error navigation link.
	 */
	public void clickError() {
		errorNavLink.click();
		page.waitForLoadState();
	}

	/**
	 * Click on Home navigation link.
	 */
	public void clickHome() {
		homeNavLink.click();
		page.waitForLoadState();
	}

	/**
	 * Verify all navigation links are present.
	 */
	public boolean areAllNavigationLinksVisible() {
		return homeNavLink.isVisible() && findOwnersNavLink.isVisible() && vetsNavLink.isVisible()
				&& errorNavLink.isVisible();
	}

	/**
	 * Verify the Spring logo is displayed.
	 */
	public boolean isSpringLogoVisible() {
		return springLogo.isVisible();
	}

	/**
	 * Get the current URL.
	 */
	public String getCurrentUrl() {
		return page.url();
	}

}