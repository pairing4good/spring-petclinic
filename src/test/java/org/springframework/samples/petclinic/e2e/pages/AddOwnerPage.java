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
import com.microsoft.playwright.Locator;

/**
 * Page Object for the Add Owner page.
 */
public class AddOwnerPage extends BasePage {

	private final Locator ownerHeading;

	private final Locator firstNameInput;

	private final Locator lastNameInput;

	private final Locator addressInput;

	private final Locator cityInput;

	private final Locator telephoneInput;

	private final Locator addOwnerButton;

	public AddOwnerPage(Page page, String baseUrl) {
		super(page, baseUrl);
		this.ownerHeading = page.locator("h2:has-text('Owner')");
		this.firstNameInput = page.locator("input[id*='firstName'], input[name*='firstName']");
		this.lastNameInput = page.locator("input[id*='lastName'], input[name*='lastName']");
		this.addressInput = page.locator("input[id*='address'], input[name*='address']");
		this.cityInput = page.locator("input[id*='city'], input[name*='city']");
		this.telephoneInput = page.locator("input[id*='telephone'], input[name*='telephone']");
		this.addOwnerButton = page.locator("button:has-text('Add Owner')");
	}

	public void navigateTo() {
		page.navigate(baseUrl + "/owners/new");
		waitForPageLoad();
	}

	public boolean isOwnerHeadingVisible() {
		return ownerHeading.isVisible();
	}

	public void enterFirstName(String firstName) {
		firstNameInput.fill(firstName);
	}

	public void enterLastName(String lastName) {
		lastNameInput.fill(lastName);
	}

	public void enterAddress(String address) {
		addressInput.fill(address);
	}

	public void enterCity(String city) {
		cityInput.fill(city);
	}

	public void enterTelephone(String telephone) {
		telephoneInput.fill(telephone);
	}

	public void clickAddOwner() {
		addOwnerButton.click();
		waitForPageLoad();
	}

	public void fillOwnerForm(String firstName, String lastName, String address, String city, String telephone) {
		enterFirstName(firstName);
		enterLastName(lastName);
		enterAddress(address);
		enterCity(city);
		enterTelephone(telephone);
	}

	public boolean areAllFieldsVisible() {
		return firstNameInput.isVisible() && lastNameInput.isVisible() && addressInput.isVisible()
				&& cityInput.isVisible() && telephoneInput.isVisible() && addOwnerButton.isVisible();
	}

	public String getFirstNameValue() {
		return firstNameInput.inputValue();
	}

	public String getLastNameValue() {
		return lastNameInput.inputValue();
	}

	public String getAddressValue() {
		return addressInput.inputValue();
	}

	public String getCityValue() {
		return cityInput.inputValue();
	}

	public String getTelephoneValue() {
		return telephoneInput.inputValue();
	}

}