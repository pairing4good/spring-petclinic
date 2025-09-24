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
import com.microsoft.playwright.Locator;

/**
 * Page Object for the Find Owners page.
 */
public class FindOwnersPage extends BasePage {

	private final Locator findOwnersHeading;

	private final Locator lastNameInput;

	private final Locator findOwnerButton;

	private final Locator addOwnerLink;

	public FindOwnersPage(Page page, String baseUrl) {
		super(page, baseUrl);
		this.findOwnersHeading = page.getByRole("heading", new Page.GetByRoleOptions().setName("Find Owners"));
		this.lastNameInput = page.locator("#lastName");
		this.findOwnerButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Find Owner"));
		this.addOwnerLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Add Owner"));
	}

	public void navigateTo() {
		page.navigate(baseUrl + "/owners/find");
		waitForPageLoad();
	}

	public boolean isFindOwnersHeadingVisible() {
		return findOwnersHeading.isVisible();
	}

	public void enterLastName(String lastName) {
		lastNameInput.fill(lastName);
	}

	public void clearLastName() {
		lastNameInput.clear();
	}

	public void clickFindOwner() {
		findOwnerButton.click();
		waitForPageLoad();
	}

	public void clickAddOwner() {
		addOwnerLink.click();
		waitForPageLoad();
	}

	public boolean isLastNameInputVisible() {
		return lastNameInput.isVisible();
	}

	public boolean isFindOwnerButtonVisible() {
		return findOwnerButton.isVisible();
	}

	public boolean isAddOwnerLinkVisible() {
		return addOwnerLink.isVisible();
	}

	public String getLastNameValue() {
		return lastNameInput.inputValue();
	}

}