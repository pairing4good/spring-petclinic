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
 * Page Object Model for the Owner Details page
 */
public class OwnerDetailsPage extends BasePage {

	public OwnerDetailsPage(Page page) {
		super(page);
	}

	// Page elements
	private Locator ownerInformationHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Owner Information"));
	}

	private Locator petsAndVisitsHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Pets and Visits"));
	}

	private Locator editOwnerLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Edit Owner"));
	}

	private Locator addNewPetLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add New Pet"));
	}

	// Owner information table elements
	private Locator ownerTable() {
		return page.locator("table").first();
	}

	private Locator ownerName() {
		return ownerTable().locator("tr").filter(new Locator.FilterOptions().setHasText("Name")).locator("td").nth(1);
	}

	private Locator ownerAddress() {
		return ownerTable().locator("tr")
			.filter(new Locator.FilterOptions().setHasText("Address"))
			.locator("td")
			.nth(1);
	}

	private Locator ownerCity() {
		return ownerTable().locator("tr").filter(new Locator.FilterOptions().setHasText("City")).locator("td").nth(1);
	}

	private Locator ownerTelephone() {
		return ownerTable().locator("tr")
			.filter(new Locator.FilterOptions().setHasText("Telephone"))
			.locator("td")
			.nth(1);
	}

	// Pet-related elements
	private Locator petsTable() {
		return page.locator("table").nth(1);
	}

	private Locator petRows() {
		return petsTable().locator("tbody tr");
	}

	// Actions
	public OwnerFormPage clickEditOwner() {
		editOwnerLink().click();
		return new OwnerFormPage(page);
	}

	public PetFormPage clickAddNewPet() {
		addNewPetLink().click();
		return new PetFormPage(page);
	}

	public PetFormPage clickEditPetByName(String petName) {
		// Find the pet row and click the Edit Pet link within it
		Locator petRow = petRows().filter(new Locator.FilterOptions().setHasText(petName));
		petRow.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Edit Pet")).click();
		return new PetFormPage(page);
	}

	public VisitFormPage clickAddVisitByPetName(String petName) {
		// Find the pet row and click the Add Visit link within it
		Locator petRow = petRows().filter(new Locator.FilterOptions().setHasText(petName));
		petRow.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Add Visit")).click();
		return new VisitFormPage(page);
	}

	// Verification methods
	public boolean isOwnerInformationVisible() {
		return ownerInformationHeading().isVisible();
	}

	public boolean isPetsAndVisitsVisible() {
		return petsAndVisitsHeading().isVisible();
	}

	public boolean isEditOwnerLinkVisible() {
		return editOwnerLink().isVisible();
	}

	public boolean isAddNewPetLinkVisible() {
		return addNewPetLink().isVisible();
	}

	// Get owner information
	public String getOwnerName() {
		return ownerName().textContent().trim();
	}

	public String getOwnerAddress() {
		return ownerAddress().textContent().trim();
	}

	public String getOwnerCity() {
		return ownerCity().textContent().trim();
	}

	public String getOwnerTelephone() {
		return ownerTelephone().textContent().trim();
	}

	// Pet-related verifications
	public int getPetCount() {
		return petRows().count();
	}

	public boolean hasPetWithName(String petName) {
		return petRows().filter(new Locator.FilterOptions().setHasText(petName)).count() > 0;
	}

	public String getPetType(String petName) {
		Locator petRow = petRows().filter(new Locator.FilterOptions().setHasText(petName));
		return petRow.locator("dd").filter(new Locator.FilterOptions().setHasText("")).nth(2).textContent().trim();
	}

}