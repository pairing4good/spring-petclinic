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

import java.util.List;

/**
 * Page Object for the Owners List page showing search results. Contains locators and
 * actions for the owners search results table.
 */
public class OwnersListPage extends BasePage {

	// Specific locators for the owners list table
	private static final String PAGE_HEADING = "h2:has-text('Owners')";

	private static final String OWNERS_TABLE = "table";

	private static final String OWNER_ROWS = "table tbody tr";

	private static final String OWNER_NAME_LINKS = "table tbody tr td:first-child a";

	public OwnersListPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Wait for owners list page to load
	 */
	public void waitForPageLoad() {
		waitForElement(PAGE_HEADING);
		waitForElement(OWNERS_TABLE);
	}

	/**
	 * Get the number of owners in the search results
	 */
	public int getOwnerCount() {
		return page.locator(OWNER_ROWS).count();
	}

	/**
	 * Get all owner names from the search results
	 */
	public List<String> getOwnerNames() {
		return page.locator(OWNER_NAME_LINKS).allTextContents();
	}

	/**
	 * Click on an owner by name to view details
	 */
	public OwnerDetailsPage clickOwnerByName(String ownerName) {
		// Use filter to find the specific owner link to avoid ambiguity
		page.locator(OWNER_NAME_LINKS).filter(new Locator.FilterOptions().setHasText(ownerName)).click();
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Click on the first owner in the list
	 */
	public OwnerDetailsPage clickFirstOwner() {
		page.locator(OWNER_NAME_LINKS).first().click();
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Get owner information for a specific row
	 */
	public OwnerInfo getOwnerInfo(int rowIndex) {
		Locator row = page.locator(OWNER_ROWS).nth(rowIndex);
		String name = row.locator("td").nth(0).textContent();
		String address = row.locator("td").nth(1).textContent();
		String city = row.locator("td").nth(2).textContent();
		String telephone = row.locator("td").nth(3).textContent();
		String pets = row.locator("td").nth(4).textContent();

		return new OwnerInfo(name, address, city, telephone, pets);
	}

	/**
	 * Check if owners were found (table has data rows)
	 */
	public boolean hasOwners() {
		return getOwnerCount() > 0;
	}

	/**
	 * Check if a specific owner name appears in the results
	 */
	public boolean containsOwner(String ownerName) {
		return getOwnerNames().stream().anyMatch(name -> name.contains(ownerName));
	}

	/**
	 * Data class to hold owner information from the table
	 */
	public static class OwnerInfo {

		private final String name;

		private final String address;

		private final String city;

		private final String telephone;

		private final String pets;

		public OwnerInfo(String name, String address, String city, String telephone, String pets) {
			this.name = name;
			this.address = address;
			this.city = city;
			this.telephone = telephone;
			this.pets = pets;
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public String getCity() {
			return city;
		}

		public String getTelephone() {
			return telephone;
		}

		public String getPets() {
			return pets;
		}

	}

}