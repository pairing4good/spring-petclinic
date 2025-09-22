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
import java.util.List;

/**
 * Page Object Model for the Owners List page.
 */
public class OwnersListPage extends BasePage {

	private final Locator ownersHeading;

	private final Locator ownersTable;

	private final Locator paginationControls;

	private final Locator nextPageLink;

	private final Locator previousPageLink;

	private final Locator firstPageLink;

	private final Locator lastPageLink;

	public OwnersListPage(Page page) {
		super(page);
		this.ownersHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Owners"));
		this.ownersTable = page.getByRole(AriaRole.TABLE);
		this.paginationControls = page.locator("div").filter(new Locator.FilterOptions().setHasText("pages"));
		this.nextPageLink = page.locator("a").filter(new Locator.FilterOptions().setHasText("2"));
		this.previousPageLink = page.locator("a[title='previous']");
		this.firstPageLink = page.locator("a[title='first']");
		this.lastPageLink = page.locator("a[title='last']");
	}

	/**
	 * Check if this is the Owners List page
	 */
	public boolean isOwnersListPage() {
		return ownersHeading.isVisible() && ownersTable.isVisible();
	}

	/**
	 * Get the number of owners displayed in the table
	 */
	public int getOwnersCount() {
		return ownersTable.locator("tbody tr").count();
	}

	/**
	 * Get all owner names from the table
	 */
	public List<String> getOwnerNames() {
		return ownersTable.locator("tbody tr td:first-child a").allTextContents();
	}

	/**
	 * Click on an owner by name
	 */
	public OwnerDetailsPage clickOwnerByName(String ownerName) {
		ownersTable.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(ownerName)).click();
		return new OwnerDetailsPage(page);
	}

	/**
	 * Check if pagination is present
	 */
	public boolean isPaginationPresent() {
		return paginationControls.isVisible();
	}

	/**
	 * Go to the next page if available
	 */
	public OwnersListPage goToNextPage() {
		if (nextPageLink.isVisible()) {
			nextPageLink.click();
		}
		return this;
	}

	/**
	 * Go to the previous page if available
	 */
	public OwnersListPage goToPreviousPage() {
		if (previousPageLink.isVisible()) {
			previousPageLink.click();
		}
		return this;
	}

	/**
	 * Go to the first page if available
	 */
	public OwnersListPage goToFirstPage() {
		if (firstPageLink.isVisible()) {
			firstPageLink.click();
		}
		return this;
	}

	/**
	 * Go to the last page if available
	 */
	public OwnersListPage goToLastPage() {
		if (lastPageLink.isVisible()) {
			lastPageLink.click();
		}
		return this;
	}

	/**
	 * Check if there are any owners displayed
	 */
	public boolean hasOwners() {
		return getOwnersCount() > 0;
	}

	/**
	 * Get owner info from a specific row
	 */
	public String getOwnerInfo(int rowIndex) {
		Locator row = ownersTable.locator("tbody tr").nth(rowIndex);
		return row.textContent();
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		ownersHeading.waitFor();
		ownersTable.waitFor();
	}

}