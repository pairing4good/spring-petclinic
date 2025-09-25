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
 * Page Object Model for the Owner Form page (Add/Edit Owner)
 */
public class OwnerFormPage extends BasePage {

	public OwnerFormPage(Page page) {
		super(page);
	}

	// Page elements
	private Locator pageHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Owner"));
	}

	private Locator firstNameInput() {
		return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name"));
	}

	private Locator lastNameInput() {
		return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name"));
	}

	private Locator addressInput() {
		return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Address"));
	}

	private Locator cityInput() {
		return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("City"));
	}

	private Locator telephoneInput() {
		return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Telephone"));
	}

	private Locator addOwnerButton() {
		return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner"));
	}

	private Locator updateOwnerButton() {
		return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update Owner"));
	}

	// Validation error elements - using specific locators for field errors
	private Locator firstNameError() {
		return page.locator(".help-inline").filter(new Locator.FilterOptions().setHasText("may not be empty"));
	}

	private Locator addressError() {
		return page.locator(".help-inline").filter(new Locator.FilterOptions().setHasText("may not be empty"));
	}

	private Locator telephoneError() {
		return page.locator(".help-inline").filter(new Locator.FilterOptions().setHasText("numeric value"));
	}

	// Form actions
	public OwnerFormPage fillFirstName(String firstName) {
		firstNameInput().fill(firstName);
		return this;
	}

	public OwnerFormPage fillLastName(String lastName) {
		lastNameInput().fill(lastName);
		return this;
	}

	public OwnerFormPage fillAddress(String address) {
		addressInput().fill(address);
		return this;
	}

	public OwnerFormPage fillCity(String city) {
		cityInput().fill(city);
		return this;
	}

	public OwnerFormPage fillTelephone(String telephone) {
		telephoneInput().fill(telephone);
		return this;
	}

	public OwnerFormPage fillAllFields(String firstName, String lastName, String address, String city, String telephone) {
		return fillFirstName(firstName)
			.fillLastName(lastName)
			.fillAddress(address)
			.fillCity(city)
			.fillTelephone(telephone);
	}

	public void clickAddOwner() {
		addOwnerButton().click();
		page.waitForLoadState();
	}

	public void clickUpdateOwner() {
		updateOwnerButton().click();
		page.waitForLoadState();
	}

	// Verification methods
	public boolean isPageHeadingVisible() {
		return pageHeading().isVisible();
	}

	public boolean isAddOwnerButtonVisible() {
		return addOwnerButton().isVisible();
	}

	public boolean isUpdateOwnerButtonVisible() {
		return updateOwnerButton().isVisible();
	}

	// Validation error checking
	public boolean hasFirstNameError() {
		return firstNameError().isVisible();
	}

	public boolean hasAddressError() {
		return addressError().isVisible();
	}

	public boolean hasTelephoneError() {
		return telephoneError().isVisible();
	}

	public boolean hasValidationErrors() {
		// Check if any validation error is visible on the page
		return page.locator(".help-inline").first().isVisible();
	}

	// Get current field values
	public String getFirstNameValue() {
		return firstNameInput().inputValue();
	}

	public String getLastNameValue() {
		return lastNameInput().inputValue();
	}

	public String getAddressValue() {
		return addressInput().inputValue();
	}

	public String getCityValue() {
		return cityInput().inputValue();
	}

	public String getTelephoneValue() {
		return telephoneInput().inputValue();
	}

}