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

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object Model for the Owners List page. Handles display and interaction with owner
 * search results.
 */
public class OwnersListPage extends BasePage {

	// Locators for owners list elements
	private static final String OWNERS_TABLE_SELECTOR = "table#owners";

	private static final String OWNER_ROW_SELECTOR = "table#owners tbody tr";

	private static final String OWNER_NAME_LINK_SELECTOR = "table#owners tbody tr td a";

	private static final String NO_RESULTS_MESSAGE_SELECTOR = "p:has-text('has not been found')";

	public OwnersListPage(Page page) {
		super(page);
	}

	/**
	 * Check if the owners table is visible
	 */
	public boolean isOwnersTableVisible() {
		return isElementVisible(OWNERS_TABLE_SELECTOR);
	}

	/**
	 * Get the number of owners displayed in the list
	 */
	public int getOwnersCount() {
		if (!isOwnersTableVisible()) {
			return 0;
		}
		return page.locator(OWNER_ROW_SELECTOR).count();
	}

	/**
	 * Click on the first owner in the list to view details
	 */
	public OwnerDetailsPage clickFirstOwner() {
		if (getOwnersCount() > 0) {
			page.locator(OWNER_NAME_LINK_SELECTOR).first().click();
			waitForPageLoad();
			return new OwnerDetailsPage(page);
		}
		throw new RuntimeException("No owners found to click");
	}

	/**
	 * Click on a specific owner by name
	 */
	public OwnerDetailsPage clickOwnerByName(String ownerName) {
		// Find owner link containing the specified name
		Locator ownerLink = page.locator(OWNER_NAME_LINK_SELECTOR)
			.filter(new Locator.FilterOptions().setHasText(ownerName));
		if (ownerLink.count() > 0) {
			ownerLink.first().click();
			waitForPageLoad();
			return new OwnerDetailsPage(page);
		}
		throw new RuntimeException("Owner with name '" + ownerName + "' not found");
	}

	/**
	 * Check if a specific owner exists in the list
	 */
	public boolean isOwnerInList(String ownerName) {
		if (!isOwnersTableVisible()) {
			return false;
		}
		return page.locator(OWNER_NAME_LINK_SELECTOR)
			.filter(new Locator.FilterOptions().setHasText(ownerName))
			.count() > 0;
	}

	/**
	 * Check if "no results found" message is displayed
	 */
	public boolean isNoResultsMessageDisplayed() {
		return isElementVisible(NO_RESULTS_MESSAGE_SELECTOR);
	}

	/**
	 * Get all owner names from the list
	 */
	public String[] getAllOwnerNames() {
		if (!isOwnersTableVisible()) {
			return new String[0];
		}
		Locator ownerLinks = page.locator(OWNER_NAME_LINK_SELECTOR);
		int count = ownerLinks.count();
		String[] names = new String[count];
		for (int i = 0; i < count; i++) {
			names[i] = ownerLinks.nth(i).textContent().trim();
		}
		return names;
	}

	/**
	 * Verify this is the owners list page (or search results page)
	 */
	public boolean isOwnersListPage() {
		return isOwnersTableVisible() || isNoResultsMessageDisplayed();
	}

}