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

/**
 * Base Page Object Model class containing common page elements and methods.
 */
public abstract class BasePage {

	protected final Page page;

	public BasePage(Page page) {
		this.page = page;
	}

	// Navigation elements
	private Locator homeLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
	}

	private Locator findOwnersLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find Owners"));
	}

	private Locator veterinariansLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians"));
	}

	private Locator errorLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error"));
	}

	// Navigation methods
	public HomePage navigateToHome() {
		homeLink().click();
		return new HomePage(page);
	}

	public FindOwnersPage navigateToFindOwners() {
		findOwnersLink().click();
		return new FindOwnersPage(page);
	}

	public VeterinariansPage navigateToVeterinarians() {
		veterinariansLink().click();
		return new VeterinariansPage(page);
	}

	public ErrorPage navigateToError() {
		errorLink().click();
		return new ErrorPage(page);
	}

	// Common verification methods
	public String getPageTitle() {
		return page.title();
	}

	public boolean isPageTitleCorrect() {
		return "PetClinic :: a Spring Framework demonstration".equals(getPageTitle());
	}

	// Wait for page to be loaded
	public void waitForPageLoad() {
		page.waitForLoadState();
	}

}