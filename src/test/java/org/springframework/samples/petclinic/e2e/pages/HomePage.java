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
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object Model for the Home (Welcome) page.
 */
public class HomePage extends BasePage {

	private final Locator welcomeHeading;

	private final Locator petImage;

	public HomePage(Page page) {
		super(page);
		this.welcomeHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome"));
		this.petImage = page.locator("img").first(); // Pet image on the welcome page
	}

	/**
	 * Check if this is the home page by verifying the Welcome heading
	 */
	public boolean isHomePage() {
		return welcomeHeading.isVisible();
	}

	/**
	 * Get the welcome heading text
	 */
	public String getWelcomeText() {
		return welcomeHeading.textContent();
	}

	/**
	 * Check if the pet image is displayed
	 */
	public boolean isPetImageDisplayed() {
		return petImage.isVisible();
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		welcomeHeading.waitFor();
	}

}