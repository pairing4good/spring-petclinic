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
 * Page Object Model for the Create Owner page.
 */
public class CreateOwnerPage extends BasePage {

	private final Locator ownerHeading;

	private final Locator firstNameInput;

	private final Locator lastNameInput;

	private final Locator addressInput;

	private final Locator cityInput;

	private final Locator telephoneInput;

	private final Locator submitButton;

	private final Locator errorMessages;

	public CreateOwnerPage(Page page) {
		super(page);
		this.ownerHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Owner"));
		this.firstNameInput = page.locator("input[name='firstName']");
		this.lastNameInput = page.locator("input[name='lastName']");
		this.addressInput = page.locator("input[name='address']");
		this.cityInput = page.locator("input[name='city']");
		this.telephoneInput = page.locator("input[name='telephone']");
		this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
		this.errorMessages = page.locator(".help-inline");
	}

	/**
	 * Check if this is the Create Owner page
	 */
	public boolean isCreateOwnerPage() {
		return ownerHeading.isVisible() && submitButton.isVisible();
	}

	/**
	 * Fill in the owner form
	 */
	public CreateOwnerPage fillOwnerForm(String firstName, String lastName, String address, String city,
			String telephone) {
		firstNameInput.fill(firstName);
		lastNameInput.fill(lastName);
		addressInput.fill(address);
		cityInput.fill(city);
		telephoneInput.fill(telephone);
		return this;
	}

	/**
	 * Submit the form
	 */
	public OwnerDetailsPage submitForm() {
		submitButton.click();
		return new OwnerDetailsPage(page);
	}

	/**
	 * Submit form with validation errors (stays on same page)
	 */
	public CreateOwnerPage submitFormWithErrors() {
		submitButton.click();
		return this;
	}

	/**
	 * Clear all form fields
	 */
	public CreateOwnerPage clearForm() {
		firstNameInput.clear();
		lastNameInput.clear();
		addressInput.clear();
		cityInput.clear();
		telephoneInput.clear();
		return this;
	}

	/**
	 * Check if validation errors are present
	 */
	public boolean hasValidationErrors() {
		return errorMessages.count() > 0;
	}

	/**
	 * Get validation error message
	 */
	public String getValidationError() {
		if (hasValidationErrors()) {
			return errorMessages.first().textContent();
		}
		return "";
	}

	/**
	 * Fill required fields only
	 */
	public CreateOwnerPage fillRequiredFields(String firstName, String lastName) {
		firstNameInput.fill(firstName);
		lastNameInput.fill(lastName);
		return this;
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		ownerHeading.waitFor();
	}

}