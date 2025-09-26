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
 * Page Object for the Find Owners page
 */
public class FindOwnersPage extends BasePage {

	public FindOwnersPage(Page page) {
		super(page);
	}

	public void navigate() {
		navigateTo("/owners/find");
	}

	public Locator getLastNameInput() {
		return page.locator("input[name='lastName']");
	}

	public Locator getFindOwnerButton() {
		return page.locator("button[type='submit']");
	}

	public Locator getAddOwnerButton() {
		return page.locator("a[href*='/owners/new']");
	}

	public void searchByLastName(String lastName) {
		getLastNameInput().fill(lastName);
		getFindOwnerButton().click();
	}

	public void searchAllOwners() {
		getFindOwnerButton().click();
	}

	public void clickAddOwner() {
		getAddOwnerButton().click();
	}

	public boolean hasNoResultsMessage() {
		return page.locator(".has-error, .error").isVisible();
	}

	public String getNoResultsMessage() {
		return page.locator(".has-error .help-block, .error").textContent();
	}

}