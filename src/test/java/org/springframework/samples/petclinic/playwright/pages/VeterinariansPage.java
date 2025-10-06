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
 * Page Object Model for the Veterinarians page. Handles display of veterinarians list and
 * pagination.
 */
public class VeterinariansPage extends BasePage {

	// Locators for veterinarians page elements
	private static final String VETS_TABLE_SELECTOR = "table#vets";

	private static final String VET_ROW_SELECTOR = "table#vets tbody tr";

	private static final String PAGINATION_SELECTOR = "div:has(span:text('Pages:'))";

	private static final String PAGINATION_LINK_SELECTOR = "a[href*='page=']";

	private static final String CURRENT_PAGE_SELECTOR = "span:not(a)";

	public VeterinariansPage(Page page) {
		super(page);
	}

	/**
	 * Check if the veterinarians table is visible
	 */
	public boolean isVetsTableVisible() {
		return isElementVisible(VETS_TABLE_SELECTOR);
	}

	/**
	 * Get the number of veterinarians displayed on current page
	 */
	public int getVetsCount() {
		if (!isVetsTableVisible()) {
			return 0;
		}
		return page.locator(VET_ROW_SELECTOR).count();
	}

	/**
	 * Check if pagination controls are visible
	 */
	public boolean isPaginationVisible() {
		return isElementVisible(PAGINATION_SELECTOR);
	}

	/**
	 * Click on a specific page number in pagination
	 */
	public void goToPage(int pageNumber) {
		if (isPaginationVisible()) {
			page.locator(PAGINATION_LINK_SELECTOR)
				.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText(String.valueOf(pageNumber)))
				.click();
			waitForPageLoad();
		}
	}

	/**
	 * Get all veterinarian names from the current page
	 */
	public String[] getAllVetNames() {
		if (!isVetsTableVisible()) {
			return new String[0];
		}

		// Get all name cells (first column of each row)
		var nameLocators = page.locator(VET_ROW_SELECTOR + " td:first-child");
		int count = nameLocators.count();
		String[] names = new String[count];

		for (int i = 0; i < count; i++) {
			names[i] = nameLocators.nth(i).textContent().trim();
		}

		return names;
	}

	/**
	 * Check if a specific veterinarian is listed on the current page
	 */
	public boolean isVetListed(String vetName) {
		String[] allNames = getAllVetNames();
		for (String name : allNames) {
			if (name.contains(vetName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the specialties for a specific veterinarian
	 */
	public String getVetSpecialties(String vetName) {
		if (!isVetsTableVisible()) {
			return "";
		}

		// Find the row containing the vet name and get the specialties column
		var rows = page.locator(VET_ROW_SELECTOR);
		int rowCount = rows.count();

		for (int i = 0; i < rowCount; i++) {
			var nameCell = rows.nth(i).locator("td:first-child");
			if (nameCell.textContent().contains(vetName)) {
				return rows.nth(i).locator("td:nth-child(2)").textContent().trim();
			}
		}

		return "";
	}

	/**
	 * Verify this is the veterinarians page
	 */
	public boolean isVeterinariansPage() {
		return getCurrentUrl().contains("/vets") && isVetsTableVisible();
	}

}