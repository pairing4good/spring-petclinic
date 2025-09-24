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
package org.springframework.samples.petclinic.playwright.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object Model for the Owner Details page. Handles viewing and editing owner
 * information and managing pets.
 */
public class OwnerDetailsPage extends BasePage {

	// Locators for owner details elements
	private static final String OWNER_DETAILS_TABLE_SELECTOR = "table.table-striped";

	private static final String EDIT_OWNER_BUTTON_SELECTOR = "a[href*='/edit']:has-text('Edit Owner')";

	private static final String ADD_NEW_PET_BUTTON_SELECTOR = "a[href*='/pets/new']:has-text('Add New Pet')";

	private static final String PETS_TABLE_SELECTOR = "table.table-striped";

	private static final String PET_NAME_LINK_SELECTOR = "table.table-striped a[href*='/pets/']";

	public OwnerDetailsPage(Page page) {
		super(page);
	}

	/**
	 * Check if owner details are visible
	 */
	public boolean isOwnerDetailsVisible() {
		return isElementVisible(OWNER_DETAILS_TABLE_SELECTOR);
	}

	/**
	 * Navigate to edit owner page
	 */
	public EditOwnerPage editOwner() {
		page.locator(EDIT_OWNER_BUTTON_SELECTOR).click();
		waitForPageLoad();
		return new EditOwnerPage(page);
	}

	/**
	 * Navigate to add new pet page
	 */
	public AddPetPage addNewPet() {
		page.locator(ADD_NEW_PET_BUTTON_SELECTOR).click();
		waitForPageLoad();
		return new AddPetPage(page);
	}

	/**
	 * Check if edit owner button is visible
	 */
	public boolean isEditOwnerButtonVisible() {
		return isElementVisible(EDIT_OWNER_BUTTON_SELECTOR);
	}

	/**
	 * Check if add new pet button is visible
	 */
	public boolean isAddNewPetButtonVisible() {
		return isElementVisible(ADD_NEW_PET_BUTTON_SELECTOR);
	}

	/**
	 * Get the number of pets for this owner
	 */
	public int getPetsCount() {
		return page.locator(PET_NAME_LINK_SELECTOR).count();
	}

	/**
	 * Check if the owner has any pets
	 */
	public boolean hasPets() {
		return getPetsCount() > 0;
	}

	/**
	 * Click on the first pet to view pet details
	 */
	public PetDetailsPage clickFirstPet() {
		if (hasPets()) {
			page.locator(PET_NAME_LINK_SELECTOR).first().click();
			waitForPageLoad();
			return new PetDetailsPage(page);
		}
		throw new RuntimeException("No pets found for this owner");
	}

	/**
	 * Get owner information from the details table (basic check)
	 */
	public String getOwnerName() {
		// Look for the owner name in the page - this would need to be more specific based
		// on actual HTML structure
		return page.locator("h2").textContent();
	}

	/**
	 * Verify this is an owner details page
	 */
	public boolean isOwnerDetailsPage() {
		return getCurrentUrl().contains("/owners/") && !getCurrentUrl().contains("/new")
				&& !getCurrentUrl().contains("/edit") && isOwnerDetailsVisible();
	}

	/**
	 * Check if the owner information section is displayed
	 */
	public boolean isOwnerInformationDisplayed() {
		return isOwnerDetailsVisible() && isEditOwnerButtonVisible() && isAddNewPetButtonVisible();
	}

}