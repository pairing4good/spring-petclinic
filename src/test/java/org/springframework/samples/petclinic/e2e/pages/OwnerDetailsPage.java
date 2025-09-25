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

import java.util.List;

/**
 * Page Object for the Owner Details page. Contains locators and actions for viewing and
 * managing owner information.
 */
public class OwnerDetailsPage extends BasePage {

	// Locators for owner information section
	private static final String OWNER_INFO_HEADING = "h2:has-text('Owner Information')";

	private static final String OWNER_INFO_TABLE = "table:first-of-type";

	private static final String EDIT_OWNER_LINK = "a:has-text('Edit Owner')";

	private static final String ADD_PET_LINK = "a:has-text('Add New Pet')";

	// Locators for pets and visits section
	private static final String PETS_VISITS_HEADING = "h2:has-text('Pets and Visits')";

	private static final String PETS_TABLE = "table:last-of-type";

	public OwnerDetailsPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Wait for owner details page to load
	 */
	public void waitForPageLoad() {
		waitForElement(OWNER_INFO_HEADING);
		waitForElement(OWNER_INFO_TABLE);
	}

	/**
	 * Get owner name from the details table
	 */
	public String getOwnerName() {
		// Find the row containing "Name" and get the corresponding value
		return page.locator(OWNER_INFO_TABLE + " tr:has(td:text('Name')) td:nth-child(2)").textContent().trim();
	}

	/**
	 * Get owner address from the details table
	 */
	public String getOwnerAddress() {
		return page.locator(OWNER_INFO_TABLE + " tr:has(td:text('Address')) td:nth-child(2)").textContent().trim();
	}

	/**
	 * Get owner city from the details table
	 */
	public String getOwnerCity() {
		return page.locator(OWNER_INFO_TABLE + " tr:has(td:text('City')) td:nth-child(2)").textContent().trim();
	}

	/**
	 * Get owner telephone from the details table
	 */
	public String getOwnerTelephone() {
		return page.locator(OWNER_INFO_TABLE + " tr:has(td:text('Telephone')) td:nth-child(2)").textContent().trim();
	}

	/**
	 * Click Edit Owner link
	 */
	public EditOwnerPage clickEditOwner() {
		page.locator(EDIT_OWNER_LINK).click();
		return new EditOwnerPage(page, baseUrl);
	}

	/**
	 * Click Add New Pet link
	 */
	public AddPetPage clickAddNewPet() {
		page.locator(ADD_PET_LINK).click();
		return new AddPetPage(page, baseUrl);
	}

	/**
	 * Get list of pet names
	 */
	public List<String> getPetNames() {
		return page.locator(PETS_TABLE + " dl dt:has-text('Name') + dd").allTextContents();
	}

	/**
	 * Check if a specific pet exists
	 */
	public boolean hasPet(String petName) {
		return getPetNames().stream().anyMatch(name -> name.trim().equals(petName));
	}

	/**
	 * Click Edit Pet link for a specific pet
	 */
	public EditPetPage clickEditPet(String petName) {
		// Find the Edit Pet link in the same row as the pet name
		Locator petRow = page.locator(PETS_TABLE + " tr:has(dd:text('" + petName + "'))");
		petRow.locator("a:has-text('Edit Pet')").click();
		return new EditPetPage(page, baseUrl);
	}

	/**
	 * Click Add Visit link for a specific pet
	 */
	public AddVisitPage clickAddVisit(String petName) {
		// Find the Add Visit link in the same row as the pet name
		Locator petRow = page.locator(PETS_TABLE + " tr:has(dd:text('" + petName + "'))");
		petRow.locator("a:has-text('Add Visit')").click();
		return new AddVisitPage(page, baseUrl);
	}

	/**
	 * Get the number of pets for this owner
	 */
	public int getPetCount() {
		return getPetNames().size();
	}

	/**
	 * Check if the owner has any pets
	 */
	public boolean hasPets() {
		return getPetCount() > 0;
	}

	/**
	 * Get pet information for a specific pet name
	 */
	public PetInfo getPetInfo(String petName) {
		Locator petSection = page.locator("dt:has-text('Name') + dd:has-text('" + petName + "')").locator("..");

		String name = petSection.locator("dt:has-text('Name') + dd").textContent().trim();
		String birthDate = petSection.locator("dt:has-text('Birth Date') + dd").textContent().trim();
		String type = petSection.locator("dt:has-text('Type') + dd").textContent().trim();

		return new PetInfo(name, birthDate, type);
	}

	/**
	 * Data class to hold pet information
	 */
	public static class PetInfo {

		private final String name;

		private final String birthDate;

		private final String type;

		public PetInfo(String name, String birthDate, String type) {
			this.name = name;
			this.birthDate = birthDate;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public String getBirthDate() {
			return birthDate;
		}

		public String getType() {
			return type;
		}

	}

}