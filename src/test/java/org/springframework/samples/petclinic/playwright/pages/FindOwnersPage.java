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
 * Page Object Model for the Find Owners page. Handles owner search functionality and
 * navigation to owner creation.
 */
public class FindOwnersPage extends BasePage {

	// Specific locators for find owners page elements
	private static final String LAST_NAME_INPUT_SELECTOR = "input[name='lastName']";

	private static final String FIND_OWNER_BUTTON_SELECTOR = "button[type='submit']";

	private static final String ADD_OWNER_LINK_SELECTOR = "a[href='/owners/new']";

	public FindOwnersPage(Page page) {
		super(page);
	}

	/**
	 * Search for owners by last name
	 */
	public void searchByLastName(String lastName) {
		page.locator(LAST_NAME_INPUT_SELECTOR).fill(lastName);
		page.locator(FIND_OWNER_BUTTON_SELECTOR).click();
		waitForPageLoad();
	}

	/**
	 * Search for all owners (empty search)
	 */
	public OwnersListPage searchAllOwners() {
		page.locator(LAST_NAME_INPUT_SELECTOR).fill("");
		page.locator(FIND_OWNER_BUTTON_SELECTOR).click();
		waitForPageLoad();
		return new OwnersListPage(page);
	}

	/**
	 * Navigate to Add Owner page
	 */
	public AddOwnerPage navigateToAddOwner() {
		page.locator(ADD_OWNER_LINK_SELECTOR).click();
		waitForPageLoad();
		return new AddOwnerPage(page);
	}

	/**
	 * Check if the search form is visible
	 */
	public boolean isSearchFormVisible() {
		return isElementVisible(LAST_NAME_INPUT_SELECTOR) && isElementVisible(FIND_OWNER_BUTTON_SELECTOR);
	}

	/**
	 * Check if the Add Owner link is visible
	 */
	public boolean isAddOwnerLinkVisible() {
		return isElementVisible(ADD_OWNER_LINK_SELECTOR);
	}

	/**
	 * Get the placeholder text of the last name input
	 */
	public String getLastNameInputPlaceholder() {
		return page.locator(LAST_NAME_INPUT_SELECTOR).getAttribute("placeholder");
	}

	/**
	 * Verify this is the find owners page
	 */
	public boolean isFindOwnersPage() {
		return getCurrentUrl().contains("/owners/find") && isSearchFormVisible();
	}

	/**
	 * Clear the search input
	 */
	public void clearSearch() {
		page.locator(LAST_NAME_INPUT_SELECTOR).fill("");
	}

}