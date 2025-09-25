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
 * Page Object for the Add Owner form page. Contains locators and actions for creating new
 * owners.
 */
public class AddOwnerPage extends BasePage {

	// Form field locators using name attributes for specificity
	private static final String PAGE_HEADING = "h2:has-text('Owner')";

	private static final String FIRST_NAME_INPUT = "input[name='firstName']";

	private static final String LAST_NAME_INPUT = "input[name='lastName']";

	private static final String ADDRESS_INPUT = "input[name='address']";

	private static final String CITY_INPUT = "input[name='city']";

	private static final String TELEPHONE_INPUT = "input[name='telephone']";

	private static final String ADD_OWNER_BUTTON = "button:has-text('Add Owner')";

	public AddOwnerPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Navigate directly to the Add Owner page
	 */
	public AddOwnerPage open() {
		navigateTo("/owners/new");
		waitForPageLoad();
		return this;
	}

	/**
	 * Wait for add owner form to load
	 */
	public void waitForPageLoad() {
		waitForElement(PAGE_HEADING);
		waitForElement(FIRST_NAME_INPUT);
		waitForElement(ADD_OWNER_BUTTON);
	}

	/**
	 * Fill in the first name field
	 */
	public AddOwnerPage enterFirstName(String firstName) {
		page.locator(FIRST_NAME_INPUT).fill(firstName);
		return this;
	}

	/**
	 * Fill in the last name field
	 */
	public AddOwnerPage enterLastName(String lastName) {
		page.locator(LAST_NAME_INPUT).fill(lastName);
		return this;
	}

	/**
	 * Fill in the address field
	 */
	public AddOwnerPage enterAddress(String address) {
		page.locator(ADDRESS_INPUT).fill(address);
		return this;
	}

	/**
	 * Fill in the city field
	 */
	public AddOwnerPage enterCity(String city) {
		page.locator(CITY_INPUT).fill(city);
		return this;
	}

	/**
	 * Fill in the telephone field
	 */
	public AddOwnerPage enterTelephone(String telephone) {
		page.locator(TELEPHONE_INPUT).fill(telephone);
		return this;
	}

	/**
	 * Fill out the complete owner form
	 */
	public AddOwnerPage fillOwnerForm(String firstName, String lastName, String address, String city,
			String telephone) {
		return enterFirstName(firstName).enterLastName(lastName)
			.enterAddress(address)
			.enterCity(city)
			.enterTelephone(telephone);
	}

	/**
	 * Submit the form (should redirect to owner details on success)
	 */
	public OwnerDetailsPage submitForm() {
		page.locator(ADD_OWNER_BUTTON).click();
		// Wait for page navigation after form submission
		page.waitForLoadState();
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Submit form expecting validation errors (stays on same page)
	 */
	public AddOwnerPage submitFormWithErrors() {
		page.locator(ADD_OWNER_BUTTON).click();
		// Wait for page to process but stay on same page
		page.waitForTimeout(1000);
		return this;
	}

	/**
	 * Get validation error message for a specific field
	 */
	public String getFieldError(String fieldName) {
		String errorSelector = "span.text-danger";
		// Look for error message near the specific field
		return page.locator(errorSelector).first().textContent();
	}

	/**
	 * Check if form has validation errors
	 */
	public boolean hasValidationErrors() {
		return page.locator("span.text-danger").count() > 0;
	}

	/**
	 * Clear all form fields
	 */
	public AddOwnerPage clearForm() {
		page.locator(FIRST_NAME_INPUT).fill("");
		page.locator(LAST_NAME_INPUT).fill("");
		page.locator(ADDRESS_INPUT).fill("");
		page.locator(CITY_INPUT).fill("");
		page.locator(TELEPHONE_INPUT).fill("");
		return this;
	}

	/**
	 * Get current value of first name field
	 */
	public String getFirstNameValue() {
		return page.locator(FIRST_NAME_INPUT).inputValue();
	}

	/**
	 * Get current value of last name field
	 */
	public String getLastNameValue() {
		return page.locator(LAST_NAME_INPUT).inputValue();
	}

}