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
 * Page Object Model for the Owner Details page.
 */
public class OwnerDetailsPage extends BasePage {

	private final Locator ownerInfoHeading;

	private final Locator ownerInfoTable;

	private final Locator editOwnerLink;

	private final Locator addNewPetLink;

	private final Locator petsAndVisitsHeading;

	private final Locator petsTable;

	public OwnerDetailsPage(Page page) {
		super(page);
		this.ownerInfoHeading = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Owner Information"));
		this.ownerInfoTable = page.getByRole(AriaRole.TABLE).first();
		this.editOwnerLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Owner"));
		this.addNewPetLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add New Pet"));
		this.petsAndVisitsHeading = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Pets and Visits"));
		this.petsTable = page.getByRole(AriaRole.TABLE).nth(1);
	}

	/**
	 * Check if this is the Owner Details page
	 */
	public boolean isOwnerDetailsPage() {
		return ownerInfoHeading.isVisible();
	}

	/**
	 * Get the owner's name from the details table
	 */
	public String getOwnerName() {
		return ownerInfoTable.locator("tbody tr").first().locator("td").nth(1).textContent();
	}

	/**
	 * Get the owner's address
	 */
	public String getOwnerAddress() {
		return ownerInfoTable.locator("tbody tr").nth(1).locator("td").nth(1).textContent();
	}

	/**
	 * Get the owner's city
	 */
	public String getOwnerCity() {
		return ownerInfoTable.locator("tbody tr").nth(2).locator("td").nth(1).textContent();
	}

	/**
	 * Get the owner's telephone
	 */
	public String getOwnerTelephone() {
		return ownerInfoTable.locator("tbody tr").nth(3).locator("td").nth(1).textContent();
	}

	/**
	 * Click the Edit Owner link
	 */
	public EditOwnerPage clickEditOwner() {
		editOwnerLink.click();
		return new EditOwnerPage(page);
	}

	/**
	 * Click the Add New Pet link
	 */
	public CreatePetPage clickAddNewPet() {
		addNewPetLink.click();
		return new CreatePetPage(page);
	}

	/**
	 * Check if the owner has pets
	 */
	public boolean hasPets() {
		return petsAndVisitsHeading.isVisible() && petsTable.isVisible();
	}

	/**
	 * Get the number of pets
	 */
	public int getPetsCount() {
		if (!hasPets()) {
			return 0;
		}
		return petsTable.locator("tbody tr").count();
	}

	/**
	 * Click Edit Pet for the first pet
	 */
	public EditPetPage clickEditFirstPet() {
		Locator editPetLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Pet")).first();
		editPetLink.click();
		return new EditPetPage(page);
	}

	/**
	 * Click Add Visit for the first pet
	 */
	public CreateVisitPage clickAddVisitForFirstPet() {
		Locator addVisitLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Visit")).first();
		addVisitLink.click();
		return new CreateVisitPage(page);
	}

	/**
	 * Get pet name by index
	 */
	public String getPetName(int petIndex) {
		if (!hasPets() || getPetsCount() <= petIndex) {
			return "";
		}
		return petsTable.locator("tbody tr").nth(petIndex).locator("td").first().locator("dl dd").first().textContent();
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		ownerInfoHeading.waitFor();
	}

}