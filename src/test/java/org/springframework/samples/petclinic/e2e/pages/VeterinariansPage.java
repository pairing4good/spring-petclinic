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
 * Page Object Model for the Veterinarians page.
 *
 * Provides methods to interact with elements on the veterinarians page including: -
 * Veterinarian information display - Specialties display - Table navigation and sorting
 */
public class VeterinariansPage {

	private final Page page;

	// Locators using specific, unambiguous selectors
	private final Locator pageHeading;

	private final Locator vetsTable;

	private final Locator tableBody;

	public VeterinariansPage(Page page) {
		this.page = page;
		// Using specific selectors for veterinarians page elements
		this.pageHeading = page.locator("h2").filter(new Locator.FilterOptions().setHasText("Veterinarians"));
		this.vetsTable = page.locator("table");
		this.tableBody = vetsTable.locator("tbody");
	}

	/**
	 * Navigate to the veterinarians page.
	 */
	public void navigate(String baseUrl) {
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState();
	}

	/**
	 * Verify the page is loaded correctly.
	 */
	public boolean isLoaded() {
		return pageHeading.isVisible() && vetsTable.isVisible();
	}

	/**
	 * Get the page heading text.
	 */
	public String getPageHeading() {
		pageHeading.waitFor();
		return pageHeading.textContent();
	}

	/**
	 * Get the number of veterinarians displayed.
	 */
	public int getVetCount() {
		if (!vetsTable.isVisible()) {
			return 0;
		}
		return tableBody.locator("tr").count();
	}

	/**
	 * Get veterinarian name at specified index.
	 */
	public String getVetName(int index) {
		if (getVetCount() > index) {
			return tableBody.locator("tr").nth(index).locator("td").first().textContent();
		}
		return "";
	}

	/**
	 * Get veterinarian specialties at specified index.
	 */
	public String getVetSpecialties(int index) {
		if (getVetCount() > index) {
			// Specialties are typically in the second column
			return tableBody.locator("tr").nth(index).locator("td").nth(1).textContent();
		}
		return "";
	}

	/**
	 * Check if veterinarians table has data.
	 */
	public boolean hasVeterinarianData() {
		return getVetCount() > 0;
	}

	/**
	 * Verify table headers are displayed.
	 */
	public boolean hasTableHeaders() {
		return vetsTable.locator("thead, th").count() > 0;
	}

	/**
	 * Check if a specific veterinarian exists by name.
	 */
	public boolean hasVeterinarianWithName(String name) {
		return vetsTable.locator("td").filter(new Locator.FilterOptions().setHasText(name)).count() > 0;
	}

	/**
	 * Get all veterinarian names as a string for easier verification.
	 */
	public String getAllVetNames() {
		if (!hasVeterinarianData()) {
			return "";
		}
		StringBuilder names = new StringBuilder();
		int count = getVetCount();
		for (int i = 0; i < count; i++) {
			if (i > 0)
				names.append(", ");
			names.append(getVetName(i));
		}
		return names.toString();
	}

	/**
	 * Check if the page shows appropriate content when no vets are available.
	 */
	public boolean hasEmptyStateHandling() {
		return !hasVeterinarianData() && (vetsTable.isVisible() || page.locator("text=No veterinarians").isVisible());
	}

}