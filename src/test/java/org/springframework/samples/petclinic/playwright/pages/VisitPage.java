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
 * Page Object Model for visit creation pages.
 *
 * @author Copilot
 */
public class VisitPage extends BasePage {

	// Visit form elements
	private final Locator dateInput;

	private final Locator descriptionTextarea;

	private final Locator submitButton;

	// Pet information
	private final Locator petInfo;

	private final Locator pageHeading;

	// Validation elements
	private final Locator errorMessages;

	public VisitPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Form elements - using form context for disambiguation
		this.dateInput = page.locator("form input[name='date']");
		this.descriptionTextarea = page.locator("form textarea[name='description']");
		this.submitButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Add Visit"));

		// Page information elements
		this.petInfo = page.locator("table.table-striped"); // Pet info table
		this.pageHeading = page.locator("h2").first();

		// Validation elements
		this.errorMessages = page.locator(".alert-danger, .has-error");
	}

	/**
	 * Fill out the visit form.
	 * @param date visit date in YYYY-MM-DD format
	 * @param description visit description
	 */
	public void fillVisitForm(String date, String description) {
		fill(dateInput, date);
		fill(descriptionTextarea, description);
	}

	/**
	 * Submit the visit form.
	 */
	public void submitVisitForm() {
		click(submitButton);
	}

	/**
	 * Get the page heading text.
	 * @return page heading
	 */
	public String getPageHeading() {
		return getText(pageHeading);
	}

	/**
	 * Check if pet information is displayed.
	 * @return true if pet info table is visible
	 */
	public boolean isPetInfoDisplayed() {
		return isVisible(petInfo);
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
	 * Check if the visit form is loaded.
	 * @return true if form elements are present
	 */
	public boolean isVisitFormLoaded() {
		return isVisible(dateInput) && isVisible(descriptionTextarea);
	}

}