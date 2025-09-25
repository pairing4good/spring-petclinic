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

import com.microsoft.playwright.Page;

/**
 * Page Object for the Find Owners page. Contains locators and actions for searching
 * owners.
 */
public class FindOwnersPage extends BasePage {

	// Specific locators using unique identifiers to avoid ambiguity
	private static final String PAGE_HEADING = "h2:has-text('Find Owners')";

	private static final String LAST_NAME_INPUT = "input[name='lastName']";

	private static final String FIND_OWNER_BUTTON = "button:has-text('Find Owner')";

	private static final String ADD_OWNER_LINK = "a:has-text('Add Owner')";

	public FindOwnersPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Navigate directly to the Find Owners page
	 */
	public FindOwnersPage open() {
		navigateTo("/owners/find");
		waitForPageLoad();
		return this;
	}

	/**
	 * Wait for Find Owners page specific elements to load
	 */
	public void waitForPageLoad() {
		waitForElement(PAGE_HEADING);
		waitForElement(LAST_NAME_INPUT);
		waitForElement(FIND_OWNER_BUTTON);
	}

	/**
	 * Enter last name in the search field
	 */
	public FindOwnersPage enterLastName(String lastName) {
		page.locator(LAST_NAME_INPUT).fill(lastName);
		return this;
	}

	/**
	 * Click the Find Owner button to search
	 */
	public void clickFindOwner() {
		page.locator(FIND_OWNER_BUTTON).click();
		// Wait for either search results or owner details page to load
		page.waitForLoadState();
	}

	/**
	 * Search for owners by last name and return results page
	 */
	public OwnersListPage searchOwners(String lastName) {
		enterLastName(lastName);
		clickFindOwner();
		return new OwnersListPage(page, baseUrl);
	}

	/**
	 * Search for a single owner that should redirect to owner details
	 */
	public OwnerDetailsPage searchSingleOwner(String lastName) {
		enterLastName(lastName);
		clickFindOwner();
		return new OwnerDetailsPage(page, baseUrl);
	}

	/**
	 * Click Add Owner link to go to add owner form
	 */
	public AddOwnerPage clickAddOwner() {
		page.locator(ADD_OWNER_LINK).click();
		return new AddOwnerPage(page, baseUrl);
	}

	/**
	 * Get the current value in the last name field
	 */
	public String getLastNameValue() {
		return page.locator(LAST_NAME_INPUT).inputValue();
	}

	/**
	 * Clear the last name field
	 */
	public FindOwnersPage clearLastName() {
		page.locator(LAST_NAME_INPUT).fill("");
		return this;
	}

	/**
	 * Check if the form is displayed correctly
	 */
	public boolean isFormDisplayed() {
		return page.locator(PAGE_HEADING).isVisible() && page.locator(LAST_NAME_INPUT).isVisible()
				&& page.locator(FIND_OWNER_BUTTON).isVisible() && page.locator(ADD_OWNER_LINK).isVisible();
	}

	/**
	 * Navigate to Veterinarians page using navigation menu
	 */
	public VeterinariansPage goToVeterinarians() {
		clickNavigationItem("Veterinarians");
		return new VeterinariansPage(page, baseUrl);
	}

}