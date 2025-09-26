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
 * Page Object Model for owner details page.
 *
 * @author Copilot
 */
public class OwnerDetailsPage extends BasePage {

	// Owner information elements
	private final Locator ownerName;

	private final Locator ownerAddress;

	private final Locator ownerCity;

	private final Locator ownerTelephone;

	// Action buttons
	private final Locator editOwnerButton;

	private final Locator addNewPetButton;

	// Pets section
	private final Locator petsTable;

	private final Locator petNames;

	private final Locator editPetLinks;

	private final Locator addVisitLinks;

	public OwnerDetailsPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Owner info - using table structure for disambiguation
		this.ownerName = page.locator("table.table-striped th:has-text('Name') + td");
		this.ownerAddress = page.locator("table.table-striped th:has-text('Address') + td");
		this.ownerCity = page.locator("table.table-striped th:has-text('City') + td");
		this.ownerTelephone = page.locator("table.table-striped th:has-text('Telephone') + td");

		// Action buttons - specific button text for disambiguation
		this.editOwnerButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Owner"));
		this.addNewPetButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add New Pet"));

		// Pets section - using heading context for disambiguation
		this.petsTable = page.locator("h2:has-text('Pets and Visits') + table");
		this.petNames = petsTable.locator("tbody tr td:first-child");
		this.editPetLinks = petsTable.locator("a:has-text('Edit Pet')");
		this.addVisitLinks = petsTable.locator("a:has-text('Add Visit')");
	}

	/**
	 * Get the owner's full name.
	 * @return owner name
	 */
	public String getOwnerName() {
		return getText(ownerName);
	}

	/**
	 * Get the owner's address.
	 * @return owner address
	 */
	public String getOwnerAddress() {
		return getText(ownerAddress);
	}

	/**
	 * Get the owner's city.
	 * @return owner city
	 */
	public String getOwnerCity() {
		return getText(ownerCity);
	}

	/**
	 * Get the owner's telephone.
	 * @return owner telephone
	 */
	public String getOwnerTelephone() {
		return getText(ownerTelephone);
	}

	/**
	 * Click Edit Owner button.
	 * @return OwnersPage for editing
	 */
	public OwnersPage clickEditOwner() {
		click(editOwnerButton);
		return new OwnersPage(page, baseUrl);
	}

	/**
	 * Click Add New Pet button.
	 * @return PetPage for adding pet
	 */
	public PetPage clickAddNewPet() {
		click(addNewPetButton);
		return new PetPage(page, baseUrl);
	}

	/**
	 * Check if pets table is displayed.
	 * @return true if pets table is visible
	 */
	public boolean isPetsTableDisplayed() {
		return isVisible(petsTable);
	}

	/**
	 * Get the number of pets for this owner.
	 * @return count of pets
	 */
	public int getPetCount() {
		if (!isVisible(petsTable)) {
			return 0;
		}
		return petNames.count();
	}

	/**
	 * Get pet name by index.
	 * @param index pet index (0-based)
	 * @return pet name
	 */
	public String getPetName(int index) {
		return getText(petNames.nth(index));
	}

	/**
	 * Click Edit Pet link for a specific pet.
	 * @param petIndex pet index (0-based)
	 * @return PetPage for editing
	 */
	public PetPage clickEditPet(int petIndex) {
		click(editPetLinks.nth(petIndex));
		return new PetPage(page, baseUrl);
	}

	/**
	 * Click Add Visit link for a specific pet.
	 * @param petIndex pet index (0-based)
	 * @return VisitPage for adding visit
	 */
	public VisitPage clickAddVisit(int petIndex) {
		click(addVisitLinks.nth(petIndex));
		return new VisitPage(page, baseUrl);
	}

	/**
	 * Check if the owner details page is properly loaded.
	 * @return true if owner information is displayed
	 */
	public boolean isOwnerDetailsLoaded() {
		return isVisible(ownerName) && isVisible(editOwnerButton);
	}

}