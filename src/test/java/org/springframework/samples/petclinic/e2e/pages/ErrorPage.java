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

/**
 * Page Object for the Error page.
 */
public class ErrorPage extends BasePage {

	private static final String ERROR_HEADING = "h2:has-text('Something happened...')";

	public ErrorPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	public void waitForPageLoad() {
		waitForElement(ERROR_HEADING);
	}

	public String getErrorMessage() {
		return page.locator(ERROR_HEADING).textContent();
	}

}