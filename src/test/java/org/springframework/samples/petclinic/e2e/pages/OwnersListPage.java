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

import java.util.List;
import java.util.ArrayList;

/**
 * Page Object Model for the Owners List page of the Pet Clinic application. Provides
 * methods to interact with the owners table and pagination.
 */
public class OwnersListPage extends BasePage {

	// Locators for owners list page elements - using specific, unambiguous selectors
	private static final String PAGE_HEADING = "h2";

	private static final String OWNERS_TABLE = "table";

	private static final String OWNER_ROWS = "table tbody tr"; // Rows in the table body

	private static final String OWNER_NAME_LINKS = "table tbody tr td:first-child a"; // First
																						// column
																						// links

	private static final String PAGINATION_CONTAINER = "div"; // Container with pagination
																// info

	private static final String PAGE_LINKS = "a[href*='page=']"; // Links with page
																	// parameter

	private static final String CURRENT_PAGE_SPAN = "span:not([class])"; // Current page
																			// indicator

	private static final String NAVIGATION_CONTROLS = "a[class*='fa']"; // Navigation
																		// arrows

	public OwnersListPage(String baseUrl) {
		super(baseUrl);
		// Don't navigate automatically as this page is reached via search
		waitForElement(OWNERS_TABLE);
	}

	/**
	 * Verify we're on the Owners List page by checking the heading
	 */
	public boolean isOwnersListPageDisplayed() {
		try {
			return page.locator(PAGE_HEADING).textContent().trim().equals("Owners");
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the page heading text
	 */
	public String getPageHeading() {
		return page.locator(PAGE_HEADING).textContent().trim();
	}

	/**
	 * Get the number of owners displayed in the current page
	 */
	public int getOwnerCount() {
		return page.locator(OWNER_ROWS).count();
	}

	/**
	 * Get all owner names from the current page
	 */
	public List<String> getOwnerNames() {
		List<String> names = new ArrayList<>();
		Locator nameLinks = page.locator(OWNER_NAME_LINKS);
		int count = nameLinks.count();

		for (int i = 0; i < count; i++) {
			names.add(nameLinks.nth(i).textContent().trim());
		}

		return names;
	}

	/**
	 * Click on an owner name to view their details
	 * @param ownerName the name of the owner to click
	 */
	public OwnerDetailsPage clickOwnerByName(String ownerName) {
		// Find the specific owner link by text content
		page.locator(OWNER_NAME_LINKS).filter(new Locator.FilterOptions().setHasText(ownerName)).first().click();
		return new OwnerDetailsPage(baseUrl);
	}

	/**
	 * Click on the first owner in the list
	 */
	public OwnerDetailsPage clickFirstOwner() {
		page.locator(OWNER_NAME_LINKS).first().click();
		return new OwnerDetailsPage(baseUrl);
	}

	/**
	 * Get owner information for a specific row
	 * @param rowIndex the zero-based index of the row
	 */
	public OwnerInfo getOwnerInfo(int rowIndex) {
		Locator row = page.locator(OWNER_ROWS).nth(rowIndex);
		Locator cells = row.locator("td");

		if (cells.count() >= 5) {
			return new OwnerInfo(cells.nth(0).textContent().trim(), // Name
					cells.nth(1).textContent().trim(), // Address
					cells.nth(2).textContent().trim(), // City
					cells.nth(3).textContent().trim(), // Telephone
					cells.nth(4).textContent().trim() // Pets
			);
		}

		throw new RuntimeException("Invalid owner row data at index " + rowIndex);
	}

	/**
	 * Check if pagination is displayed
	 */
	public boolean isPaginationDisplayed() {
		try {
			// Look for pagination indicators (pages, brackets, numbers)
			return page.locator("text=pages").isVisible() || page.locator(PAGE_LINKS).count() > 0;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the current page number
	 */
	public int getCurrentPageNumber() {
		try {
			// Current page is usually shown as a span without links
			String pageText = page.locator(CURRENT_PAGE_SPAN).first().textContent();
			return Integer.parseInt(pageText.trim());
		}
		catch (Exception e) {
			return 1; // Default to page 1 if not found
		}
	}

	/**
	 * Click on a specific page number
	 * @param pageNumber the page number to navigate to
	 */
	public void goToPage(int pageNumber) {
		page.locator("a[href*='page=" + pageNumber + "']").click();
		waitForElement(OWNERS_TABLE);
	}

	/**
	 * Click the next page link if available
	 */
	public boolean goToNextPage() {
		try {
			// Look for forward navigation arrow
			Locator nextLink = page.locator("a[class*='fa-step-forward'], a[class*='fa-fast-forward']").first();
			if (nextLink.isVisible()) {
				nextLink.click();
				waitForElement(OWNERS_TABLE);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click the previous page link if available
	 */
	public boolean goToPreviousPage() {
		try {
			// Look for backward navigation arrow
			Locator prevLink = page.locator("a[class*='fa-step-backward'], a[class*='fa-fast-backward']").first();
			if (prevLink.isVisible()) {
				prevLink.click();
				waitForElement(OWNERS_TABLE);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if the owners table is displayed
	 */
	public boolean isOwnersTableDisplayed() {
		return page.locator(OWNERS_TABLE).isVisible();
	}

	/**
	 * Data class to hold owner information from the table
	 */
	public static class OwnerInfo {

		public final String name;

		public final String address;

		public final String city;

		public final String telephone;

		public final String pets;

		public OwnerInfo(String name, String address, String city, String telephone, String pets) {
			this.name = name;
			this.address = address;
			this.city = city;
			this.telephone = telephone;
			this.pets = pets;
		}

		@Override
		public String toString() {
			return String.format("OwnerInfo{name='%s', address='%s', city='%s', telephone='%s', pets='%s'}", name,
					address, city, telephone, pets);
		}

	}

}