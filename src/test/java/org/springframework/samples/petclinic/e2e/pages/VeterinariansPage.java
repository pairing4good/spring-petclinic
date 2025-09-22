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
 * Page Object Model for the Veterinarians page.
 */
public class VeterinariansPage extends BasePage {

	private final Locator veterinariansHeading;

	private final Locator vetsTable;

	private final Locator paginationControls;

	public VeterinariansPage(Page page) {
		super(page);
		this.veterinariansHeading = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Veterinarians"));
		this.vetsTable = page.getByRole(AriaRole.TABLE);
		this.paginationControls = page.locator("div").filter(new Locator.FilterOptions().setHasText("pages"));
	}

	/**
	 * Check if this is the Veterinarians page
	 */
	public boolean isVeterinariansPage() {
		return veterinariansHeading.isVisible();
	}

	/**
	 * Get the number of veterinarians displayed
	 */
	public int getVetsCount() {
		return vetsTable.locator("tbody tr").count();
	}

	/**
	 * Get veterinarian name by index
	 */
	public String getVetName(int index) {
		return vetsTable.locator("tbody tr").nth(index).locator("td").first().textContent();
	}

	/**
	 * Get veterinarian specialties by index
	 */
	public String getVetSpecialties(int index) {
		return vetsTable.locator("tbody tr").nth(index).locator("td").nth(1).textContent();
	}

	/**
	 * Check if pagination is present
	 */
	public boolean isPaginationPresent() {
		return paginationControls.isVisible();
	}

	/**
	 * Go to next page if available
	 */
	public VeterinariansPage goToNextPage() {
		Locator nextPageLink = page.locator("a").filter(new Locator.FilterOptions().setHasText("2"));
		if (nextPageLink.isVisible()) {
			nextPageLink.click();
		}
		return this;
	}

	/**
	 * Check if there are any veterinarians
	 */
	public boolean hasVets() {
		return getVetsCount() > 0;
	}

	/**
	 * Check if a specific vet exists by name
	 */
	public boolean hasVetWithName(String name) {
		for (int i = 0; i < getVetsCount(); i++) {
			if (getVetName(i).contains(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a specific vet has specialties
	 */
	public boolean vetHasSpecialties(int index) {
		String specialties = getVetSpecialties(index);
		return !specialties.trim().equals("none");
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		veterinariansHeading.waitFor();
		vetsTable.waitFor();
	}

}