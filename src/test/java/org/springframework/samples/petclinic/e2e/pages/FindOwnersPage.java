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
 * Page Object Model for the Find Owners page
 */
public class FindOwnersPage extends BasePage {

	public FindOwnersPage(Page page) {
		super(page);
	}

	// Page elements
	private Locator pageHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Find Owners"));
	}

	private Locator lastNameInput() {
		return page.locator("#lastName");
	}

	private Locator findOwnerButton() {
		return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"));
	}

	private Locator addOwnerLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Owner"));
	}

	// Actions
	public FindOwnersPage enterLastName(String lastName) {
		lastNameInput().fill(lastName);
		return this;
	}

	public void clickFindOwner() {
		findOwnerButton().click();
		// Wait for navigation or results
		page.waitForLoadState();
	}

	public OwnerFormPage clickAddOwner() {
		addOwnerLink().click();
		return new OwnerFormPage(page);
	}

	// Search actions
	public void searchForOwner(String lastName) {
		enterLastName(lastName);
		clickFindOwner();
	}

	// Verification methods
	public boolean isPageHeadingVisible() {
		return pageHeading().isVisible();
	}

	public String getPageHeadingText() {
		return pageHeading().textContent();
	}

	public boolean isFindOwnerButtonVisible() {
		return findOwnerButton().isVisible();
	}

	public boolean isAddOwnerLinkVisible() {
		return addOwnerLink().isVisible();
	}

	public String getLastNameInputValue() {
		return lastNameInput().inputValue();
	}

}