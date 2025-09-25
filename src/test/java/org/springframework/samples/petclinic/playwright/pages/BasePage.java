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
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Base page class containing common functionality for all pages.
 *
 * @author Copilot
 */
public abstract class BasePage {

	protected final Page page;

	protected final String baseUrl;

	public BasePage(Page page, String baseUrl) {
		this.page = page;
		this.baseUrl = baseUrl;
	}

	/**
	 * Navigate to a specific path on the base URL.
	 * @param path the path to navigate to
	 */
	public void navigateTo(String path) {
		page.navigate(baseUrl + path);
	}

	/**
	 * Get the current page title.
	 * @return page title
	 */
	public String getTitle() {
		return page.title();
	}

	/**
	 * Get the current page URL.
	 * @return current URL
	 */
	public String getCurrentUrl() {
		return page.url();
	}

	/**
	 * Wait for a locator to be visible on the page.
	 * @param locator the locator to wait for
	 */
	public void waitForVisible(Locator locator) {
		locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
	}

	/**
	 * Check if an element is visible on the page.
	 * @param locator the locator to check
	 * @return true if visible, false otherwise
	 */
	public boolean isVisible(Locator locator) {
		return locator.isVisible();
	}

	/**
	 * Click on an element with disambiguation support.
	 * @param locator the locator to click - should be unambiguous
	 */
	public void click(Locator locator) {
		waitForVisible(locator);
		locator.click();
	}

	/**
	 * Fill text into an input field.
	 * @param locator the input field locator
	 * @param text the text to fill
	 */
	public void fill(Locator locator, String text) {
		waitForVisible(locator);
		locator.fill(text);
	}

	/**
	 * Get text content from an element.
	 * @param locator the element locator
	 * @return text content
	 */
	public String getText(Locator locator) {
		waitForVisible(locator);
		return locator.textContent();
	}

	/**
	 * Navigation helper - click on navigation link by text. Uses specific navigation
	 * structure for disambiguation.
	 * @param linkText the text of the navigation link
	 */
	public void clickNavigationLink(String linkText) {
		// Use specific navigation selector to avoid ambiguity
		Locator navLink = page.locator("nav .navbar-nav")
			.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(linkText));
		click(navLink);
	}

}