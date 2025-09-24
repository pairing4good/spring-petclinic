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
import com.microsoft.playwright.Locator;

/**
 * Base class for all page objects containing common page elements and functionality.
 */
public abstract class BasePage {

	protected final Page page;

	protected final String baseUrl;

	// Common navigation elements
	private final Locator homeLink;

	private final Locator findOwnersLink;

	private final Locator veterinariansLink;

	private final Locator errorLink;

	protected BasePage(Page page, String baseUrl) {
		this.page = page;
		this.baseUrl = baseUrl;
		this.homeLink = page.getByRole("link", new Page.GetByRoleOptions().setName(" Home"));
		this.findOwnersLink = page.getByRole("link", new Page.GetByRoleOptions().setName(" Find Owners"));
		this.veterinariansLink = page.getByRole("link", new Page.GetByRoleOptions().setName(" Veterinarians"));
		this.errorLink = page.getByRole("link", new Page.GetByRoleOptions().setName(" Error"));
	}

	public String getPageTitle() {
		return page.title();
	}

	public void clickHomeLink() {
		homeLink.click();
	}

	public void clickFindOwnersLink() {
		findOwnersLink.click();
	}

	public void clickVeterinariansLink() {
		veterinariansLink.click();
	}

	public void clickErrorLink() {
		errorLink.click();
	}

	public boolean isHomePageDisplayed() {
		return page.url().equals(baseUrl + "/");
	}

	public boolean isFindOwnersPageDisplayed() {
		return page.url().equals(baseUrl + "/owners/find");
	}

	public boolean isVeterinariansPageDisplayed() {
		return page.url().contains("/vets");
	}

	public boolean isErrorPageDisplayed() {
		return page.url().equals(baseUrl + "/oups");
	}

	public void waitForPageLoad() {
		page.waitForLoadState();
	}

}