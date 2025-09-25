/*
 * Copyright 2012-2019 the original author or authors.
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

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object Model for pet creation and editing pages.
 *
 * @author Copilot
 */
public class PetPage extends BasePage {

	// Pet form elements
	private final Locator nameInput;

	private final Locator birthDateInput;

	private final Locator typeSelect;

	private final Locator submitButton;

	private final Locator updateButton;

	// Validation elements
	private final Locator errorMessages;

	private final Locator pageHeading;

	public PetPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Form elements - using form context for disambiguation
		this.nameInput = page.locator("form input[name='name']");
		this.birthDateInput = page.locator("form input[name='birthDate']");
		this.typeSelect = page.locator("form select[name='type']");
		this.submitButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Add Pet"));
		this.updateButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Update Pet"));

		// Validation and navigation elements
		this.errorMessages = page.locator(".alert-danger, .has-error");
		this.pageHeading = page.locator("h2").first();
	}

	/**
	 * Fill out the pet form.
	 * @param name pet name
	 * @param birthDate birth date in YYYY-MM-DD format
	 * @param type pet type
	 */
	public void fillPetForm(String name, String birthDate, String type) {
		fill(nameInput, name);
		fill(birthDateInput, birthDate);
		selectPetType(type);
	}

	/**
	 * Select pet type from dropdown.
	 * @param type the pet type to select
	 */
	public void selectPetType(String type) {
		typeSelect.selectOption(type);
	}

	/**
	 * Submit the pet form (for new pets).
	 */
	public void submitPetForm() {
		if (isVisible(submitButton)) {
			click(submitButton);
		}
		else {
			click(updateButton);
		}
	}

	/**
	 * Get the page heading text.
	 * @return page heading
	 */
	public String getPageHeading() {
		return getText(pageHeading);
	}

	/**
	 * Check if validation errors are displayed.
	 * @return true if error messages are present
	 */
	public boolean hasValidationErrors() {
		return isVisible(errorMessages);
	}

	/**
	 * Get validation error text.
	 * @return error message text
	 */
	public String getValidationErrorText() {
		return getText(errorMessages);
	}

	/**
	 * Check if the pet form is loaded.
	 * @return true if form elements are present
	 */
	public boolean isPetFormLoaded() {
		return isVisible(nameInput) && isVisible(typeSelect);
	}

}