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
 * Page Object Model for the Add/Create Owner page.
 * 
 * Provides methods to interact with elements on the add owner form including:
 * - Form field interactions
 * - Form validation
 * - Form submission
 * - Error handling
 */
public class AddOwnerPage {

	private final Page page;

	// Locators using specific, unambiguous selectors with form context
	private final Locator pageHeading;

	private final Locator firstNameInput;

	private final Locator lastNameInput;

	private final Locator addressInput;

	private final Locator cityInput;

	private final Locator telephoneInput;

	private final Locator submitButton;

	private final Locator ownerForm;

	private final Locator fieldErrors;

	private final Locator successMessage;

	private final Locator errorMessage;

	public AddOwnerPage(Page page) {
		this.page = page;
		// Using form context for disambiguation and more reliable locators
		this.pageHeading = page.locator("h2");
		this.ownerForm = page.locator("form");
		this.firstNameInput = page.locator("input[name='firstName']");
		this.lastNameInput = page.locator("input[name='lastName']");
		this.addressInput = page.locator("input[name='address']");
		this.cityInput = page.locator("input[name='city']");
		this.telephoneInput = page.locator("input[name='telephone']");
		this.submitButton = ownerForm.locator("button[type='submit']");
		this.fieldErrors = page.locator(".help-inline, .has-error");
		this.successMessage = page.locator("#success-message, .alert-success");
		this.errorMessage = page.locator("#error-message, .alert-danger");
	}

	/**
	 * Navigate to the add owner page.
	 */
	public void navigate(String baseUrl) {
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState();
	}

	/**
	 * Verify the page is loaded correctly.
	 */
	public boolean isLoaded() {
		return ownerForm.isVisible() && firstNameInput.isVisible();
	}

	/**
	 * Fill all owner form fields with valid data.
	 */
	public void fillOwnerForm(String firstName, String lastName, String address, String city, String telephone) {
		firstNameInput.waitFor();
		firstNameInput.fill(firstName);
		lastNameInput.fill(lastName);
		addressInput.fill(address);
		cityInput.fill(city);
		telephoneInput.fill(telephone);
	}

	/**
	 * Fill partial owner form to test validation.
	 */
	public void fillPartialOwnerForm(String firstName, String lastName, String city) {
		firstNameInput.waitFor();
		firstNameInput.fill(firstName);
		lastNameInput.fill(lastName);
		cityInput.fill(city);
		// Intentionally leave address and telephone empty for validation testing
	}

	/**
	 * Submit the owner form.
	 */
	public void submitForm() {
		submitButton.click();
		page.waitForLoadState();
	}

	/**
	 * Fill form and submit in one action.
	 */
	public void createOwner(String firstName, String lastName, String address, String city, String telephone) {
		fillOwnerForm(firstName, lastName, address, city, telephone);
		submitForm();
	}

	/**
	 * Check if form validation errors are displayed.
	 */
	public boolean hasValidationErrors() {
		return fieldErrors.count() > 0;
	}

	/**
	 * Get validation error messages.
	 */
	public String getValidationErrors() {
		if (hasValidationErrors()) {
			StringBuilder errors = new StringBuilder();
			int errorCount = fieldErrors.count();
			for (int i = 0; i < errorCount; i++) {
				if (i > 0)
					errors.append("; ");
				errors.append(fieldErrors.nth(i).textContent());
			}
			return errors.toString();
		}
		return "";
	}

	/**
	 * Check if success message is displayed.
	 */
	public boolean hasSuccessMessage() {
		return successMessage.isVisible();
	}

	/**
	 * Get success message text.
	 */
	public String getSuccessMessage() {
		if (hasSuccessMessage()) {
			return successMessage.textContent();
		}
		return "";
	}

	/**
	 * Check if error message is displayed.
	 */
	public boolean hasErrorMessage() {
		return errorMessage.isVisible();
	}

	/**
	 * Get error message text.
	 */
	public String getErrorMessage() {
		if (hasErrorMessage()) {
			return errorMessage.textContent();
		}
		return "";
	}

	/**
	 * Clear all form fields.
	 */
	public void clearForm() {
		firstNameInput.fill("");
		lastNameInput.fill("");
		addressInput.fill("");
		cityInput.fill("");
		telephoneInput.fill("");
	}

	/**
	 * Test form with special characters.
	 */
	public void fillFormWithSpecialCharacters() {
		fillOwnerForm("José María", "O'Connor-Smith", "123 Main St. #4B", "São Paulo", "+1-555-123-4567");
	}

	/**
	 * Test form with maximum length inputs.
	 */
	public void fillFormWithMaxLengthInputs() {
		String longName = "A".repeat(50); // Assuming reasonable max length
		String longAddress = "1234 Very Long Street Name That Goes On And On And On".substring(0, 80);
		String longCity = "Very Long City Name That Tests Maximum Length".substring(0, 80);
		fillOwnerForm(longName, longName, longAddress, longCity, "555-123-4567");
	}

	/**
	 * Get page heading text.
	 */
	public String getPageHeading() {
		pageHeading.waitFor();
		return pageHeading.textContent();
	}

	/**
	 * Check if redirected to owner details page after successful creation.
	 */
	public boolean isRedirectedToOwnerDetails() {
		return page.url().matches(".*\\/owners\\/\\d+$");
	}

}