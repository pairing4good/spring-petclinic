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
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Base Page Object containing common functionality for all pages. Implements the Page
 * Object Model pattern for maintainable E2E tests.
 */
public abstract class BasePage {

	protected final Page page;

	protected final String baseUrl;

	public BasePage(Page page, String baseUrl) {
		this.page = page;
		this.baseUrl = baseUrl;
	}

	/**
	 * Navigate to a specific path relative to the base URL
	 */
	public void navigateTo(String path) {
		page.navigate(baseUrl + path);
	}

	/**
	 * Get page title
	 */
	public String getTitle() {
		return page.title();
	}

	/**
	 * Wait for element to be visible with specific timeout
	 */
	public void waitForElement(String selector, int timeoutMs) {
		page.waitForSelector(selector,
				new Page.WaitForSelectorOptions().setTimeout(timeoutMs).setState(WaitForSelectorState.VISIBLE));
	}

	/**
	 * Wait for element to be visible with default timeout
	 */
	public void waitForElement(String selector) {
		waitForElement(selector, 10000);
	}

	/**
	 * Click on navigation menu item using specific locator strategy
	 */
	public void clickNavigationItem(String linkText) {
		// Use filter to find the specific navigation link to avoid ambiguity
		page.locator("nav ul.navbar-nav li a").filter(new Locator.FilterOptions().setHasText(linkText)).click();
	}

	/**
	 * Verify page has loaded by checking for common elements
	 */
	public boolean isPageLoaded() {
		try {
			// Check for navigation bar - present on all pages
			waitForElement("nav.navbar", 5000);
			// Check for main content container - present on all pages
			waitForElement(".container-fluid .container", 5000);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get current URL
	 */
	public String getCurrentUrl() {
		return page.url();
	}

}