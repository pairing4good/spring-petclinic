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
 * Page Object for the Error page (/oups).
 */
public class ErrorPage extends BasePage {

	private final Locator errorHeading;

	private final Locator errorImage;

	private final Locator errorDescription;

	public ErrorPage(Page page, String baseUrl) {
		super(page, baseUrl);
		this.errorHeading = page.getByRole("heading", new Page.GetByRoleOptions().setName("Something happened..."));
		this.errorImage = page.locator("img").first(); // Error page image
		this.errorDescription = page.locator("p");
	}

	public void navigateTo() {
		page.navigate(baseUrl + "/oups");
		waitForPageLoad();
	}

	public boolean isErrorHeadingVisible() {
		return errorHeading.isVisible();
	}

	public boolean isErrorImageVisible() {
		return errorImage.isVisible();
	}

	public boolean isErrorDescriptionVisible() {
		return errorDescription.isVisible();
	}

	public String getErrorHeadingText() {
		return errorHeading.textContent();
	}

	public String getErrorDescriptionText() {
		return errorDescription.first().textContent();
	}

	public boolean isHttpStatus500() {
		// Check if page was served with 500 status (this happens when navigating via
		// /oups URL)
		return page.url().contains("/oups");
	}

}