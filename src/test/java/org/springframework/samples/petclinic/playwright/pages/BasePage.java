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
import com.microsoft.playwright.options.LoadState;

/**
 * Base page class for Page Object Model pattern implementation. Contains common
 * functionality shared across all pages.
 */
public abstract class BasePage {

	protected final Page page;

	public BasePage(Page page) {
		this.page = page;
	}

	/**
	 * Wait for the page to fully load
	 */
	public void waitForPageLoad() {
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Get the page title
	 */
	public String getTitle() {
		return page.title();
	}

	/**
	 * Get the current URL
	 */
	public String getCurrentUrl() {
		return page.url();
	}

	/**
	 * Check if an element is visible using a specific locator
	 */
	public boolean isElementVisible(String locator) {
		return page.isVisible(locator);
	}

	/**
	 * Click on navigation menu item by text
	 */
	public void clickNavigationItem(String itemText) {
		// Using specific navigation structure - nav.navbar contains menu items
		page.locator("nav.navbar").locator("text=" + itemText).click();
		waitForPageLoad();
	}

	/**
	 * Verify navigation bar is present
	 */
	public boolean isNavigationBarVisible() {
		return page.locator("nav.navbar").isVisible();
	}

	/**
	 * Get the main navigation menu locator for reliable element identification
	 */
	protected String getNavigationSelector() {
		return "nav.navbar";
	}

}