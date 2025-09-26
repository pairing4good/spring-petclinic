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

/**
 * Page Object Model for the Error page of the Pet Clinic application. Provides methods to
 * verify error page display and content.
 */
public class ErrorPage extends BasePage {

	// Locators for error page elements - using specific, unambiguous selectors
	private static final String ERROR_HEADING = "h2";

	private static final String ERROR_MESSAGE = "p";

	private static final String ERROR_IMAGE = "img[src*='pets.png']";

	public ErrorPage(String baseUrl) {
		super(baseUrl);
		navigateTo("/oups");
	}

	/**
	 * Verify we're on the Error page by checking the heading
	 */
	public boolean isErrorPageDisplayed() {
		try {
			return page.locator(ERROR_HEADING).textContent().trim().equals("Something happened...");
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the error heading text
	 */
	public String getErrorHeading() {
		return page.locator(ERROR_HEADING).textContent().trim();
	}

	/**
	 * Get the error message text
	 */
	public String getErrorMessage() {
		return page.locator(ERROR_MESSAGE).textContent().trim();
	}

	/**
	 * Check if the error image is displayed
	 */
	public boolean isErrorImageDisplayed() {
		return page.locator(ERROR_IMAGE).isVisible();
	}

	/**
	 * Verify this is a custom error page (not the default whitelabel error page)
	 */
	public boolean isCustomErrorPage() {
		try {
			// Check for custom error page elements
			boolean hasCustomHeading = isErrorPageDisplayed();
			boolean hasErrorImage = isErrorImageDisplayed();

			// Ensure it's not the whitelabel error page
			boolean isNotWhitelabel = !page.content().contains("Whitelabel Error Page")
					&& !page.content().contains("This application has no explicit mapping for");

			return hasCustomHeading && hasErrorImage && isNotWhitelabel;
		}
		catch (Exception e) {
			return false;
		}
	}

}