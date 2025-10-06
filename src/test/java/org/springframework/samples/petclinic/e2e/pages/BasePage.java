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
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.AriaRole;

/**
 * Base Page Object Model class providing common functionality for all pages.
 */
public abstract class BasePage {

	protected final Page page;

	// Common navigation elements
	protected final Locator homeLink;

	protected final Locator findOwnersLink;

	protected final Locator veterinariansLink;

	protected final Locator errorLink;

	protected final Locator logo;

	public BasePage(Page page) {
		this.page = page;
		this.homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Home"));
		this.findOwnersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Find Owners"));
		this.veterinariansLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Veterinarians"));
		this.errorLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Error"));
		this.logo = page.locator("img[alt='VMware Tanzu Logo']");
	}

	/**
	 * Navigate to home page using navigation link
	 */
	public HomePage navigateToHome() {
		homeLink.click();
		return new HomePage(page);
	}

	/**
	 * Navigate to Find Owners page using navigation link
	 */
	public FindOwnersPage navigateToFindOwners() {
		findOwnersLink.click();
		return new FindOwnersPage(page);
	}

	/**
	 * Navigate to Veterinarians page using navigation link
	 */
	public VeterinariansPage navigateToVeterinarians() {
		veterinariansLink.click();
		return new VeterinariansPage(page);
	}

	/**
	 * Navigate to Error page using navigation link
	 */
	public ErrorPage navigateToError() {
		errorLink.click();
		return new ErrorPage(page);
	}

	/**
	 * Get the page title
	 */
	public String getTitle() {
		return page.title();
	}

	/**
	 * Check if the navigation is present
	 */
	public boolean isNavigationPresent() {
		return homeLink.isVisible() && findOwnersLink.isVisible() && veterinariansLink.isVisible()
				&& errorLink.isVisible();
	}

	/**
	 * Check if the logo is present
	 */
	public boolean isLogoPresent() {
		return logo.isVisible();
	}

	/**
	 * Wait for the page to be loaded (override in subclasses if needed)
	 */
	public void waitForPageLoad() {
		page.waitForLoadState();
	}

}