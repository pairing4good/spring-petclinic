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
 * Page Object Model for the Error page
 */
public class ErrorPage extends BasePage {

	public ErrorPage(Page page) {
		super(page);
	}

	// Page elements
	private Locator errorHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Something happened"));
	}

	private Locator errorMessage() {
		return page.locator("p")
			.filter(new Locator.FilterOptions()
				.setHasText("Expected: controller used to showcase what happens when an exception is thrown"));
	}

	private Locator errorImage() {
		return page.locator("img").first();
	}

	// Verification methods
	public boolean isErrorHeadingVisible() {
		return errorHeading().isVisible();
	}

	public String getErrorHeadingText() {
		return errorHeading().textContent();
	}

	public boolean isErrorMessageVisible() {
		return errorMessage().isVisible();
	}

	public String getErrorMessageText() {
		return errorMessage().textContent();
	}

	public boolean isErrorImageVisible() {
		return errorImage().isVisible();
	}

	// Verification that this is the expected error page (not a generic 404)
	public boolean isExpectedErrorPage() {
		return isErrorHeadingVisible() && isErrorMessageVisible()
				&& getErrorMessageText().contains("Expected: controller used to showcase");
	}

}