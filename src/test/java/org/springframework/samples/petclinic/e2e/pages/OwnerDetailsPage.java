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
 * Page Object for the Owner Details page
 */
public class OwnerDetailsPage extends BasePage {

	public OwnerDetailsPage(Page page) {
		super(page);
	}

	public Locator getEditOwnerButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
				new Page.GetByRoleOptions().setName("Edit Owner"));
	}

	public Locator getAddNewPetButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
				new Page.GetByRoleOptions().setName("Add New Pet"));
	}

	public Locator getOwnerInfo() {
		return page.locator("table").first();
	}

	public Locator getPetsTable() {
		return page.locator("table").nth(1);
	}

	public void clickEditOwner() {
		getEditOwnerButton().click();
	}

	public void clickAddNewPet() {
		getAddNewPetButton().click();
	}

	public String getOwnerName() {
		return page.locator("h2").textContent();
	}

	public boolean hasPets() {
		return getPetsTable().isVisible();
	}

	public int getPetCount() {
		if (!hasPets()) {
			return 0;
		}
		return getPetsTable().locator("tbody tr").count();
	}

	public Locator getPetRow(String petName) {
		return getPetsTable().locator("tr").filter(new Locator.FilterOptions().setHasText(petName));
	}

	public void clickEditPet(String petName) {
		getPetRow(petName)
			.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
					new Locator.GetByRoleOptions().setName("Edit Pet"))
			.click();
	}

	public void clickAddVisit(String petName) {
		getPetRow(petName)
			.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
					new Locator.GetByRoleOptions().setName("Add Visit"))
			.click();
	}

	public boolean isPetDisplayed(String petName) {
		return getPetRow(petName).isVisible();
	}

}