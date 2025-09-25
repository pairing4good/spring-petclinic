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
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object Model for owner-related pages (search, list, details).
 *
 * @author Copilot
 */
public class OwnersPage extends BasePage {

	// Find owners form elements
	private final Locator lastNameInput;

	private final Locator findOwnerButton;

	private final Locator addOwnerButton;

	// Owner list elements
	private final Locator ownersTable;

	private final Locator ownerNameLinks;

	// Owner form elements (for create/edit)
	private final Locator firstNameInput;

	private final Locator lastNameFormInput;

	private final Locator addressInput;

	private final Locator cityInput;

	private final Locator telephoneInput;

	private final Locator submitButton;

	private final Locator updateButton;

	// Validation elements
	private final Locator errorMessages;

	public OwnersPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Search form locators - using specific form context for disambiguation
		this.lastNameInput = page.locator("form input[name='lastName']");
		this.findOwnerButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Find Owner"));
		this.addOwnerButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Owner"));

		// List view locators - table-specific to avoid ambiguity
		this.ownersTable = page.locator("#owners"); // Specific table ID
		this.ownerNameLinks = ownersTable.locator("tbody td a"); // Links within owner
																	// table

		// Form elements - using form context for disambiguation
		this.firstNameInput = page.locator("form input[name='firstName']");
		this.lastNameFormInput = page.locator("form input[name='lastName']");
		this.addressInput = page.locator("form input[name='address']");
		this.cityInput = page.locator("form input[name='city']");
		this.telephoneInput = page.locator("form input[name='telephone']");
		this.submitButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Add Owner"));
		this.updateButton = page.locator("form button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Update Owner"));

		// Validation elements - use first error message for disambiguation
		this.errorMessages = page.locator(".alert-danger, .has-error").first();
	}

	/**
	 * Navigate to find owners page.
	 */
	public void navigateToFindOwners() {
		navigateTo("/owners/find");
	}

	/**
	 * Search for owners by last name.
	 * @param lastName the last name to search for
	 */
	public void searchOwnersByLastName(String lastName) {
		fill(lastNameInput, lastName);
		click(findOwnerButton);
	}

	/**
	 * Search for all owners (empty search).
	 */
	public void searchAllOwners() {
		// Clear the field and search
		fill(lastNameInput, "");
		click(findOwnerButton);
	}

	/**
	 * Click Add Owner button to go to create form.
	 * @return this page for chaining
	 */
	public OwnersPage clickAddOwner() {
		click(addOwnerButton);
		return this;
	}

	/**
	 * Click on first owner in the list.
	 * @return OwnerDetailsPage for chaining
	 */
	public OwnerDetailsPage clickFirstOwner() {
		click(ownerNameLinks.first());
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Click on owner by name.
	 * @param ownerName the name to click on
	 * @return OwnerDetailsPage for chaining
	 */
	public OwnerDetailsPage clickOwnerByName(String ownerName) {
		Locator ownerLink = ownerNameLinks.filter(new Locator.FilterOptions().setHasText(ownerName));
		click(ownerLink);
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Fill out the owner form with provided details.
	 * @param firstName first name
	 * @param lastName last name
	 * @param address address
	 * @param city city
	 * @param telephone telephone number
	 */
	public void fillOwnerForm(String firstName, String lastName, String address, String city, String telephone) {
		fill(firstNameInput, firstName);
		fill(lastNameFormInput, lastName);
		fill(addressInput, address);
		fill(cityInput, city);
		fill(telephoneInput, telephone);
	}

	/**
	 * Submit the owner creation form.
	 */
	public void submitOwnerForm() {
		(isVisible(submitButton) ? submitButton : updateButton).click();
	}

	/**
	 * Check if owners table is displayed.
	 * @return true if owners table is visible
	 */
	public boolean isOwnersTableDisplayed() {
		return isVisible(ownersTable);
	}

	/**
	 * Get the number of owners in the results table.
	 * @return count of owner rows
	 */
	public int getOwnerCount() {
		if (!isVisible(ownersTable)) {
			return 0;
		}
		return ownerNameLinks.count();
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

}