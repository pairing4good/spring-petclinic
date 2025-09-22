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
 * Page Object Model for the Error page.
 */
public class ErrorPage extends BasePage {

	private final Locator errorHeading;

	private final Locator errorImage;

	public ErrorPage(Page page) {
		super(page);
		this.errorHeading = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Something happened..."));
		this.errorImage = page.locator("img").first();
	}

	/**
	 * Check if this is the Error page
	 */
	public boolean isErrorPage() {
		return errorHeading.isVisible();
	}

	/**
	 * Get the error heading text
	 */
	public String getErrorHeadingText() {
		return errorHeading.textContent();
	}

	/**
	 * Check if the error image is displayed
	 */
	public boolean isErrorImageDisplayed() {
		return errorImage.isVisible();
	}

	/**
	 * Verify that the page shows a 500 error
	 */
	public boolean isInternalServerError() {
		// The error page is triggered by /oups endpoint which causes a 500 error
		return page.url().contains("/oups") && isErrorPage();
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		errorHeading.waitFor();
	}

}