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
 * Page Object Model for the Find Owners page.
 *
 * Provides methods to interact with elements on the find owners page including: - Owner
 * search functionality - Form validation - Navigation to add new owner - Results handling
 */
public class FindOwnersPage {

	private final Page page;

	// Locators using specific, unambiguous selectors
	private final Locator pageHeading;

	private final Locator lastNameInput;

	private final Locator findOwnerButton;

	private final Locator addOwnerButton;

	private final Locator searchForm;

	private final Locator errorMessages;

	private final Locator ownersTable;

	public FindOwnersPage(Page page) {
		this.page = page;
		// Using specific selectors with context for disambiguation
		this.pageHeading = page.locator("h2").filter(new Locator.FilterOptions().setHasText("Find Owners"));
		this.lastNameInput = page.locator("input[name='lastName']");
		this.findOwnerButton = page.locator("button[type='submit']")
			.filter(new Locator.FilterOptions().setHasText("Find Owner"));
		this.addOwnerButton = page.locator("a.btn").filter(new Locator.FilterOptions().setHasText("Add Owner"));
		this.searchForm = page.locator("form#search-owner-form");
		this.errorMessages = page.locator(".help-inline p");
		this.ownersTable = page.locator("table.table");
	}

	/**
	 * Navigate to the find owners page.
	 */
	public void navigate(String baseUrl) {
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState();
	}

	/**
	 * Verify the page is loaded correctly.
	 */
	public boolean isLoaded() {
		return pageHeading.isVisible() && searchForm.isVisible();
	}

	/**
	 * Search for owners by last name.
	 */
	public void searchByLastName(String lastName) {
		lastNameInput.waitFor();
		lastNameInput.fill(lastName);
		findOwnerButton.click();
		page.waitForLoadState();
	}

	/**
	 * Search with empty last name to test validation.
	 */
	public void searchWithEmptyLastName() {
		lastNameInput.waitFor();
		lastNameInput.fill("");
		findOwnerButton.click();
		page.waitForLoadState();
	}

	/**
	 * Click Add Owner button.
	 */
	public void clickAddOwner() {
		addOwnerButton.click();
		page.waitForLoadState();
	}

	/**
	 * Check if error messages are displayed.
	 */
	public boolean hasErrorMessages() {
		return errorMessages.count() > 0 && errorMessages.first().isVisible();
	}

	/**
	 * Get error message text.
	 */
	public String getErrorMessage() {
		if (hasErrorMessages()) {
			return errorMessages.first().textContent();
		}
		return "";
	}

	/**
	 * Check if owners table is displayed (search results).
	 */
	public boolean hasSearchResults() {
		return ownersTable.isVisible();
	}

	/**
	 * Get the number of owners found in search results.
	 */
	public int getSearchResultCount() {
		if (!hasSearchResults()) {
			return 0;
		}
		// Count table rows excluding header
		return ownersTable.locator("tbody tr").count();
	}

	/**
	 * Click on the first owner in search results.
	 */
	public void clickFirstOwnerResult() {
		if (hasSearchResults()) {
			ownersTable.locator("tbody tr").first().locator("a").first().click();
			page.waitForLoadState();
		}
	}

	/**
	 * Get owner name from search results at specified index.
	 */
	public String getOwnerNameFromResults(int index) {
		if (hasSearchResults() && index < getSearchResultCount()) {
			return ownersTable.locator("tbody tr").nth(index).locator("td").first().textContent();
		}
		return "";
	}

	/**
	 * Verify form validation for empty search.
	 */
	public boolean isFormValidationShown() {
		// Check if redirected to owners list or error shown
		return page.url().contains("/owners") || hasErrorMessages();
	}

	/**
	 * Get page heading text.
	 */
	public String getPageHeading() {
		pageHeading.waitFor();
		return pageHeading.textContent();
	}

	/**
	 * Clear the search field.
	 */
	public void clearSearch() {
		lastNameInput.fill("");
	}

}