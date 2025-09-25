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

/**
 * Page Object Model for error pages (404, 500, etc.).
 *
 * @author Copilot
 */
public class ErrorPage extends BasePage {

	// Error page elements
	private final Locator errorImage;

	private final Locator errorHeading;

	private final Locator errorMessage;

	public ErrorPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Error page specific elements
		this.errorImage = page.locator("img[src*='pets.png']");
		this.errorHeading = page.locator("h2").first(); // Main error heading
		this.errorMessage = page.locator("p").first(); // Error message paragraph
	}

	/**
	 * Navigate to trigger error page.
	 */
	public void navigateToErrorPage() {
		navigateTo("/oups");
	}

	/**
	 * Navigate to a non-existent page to trigger 404.
	 */
	public void navigateTo404Page() {
		navigateTo("/nonexistent-page");
	}

	/**
	 * Get the error heading text.
	 * @return error heading
	 */
	public String getErrorHeading() {
		return getText(errorHeading);
	}

	/**
	 * Get the error message text.
	 * @return error message
	 */
	public String getErrorMessage() {
		return getText(errorMessage);
	}

	/**
	 * Check if the error image is displayed.
	 * @return true if error image is visible
	 */
	public boolean isErrorImageDisplayed() {
		return isVisible(errorImage);
	}

	/**
	 * Check if the error page is properly loaded.
	 * @return true if error elements are present
	 */
	public boolean isErrorPageLoaded() {
		return isVisible(errorHeading) && isVisible(errorImage);
	}

	/**
	 * Check if this is a "Something happened" error page.
	 * @return true if contains "Something happened" text
	 */
	public boolean isSomethingHappenedError() {
		return getErrorHeading().contains("Something happened");
	}

}