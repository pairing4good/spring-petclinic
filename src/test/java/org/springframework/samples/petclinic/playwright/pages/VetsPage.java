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

/**
 * Page Object Model for the veterinarians page.
 *
 * @author Copilot
 */
public class VetsPage extends BasePage {

	// Veterinarians table elements
	private final Locator vetsTable;

	private final Locator vetNames;

	private final Locator vetSpecialties;

	private final Locator pageHeading;

	public VetsPage(Page page, String baseUrl) {
		super(page, baseUrl);

		// Using specific table ID for disambiguation
		this.vetsTable = page.locator("#vets"); // Specific vets table ID
		this.vetNames = vetsTable.locator("tbody td:first-child");
		this.vetSpecialties = vetsTable.locator("tbody td:nth-child(2)");
		this.pageHeading = page.locator("h2").first();
	}

	/**
	 * Navigate to the veterinarians page.
	 */
	public void navigateToVets() {
		navigateTo("/vets");
	}

	/**
	 * Get the page heading text.
	 * @return page heading
	 */
	public String getPageHeading() {
		return getText(pageHeading);
	}

	/**
	 * Check if the veterinarians table is displayed.
	 * @return true if vets table is visible
	 */
	public boolean isVetsTableDisplayed() {
		return isVisible(vetsTable);
	}

	/**
	 * Get the number of veterinarians in the table.
	 * @return count of veterinarians
	 */
	public int getVetCount() {
		if (!isVisible(vetsTable)) {
			return 0;
		}
		return vetNames.count();
	}

	/**
	 * Get veterinarian name by index.
	 * @param index vet index (0-based)
	 * @return veterinarian name
	 */
	public String getVetName(int index) {
		return getText(vetNames.nth(index));
	}

	/**
	 * Get veterinarian specialties by index.
	 * @param index vet index (0-based)
	 * @return veterinarian specialties
	 */
	public String getVetSpecialties(int index) {
		return getText(vetSpecialties.nth(index));
	}

	/**
	 * Check if the veterinarians page is properly loaded.
	 * @return true if vets table and heading are present
	 */
	public boolean isVetsPageLoaded() {
		return isVisible(pageHeading) && isVisible(vetsTable);
	}

}