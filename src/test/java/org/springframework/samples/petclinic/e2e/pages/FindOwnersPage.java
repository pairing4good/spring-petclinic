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
 * Page Object Model for the Find Owners page.
 */
public class FindOwnersPage extends BasePage {

	private final Locator findOwnersHeading;

	private final Locator lastNameInput;

	private final Locator findOwnerButton;

	private final Locator addOwnerLink;

	public FindOwnersPage(Page page) {
		super(page);
		this.findOwnersHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Find Owners"));
		this.lastNameInput = page.getByRole(AriaRole.TEXTBOX);
		this.findOwnerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
		this.addOwnerLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Owner"));
	}

	/**
	 * Check if this is the Find Owners page
	 */
	public boolean isFindOwnersPage() {
		return findOwnersHeading.isVisible();
	}

	/**
	 * Enter a last name in the search field
	 */
	public FindOwnersPage enterLastName(String lastName) {
		lastNameInput.fill(lastName);
		return this;
	}

	/**
	 * Clear the last name field
	 */
	public FindOwnersPage clearLastName() {
		lastNameInput.clear();
		return this;
	}

	/**
	 * Click the Find Owner button to search
	 */
	public OwnersListPage clickFindOwner() {
		findOwnerButton.click();
		return new OwnersListPage(page);
	}

	/**
	 * Click the Add Owner link
	 */
	public CreateOwnerPage clickAddOwner() {
		addOwnerLink.click();
		return new CreateOwnerPage(page);
	}

	/**
	 * Search for owners with empty criteria (should return all owners)
	 */
	public OwnersListPage searchAllOwners() {
		return clearLastName().clickFindOwner();
	}

	/**
	 * Search for owners by last name
	 */
	public OwnersListPage searchByLastName(String lastName) {
		return enterLastName(lastName).clickFindOwner();
	}

	@Override
	public void waitForPageLoad() {
		super.waitForPageLoad();
		findOwnersHeading.waitFor();
	}

}