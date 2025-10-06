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
 * Page Object Model for the Edit Pet page.
 */
public class EditPetPage extends BasePage {

	private final Locator petHeading;

	private final Locator nameInput;

	private final Locator birthDateInput;

	private final Locator typeSelect;

	private final Locator updateButton;

	public EditPetPage(Page page) {
		super(page);
		this.petHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Pet"));
		this.nameInput = page.locator("input[name='name']");
		this.birthDateInput = page.locator("input[name='birthDate']");
		this.typeSelect = page.locator("select[name='type']");
		this.updateButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update Pet"));
	}

	/**
	 * Check if this is the Edit Pet page
	 */
	public boolean isEditPetPage() {
		return petHeading.isVisible() && updateButton.isVisible();
	}

	/**
	 * Update pet information
	 */
	public EditPetPage updatePetInfo(String name, String birthDate, String type) {
		nameInput.clear();
		nameInput.fill(name);
		birthDateInput.clear();
		birthDateInput.fill(birthDate);
		typeSelect.selectOption(type);
		return this;
	}

	/**
	 * Submit the update
	 */
	public OwnerDetailsPage submitUpdate() {
		updateButton.click();
		return new OwnerDetailsPage(page);
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		petHeading.waitFor();
	}

}