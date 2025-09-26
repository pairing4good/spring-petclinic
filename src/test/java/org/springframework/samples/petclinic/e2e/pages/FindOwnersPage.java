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

/**
 * Page Object Model for the Find Owners page of the Pet Clinic application. Provides
 * methods to search for owners and navigate to owner-related pages.
 */
public class FindOwnersPage extends BasePage {

	// Locators for find owners page elements - using specific, unambiguous selectors
	private static final String PAGE_HEADING = "h2";

	private static final String LAST_NAME_INPUT = "input[type='text']"; // Only text input
																		// on this page

	private static final String FIND_OWNER_BUTTON = "button[type='submit']"; // Only
																				// submit
																				// button

	private static final String ADD_OWNER_LINK = "a[href='/owners/new']"; // Specific href
																			// for Add
																			// Owner

	public FindOwnersPage(String baseUrl) {
		super(baseUrl);
		navigateTo("/owners/find");
	}

	/**
	 * Verify we're on the Find Owners page by checking the heading
	 */
	public boolean isFindOwnersPageDisplayed() {
		try {
			return page.locator(PAGE_HEADING).textContent().trim().equals("Find Owners");
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
	 * Enter last name in the search field
	 * @param lastName the last name to search for
	 */
	public void enterLastName(String lastName) {
		page.locator(LAST_NAME_INPUT).clear();
		page.locator(LAST_NAME_INPUT).fill(lastName);
	}

	/**
	 * Click the Find Owner button to search Returns OwnersListPage if multiple results or
	 * OwnerDetailsPage if single result
	 */
	public BasePage clickFindOwner() {
		page.locator(FIND_OWNER_BUTTON).click();

		// Wait for navigation and determine which page we landed on
		page.waitForLoadState();

		if (page.url().contains("/owners/") && !page.url().contains("/find")) {
			// We're on a specific owner details page
			return new OwnerDetailsPage(baseUrl);
		}
		else {
			// We're on the owners list page
			return new OwnersListPage(baseUrl);
		}
	}

	/**
	 * Search for owners with empty criteria (returns all owners)
	 */
	public OwnersListPage searchAllOwners() {
		enterLastName("");
		page.locator(FIND_OWNER_BUTTON).click();
		return new OwnersListPage(baseUrl);
	}

	/**
	 * Search for owners by last name
	 * @param lastName the last name to search for
	 */
	public BasePage searchByLastName(String lastName) {
		enterLastName(lastName);
		return clickFindOwner();
	}

	/**
	 * Click the Add Owner link to create a new owner
	 */
	public AddOwnerPage clickAddOwner() {
		page.locator(ADD_OWNER_LINK).click();
		return new AddOwnerPage(baseUrl);
	}

	/**
	 * Check if the last name input field is present and enabled
	 */
	public boolean isLastNameInputDisplayed() {
		return page.locator(LAST_NAME_INPUT).isVisible() && page.locator(LAST_NAME_INPUT).isEnabled();
	}

	/**
	 * Check if the Find Owner button is present and enabled
	 */
	public boolean isFindOwnerButtonDisplayed() {
		return page.locator(FIND_OWNER_BUTTON).isVisible() && page.locator(FIND_OWNER_BUTTON).isEnabled();
	}

	/**
	 * Check if the Add Owner link is present
	 */
	public boolean isAddOwnerLinkDisplayed() {
		return page.locator(ADD_OWNER_LINK).isVisible();
	}

	/**
	 * Get the current value in the last name input field
	 */
	public String getLastNameInputValue() {
		return page.locator(LAST_NAME_INPUT).inputValue();
	}

	/**
	 * Verify all form elements are present and functional
	 */
	public boolean areAllFormElementsPresent() {
		return isLastNameInputDisplayed() && isFindOwnerButtonDisplayed() && isAddOwnerLinkDisplayed();
	}

}