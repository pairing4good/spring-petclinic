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
package org.springframework.samples.petclinic.playwright.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object Model for the Add Owner page. Handles owner creation form interactions and
 * validation.
 */
public class AddOwnerPage extends BasePage {

	// Form field selectors
	private static final String FIRST_NAME_INPUT_SELECTOR = "input[name='firstName']";

	private static final String LAST_NAME_INPUT_SELECTOR = "input[name='lastName']";

	private static final String ADDRESS_INPUT_SELECTOR = "input[name='address']";

	private static final String CITY_INPUT_SELECTOR = "input[name='city']";

	private static final String TELEPHONE_INPUT_SELECTOR = "input[name='telephone']";

	private static final String SUBMIT_BUTTON_SELECTOR = "button[type='submit']";

	// Error message selectors
	private static final String ERROR_MESSAGE_SELECTOR = ".help-inline";

	private static final String FORM_GROUP_ERROR_SELECTOR = ".form-group.has-error";

	public AddOwnerPage(Page page) {
		super(page);
	}

	/**
	 * Fill in the owner form with all required fields
	 */
	public void fillOwnerForm(String firstName, String lastName, String address, String city, String telephone) {
		page.locator(FIRST_NAME_INPUT_SELECTOR).fill(firstName);
		page.locator(LAST_NAME_INPUT_SELECTOR).fill(lastName);
		page.locator(ADDRESS_INPUT_SELECTOR).fill(address);
		page.locator(CITY_INPUT_SELECTOR).fill(city);
		page.locator(TELEPHONE_INPUT_SELECTOR).fill(telephone);
	}

	/**
	 * Submit the owner form
	 */
	public void submitForm() {
		page.locator(SUBMIT_BUTTON_SELECTOR).click();
		waitForPageLoad();
	}

	/**
	 * Fill and submit owner form in one action
	 */
	public OwnerDetailsPage createOwner(String firstName, String lastName, String address, String city,
			String telephone) {
		fillOwnerForm(firstName, lastName, address, city, telephone);
		submitForm();
		return new OwnerDetailsPage(page);
	}

	/**
	 * Create owner with invalid data to test validation
	 */
	public void createOwnerWithInvalidData(String firstName, String lastName, String address, String city,
			String telephone) {
		fillOwnerForm(firstName, lastName, address, city, telephone);
		submitForm();
		// Don't navigate away - stay on form to check validation errors
	}

	/**
	 * Check if form validation errors are present
	 */
	public boolean hasValidationErrors() {
		return page.locator(FORM_GROUP_ERROR_SELECTOR).count() > 0;
	}

	/**
	 * Get validation error message for a specific field
	 */
	public String getValidationError() {
		if (page.locator(ERROR_MESSAGE_SELECTOR).count() > 0) {
			return page.locator(ERROR_MESSAGE_SELECTOR).first().textContent().trim();
		}
		return "";
	}

	/**
	 * Check if all form fields are visible
	 */
	public boolean areAllFormFieldsVisible() {
		return isElementVisible(FIRST_NAME_INPUT_SELECTOR) && isElementVisible(LAST_NAME_INPUT_SELECTOR)
				&& isElementVisible(ADDRESS_INPUT_SELECTOR) && isElementVisible(CITY_INPUT_SELECTOR)
				&& isElementVisible(TELEPHONE_INPUT_SELECTOR) && isElementVisible(SUBMIT_BUTTON_SELECTOR);
	}

	/**
	 * Clear all form fields
	 */
	public void clearAllFields() {
		page.locator(FIRST_NAME_INPUT_SELECTOR).fill("");
		page.locator(LAST_NAME_INPUT_SELECTOR).fill("");
		page.locator(ADDRESS_INPUT_SELECTOR).fill("");
		page.locator(CITY_INPUT_SELECTOR).fill("");
		page.locator(TELEPHONE_INPUT_SELECTOR).fill("");
	}

	/**
	 * Verify this is the add owner page
	 */
	public boolean isAddOwnerPage() {
		return getCurrentUrl().contains("/owners/new") && areAllFormFieldsVisible();
	}

}