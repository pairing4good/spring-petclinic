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
 * Page Object Model for the Create Visit page.
 */
public class CreateVisitPage extends BasePage {

	private final Locator visitHeading;

	private final Locator dateInput;

	private final Locator descriptionInput;

	private final Locator submitButton;

	public CreateVisitPage(Page page) {
		super(page);
		this.visitHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("New Visit"));
		this.dateInput = page.locator("input[name='date']");
		this.descriptionInput = page.locator("input[name='description']");
		this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Visit"));
	}

	/**
	 * Check if this is the Create Visit page
	 */
	public boolean isCreateVisitPage() {
		return visitHeading.isVisible() && submitButton.isVisible();
	}

	/**
	 * Fill visit information
	 */
	public CreateVisitPage fillVisitInfo(String date, String description) {
		dateInput.fill(date);
		descriptionInput.fill(description);
		return this;
	}

	/**
	 * Submit the form
	 */
	public OwnerDetailsPage submitForm() {
		submitButton.click();
		return new OwnerDetailsPage(page);
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		visitHeading.waitFor();
	}

}