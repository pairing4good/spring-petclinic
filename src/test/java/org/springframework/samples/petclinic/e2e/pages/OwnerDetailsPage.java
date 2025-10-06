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
 * Page Object Model for the Owner Details page.
 *
 * Provides methods to interact with elements on the owner details page including: - Owner
 * information display - Pet information display - Visit information display - Action
 * buttons (edit owner, add pet, add visit)
 */
public class OwnerDetailsPage {

	private final Page page;

	// Locators using specific, unambiguous selectors
	private final Locator pageHeading;

	private final Locator ownerInfoTable;

	private final Locator editOwnerButton;

	private final Locator addPetButton;

	private final Locator petsAndVisitsHeading;

	private final Locator successMessage;

	private final Locator errorMessage;

	private final Locator petsTable;

	public OwnerDetailsPage(Page page) {
		this.page = page;
		// Using specific selectors for owner details page elements
		this.pageHeading = page.locator("h2").filter(new Locator.FilterOptions().setHasText("Owner Information"));
		this.ownerInfoTable = page.locator("table.table-striped").first(); // First table
																			// is owner
																			// info
		this.editOwnerButton = page.locator("a.btn").filter(new Locator.FilterOptions().setHasText("Edit Owner"));
		this.addPetButton = page.locator("a.btn").filter(new Locator.FilterOptions().setHasText("Add New Pet"));
		this.petsAndVisitsHeading = page.locator("h2")
			.filter(new Locator.FilterOptions().setHasText("Pets and Visits"));
		this.successMessage = page.locator("#success-message, .alert-success");
		this.errorMessage = page.locator("#error-message, .alert-danger");
		this.petsTable = page.locator("table.table-striped").nth(1); // Second table is
																		// pets
	}

	/**
	 * Navigate to a specific owner details page.
	 */
	public void navigate(String baseUrl, int ownerId) {
		page.navigate(baseUrl + "/owners/" + ownerId);
		page.waitForLoadState();
	}

	/**
	 * Verify the page is loaded correctly.
	 */
	public boolean isLoaded() {
		return pageHeading.isVisible() && ownerInfoTable.isVisible();
	}

	/**
	 * Get owner name from the details table.
	 */
	public String getOwnerName() {
		ownerInfoTable.waitFor();
		// Find the name row and get the value
		return ownerInfoTable.locator("tr")
			.filter(new Locator.FilterOptions().setHasText("Name"))
			.locator("b")
			.textContent();
	}

	/**
	 * Get owner address from the details table.
	 */
	public String getOwnerAddress() {
		try {
			ownerInfoTable.waitFor();
			return ownerInfoTable.locator("tr")
				.filter(new Locator.FilterOptions().setHasText("Address"))
				.locator("td")
				.nth(1)
				.textContent();
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get owner city from the details table.
	 */
	public String getOwnerCity() {
		try {
			ownerInfoTable.waitFor();
			return ownerInfoTable.locator("tr")
				.filter(new Locator.FilterOptions().setHasText("City"))
				.locator("td")
				.nth(1)
				.textContent();
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get owner telephone from the details table.
	 */
	public String getOwnerTelephone() {
		try {
			ownerInfoTable.waitFor();
			return ownerInfoTable.locator("tr")
				.filter(new Locator.FilterOptions().setHasText("Telephone"))
				.locator("td")
				.nth(1)
				.textContent();
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * Click Edit Owner button.
	 */
	public void clickEditOwner() {
		editOwnerButton.click();
		page.waitForLoadState();
	}

	/**
	 * Click Add New Pet button.
	 */
	public void clickAddNewPet() {
		addPetButton.click();
		page.waitForLoadState();
	}

	/**
	 * Check if success message is displayed.
	 */
	public boolean hasSuccessMessage() {
		return successMessage.isVisible();
	}

	/**
	 * Get success message text.
	 */
	public String getSuccessMessage() {
		if (hasSuccessMessage()) {
			return successMessage.textContent();
		}
		return "";
	}

	/**
	 * Check if error message is displayed.
	 */
	public boolean hasErrorMessage() {
		return errorMessage.isVisible();
	}

	/**
	 * Get error message text.
	 */
	public String getErrorMessage() {
		if (hasErrorMessage()) {
			return errorMessage.textContent();
		}
		return "";
	}

	/**
	 * Check if pets and visits section is displayed.
	 */
	public boolean hasPetsAndVisitsSection() {
		return petsAndVisitsHeading.isVisible();
	}

	/**
	 * Get number of pets owned by this owner.
	 */
	public int getPetCount() {
		if (!hasPetsAndVisitsSection()) {
			return 0;
		}
		// Count pet entries (each pet has a row with name, birth date, type)
		return petsTable.locator("tbody tr").count();
	}

	/**
	 * Get pet name at specified index.
	 */
	public String getPetName(int index) {
		if (getPetCount() > index) {
			return petsTable.locator("tbody tr").nth(index).locator("dd").first().textContent();
		}
		return "";
	}

	/**
	 * Click Edit Pet link for the first pet.
	 */
	public void clickEditFirstPet() {
		if (getPetCount() > 0) {
			petsTable.locator("a").filter(new Locator.FilterOptions().setHasText("Edit Pet")).first().click();
			page.waitForLoadState();
		}
	}

	/**
	 * Click Add Visit link for the first pet.
	 */
	public void clickAddVisitForFirstPet() {
		if (getPetCount() > 0) {
			petsTable.locator("a").filter(new Locator.FilterOptions().setHasText("Add Visit")).first().click();
			page.waitForLoadState();
		}
	}

	/**
	 * Verify owner information matches expected values.
	 */
	public boolean verifyOwnerInfo(String expectedName, String expectedAddress, String expectedCity,
			String expectedTelephone) {
		return getOwnerName().contains(expectedName) && getOwnerAddress().equals(expectedAddress)
				&& getOwnerCity().equals(expectedCity) && getOwnerTelephone().equals(expectedTelephone);
	}

	/**
	 * Get page heading text.
	 */
	public String getPageHeading() {
		pageHeading.waitFor();
		return pageHeading.textContent();
	}

	/**
	 * Check if we're on the owner details page (URL pattern check).
	 */
	public boolean isOnOwnerDetailsPage() {
		return page.url().matches(".*\\/owners\\/\\d+$");
	}

}