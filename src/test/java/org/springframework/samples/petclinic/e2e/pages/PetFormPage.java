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
 * Page Object for the Pet form (create/update) page
 */
public class PetFormPage extends BasePage {

	public PetFormPage(Page page) {
		super(page);
	}

	public Locator getPetNameInput() {
		return page.locator("input[name='name']");
	}

	public Locator getBirthDateInput() {
		return page.locator("input[name='birthDate']");
	}

	public Locator getTypeSelect() {
		return page.locator("select[name='type']");
	}

	public Locator getSubmitButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Add Pet").setExact(false));
	}

	public Locator getUpdateButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Update Pet").setExact(false));
	}

	public void fillPetForm(String name, String birthDate, String type) {
		getPetNameInput().fill(name);
		getBirthDateInput().fill(birthDate);
		getTypeSelect().selectOption(type);
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
		return page.locator("input[name='" + fieldName + "'], select[name='" + fieldName + "']")
			.locator("xpath=following-sibling::span[@class='help-block']")
			.textContent();
	}

	public boolean isEditMode() {
		return getUpdateButton().isVisible();
	}

}