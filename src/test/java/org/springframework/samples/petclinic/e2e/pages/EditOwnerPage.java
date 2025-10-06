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
 * Page Object Model for the Edit Owner page.
 */
public class EditOwnerPage extends BasePage {

	private final Locator ownerHeading;

	private final Locator firstNameInput;

	private final Locator lastNameInput;

	private final Locator addressInput;

	private final Locator cityInput;

	private final Locator telephoneInput;

	private final Locator updateButton;

	private final Locator errorMessages;

	public EditOwnerPage(Page page) {
		super(page);
		this.ownerHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Owner"));
		this.firstNameInput = page.locator("input[name='firstName']");
		this.lastNameInput = page.locator("input[name='lastName']");
		this.addressInput = page.locator("input[name='address']");
		this.cityInput = page.locator("input[name='city']");
		this.telephoneInput = page.locator("input[name='telephone']");
		this.updateButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update Owner"));
		this.errorMessages = page.locator(".help-inline");
	}

	/**
	 * Check if this is the Edit Owner page
	 */
	public boolean isEditOwnerPage() {
		return ownerHeading.isVisible() && updateButton.isVisible();
	}

	/**
	 * Get current first name value
	 */
	public String getCurrentFirstName() {
		return firstNameInput.inputValue();
	}

	/**
	 * Get current last name value
	 */
	public String getCurrentLastName() {
		return lastNameInput.inputValue();
	}

	/**
	 * Update owner information
	 */
	public EditOwnerPage updateOwnerInfo(String firstName, String lastName, String address, String city,
			String telephone) {
		firstNameInput.clear();
		firstNameInput.fill(firstName);
		lastNameInput.clear();
		lastNameInput.fill(lastName);
		addressInput.clear();
		addressInput.fill(address);
		cityInput.clear();
		cityInput.fill(city);
		telephoneInput.clear();
		telephoneInput.fill(telephone);
		return this;
	}

	/**
	 * Submit the update form
	 */
	public OwnerDetailsPage submitUpdate() {
		updateButton.click();
		return new OwnerDetailsPage(page);
	}

	/**
	 * Submit form with validation errors (stays on same page)
	 */
	public EditOwnerPage submitUpdateWithErrors() {
		updateButton.click();
		return this;
	}

	/**
	 * Check if validation errors are present
	 */
	public boolean hasValidationErrors() {
		return errorMessages.count() > 0;
	}

	/**
	 * Clear a specific field and try to update (for validation testing)
	 */
	public EditOwnerPage clearFirstName() {
		firstNameInput.clear();
		return this;
	}

	public EditOwnerPage clearLastName() {
		lastNameInput.clear();
		return this;
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		ownerHeading.waitFor();
	}

}