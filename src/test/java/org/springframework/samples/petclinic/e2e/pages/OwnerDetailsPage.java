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
 * Page Object for the Owner Details page.
 */
public class OwnerDetailsPage extends BasePage {

	private final Locator ownerInformationHeading;

	private final Locator ownerName;

	private final Locator ownerAddress;

	private final Locator ownerCity;

	private final Locator ownerTelephone;

	private final Locator editOwnerLink;

	private final Locator addNewPetLink;

	private final Locator petsAndVisitsHeading;

	private final Locator successMessage;

	private final Locator errorMessage;

	public OwnerDetailsPage(Page page, String baseUrl) {
		super(page, baseUrl);
		this.ownerInformationHeading = page.getByRole("heading",
				new Page.GetByRoleOptions().setName("Owner Information"));
		this.ownerName = page.locator("table.table-striped tr:nth-child(1) td");
		this.ownerAddress = page.locator("table.table-striped tr:nth-child(2) td");
		this.ownerCity = page.locator("table.table-striped tr:nth-child(3) td");
		this.ownerTelephone = page.locator("table.table-striped tr:nth-child(4) td");
		this.editOwnerLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Edit Owner"));
		this.addNewPetLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Add New Pet"));
		this.petsAndVisitsHeading = page.getByRole("heading", new Page.GetByRoleOptions().setName("Pets and Visits"));
		this.successMessage = page.locator("#success-message");
		this.errorMessage = page.locator("#error-message");
	}

	public boolean isOwnerInformationVisible() {
		return ownerInformationHeading.isVisible();
	}

	public String getOwnerName() {
		return ownerName.textContent();
	}

	public String getOwnerAddress() {
		return ownerAddress.textContent();
	}

	public String getOwnerCity() {
		return ownerCity.textContent();
	}

	public String getOwnerTelephone() {
		return ownerTelephone.textContent();
	}

	public void clickEditOwner() {
		editOwnerLink.click();
		waitForPageLoad();
	}

	public void clickAddNewPet() {
		addNewPetLink.click();
		waitForPageLoad();
	}

	public boolean isPetsAndVisitsHeadingVisible() {
		return petsAndVisitsHeading.isVisible();
	}

	public boolean isSuccessMessageVisible() {
		return successMessage.isVisible();
	}

	public boolean isErrorMessageVisible() {
		return errorMessage.isVisible();
	}

	public String getSuccessMessage() {
		return successMessage.textContent();
	}

	public String getErrorMessage() {
		return errorMessage.textContent();
	}

	public boolean isEditOwnerLinkVisible() {
		return editOwnerLink.isVisible();
	}

	public boolean isAddNewPetLinkVisible() {
		return addNewPetLink.isVisible();
	}

	public void clickAddVisitForPet(String petName) {
		// Find the specific pet and click its Add Visit link
		page.locator("text=" + petName).locator("..").locator("text=Add Visit").click();
		waitForPageLoad();
	}

	public void clickEditPet(String petName) {
		// Find the specific pet and click its Edit Pet link
		page.locator("text=" + petName).locator("..").locator("text=Edit Pet").click();
		waitForPageLoad();
	}

}