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
 * Page Object Model for the Error page. Handles the custom error page that demonstrates
 * exception handling.
 */
public class ErrorPage extends BasePage {

	// Locators for error page elements
	private static final String ERROR_HEADING_SELECTOR = "h2";

	private static final String ERROR_MESSAGE_SELECTOR = "p";

	private static final String SOMETHING_HAPPENED_TEXT = "Something happened...";

	private static final String CONTROLLER_TEXT = "controller";

	private static final String EXCEPTION_TEXT = "exception";

	public ErrorPage(Page page) {
		super(page);
	}

	/**
	 * Check if the error heading is visible
	 */
	public boolean isErrorHeadingVisible() {
		return isElementVisible(ERROR_HEADING_SELECTOR);
	}

	/**
	 * Get the error heading text
	 */
	public String getErrorHeadingText() {
		return page.locator(ERROR_HEADING_SELECTOR).textContent();
	}

	/**
	 * Get the error message text
	 */
	public String getErrorMessageText() {
		return page.locator(ERROR_MESSAGE_SELECTOR).textContent();
	}

	/**
	 * Check if the expected error message is displayed
	 */
	public boolean hasExpectedErrorMessage() {
		String headingText = getErrorHeadingText();
		// The main requirement is that it shows "Something happened..." and is the custom
		// error page
		return headingText.contains(SOMETHING_HAPPENED_TEXT);
	}

	/**
	 * Verify this is the error page
	 */
	public boolean isErrorPage() {
		return getCurrentUrl().contains("/oups") && isErrorHeadingVisible() && hasExpectedErrorMessage();
	}

	/**
	 * Check if this is NOT the whitelabel error page
	 */
	public boolean isNotWhitelabelErrorPage() {
		String pageContent = page.content();
		return !pageContent.contains("Whitelabel Error Page")
				&& !pageContent.contains("This application has no explicit mapping for");
	}

	/**
	 * Navigate back to home from error page
	 */
	public HomePage navigateToHome() {
		clickNavigationItem("Home");
		return new HomePage(page);
	}

}