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
 * Base page object that provides common functionality for all pages in the PetClinic
 * application
 */
public abstract class BasePage {

	protected final Page page;

	protected static final String BASE_URL = "http://localhost:8080";

	public BasePage(Page page) {
		this.page = page;
	}

	public void navigateTo(String path) {
		page.navigate(BASE_URL + path);
	}

	public String getTitle() {
		return page.title();
	}

	public void waitForPageLoad() {
		page.waitForLoadState();
	}

	public Locator getNavigationLink(String linkText) {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
				new Page.GetByRoleOptions().setName(linkText));
	}

	public void clickNavigationLink(String linkText) {
		getNavigationLink(linkText).click();
	}

	public boolean isDisplayed() {
		try {
			page.waitForLoadState();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public Locator getFlashMessage() {
		return page.locator("[data-testid='flash-message'], .alert, .message");
	}

	public String getFlashMessageText() {
		return getFlashMessage().first().textContent();
	}

	public Locator getErrorMessage() {
		return page.locator("[data-testid='error-message'], .error, .alert-danger");
	}

	public String getCurrentUrl() {
		return page.url();
	}

}