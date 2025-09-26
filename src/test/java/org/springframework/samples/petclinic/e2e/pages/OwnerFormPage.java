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
 * Page Object for the Owner form (create/update) page
 */
public class OwnerFormPage extends BasePage {

	public OwnerFormPage(Page page) {
		super(page);
	}

	public void navigateToNewOwnerForm() {
		navigateTo("/owners/new");
	}

	public Locator getFirstNameInput() {
		return page.locator("input[name='firstName']");
	}

	public Locator getLastNameInput() {
		return page.locator("input[name='lastName']");
	}

	public Locator getAddressInput() {
		return page.locator("input[name='address']");
	}

	public Locator getCityInput() {
		return page.locator("input[name='city']");
	}

	public Locator getTelephoneInput() {
		return page.locator("input[name='telephone']");
	}

	public Locator getSubmitButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Add Owner").setExact(false));
	}

	public Locator getUpdateButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Update Owner").setExact(false));
	}

	public void fillOwnerForm(String firstName, String lastName, String address, String city, String telephone) {
		getFirstNameInput().fill(firstName);
		getLastNameInput().fill(lastName);
		getAddressInput().fill(address);
		getCityInput().fill(city);
		getTelephoneInput().fill(telephone);
	}

	public void submitForm() {
		if (getSubmitButton().isVisible()) {
			getSubmitButton().click();
		}
		else {
			getUpdateButton().click();
		}
	}

	public boolean hasValidationErrors() {
		return page.locator(".has-error, .error, .is-invalid").count() > 0;
	}

	public String getValidationError(String fieldName) {
		return page.locator("input[name='" + fieldName + "']")
			.locator("xpath=following-sibling::span[@class='help-block']")
			.textContent();
	}

	public boolean isEditMode() {
		return getUpdateButton().isVisible();
	}

}