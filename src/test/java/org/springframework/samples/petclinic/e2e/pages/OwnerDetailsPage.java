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

import java.util.List;
import java.util.ArrayList;

/**
 * Page Object Model for the Owner Details page of the Pet Clinic application. Provides
 * methods to view owner information, manage pets, and handle visits.
 */
public class OwnerDetailsPage extends BasePage {

	// Locators for owner details page elements - using specific, unambiguous selectors
	private static final String PAGE_HEADING = "h2";

	private static final String OWNER_INFO_TABLE = "table:first-of-type"; // First table
																			// contains
																			// owner info

	private static final String EDIT_OWNER_LINK = "a[href*='/edit']"; // Link containing
																		// 'edit'

	private static final String ADD_PET_LINK = "a[href*='/pets/new']"; // Link for adding
																		// new pet

	private static final String PETS_VISITS_HEADING = "h2:has-text('Pets and Visits')"; // Specific
																						// heading

	private static final String PETS_TABLE = "table:last-of-type"; // Last table contains
																	// pets info

	private static final String PET_ROWS = "table:last-of-type tbody tr"; // Pet rows in
																			// the pets
																			// table

	private static final String EDIT_PET_LINKS = "a[href*='/pets/'][href*='/edit']"; // Edit
																						// pet
																						// links

	private static final String ADD_VISIT_LINKS = "a[href*='/visits/new']"; // Add visit
																			// links

	public OwnerDetailsPage(String baseUrl) {
		super(baseUrl);
		// Don't navigate automatically as this page is reached via owner selection
		waitForElement(OWNER_INFO_TABLE);
	}

	/**
	 * Verify we're on the Owner Details page by checking the heading
	 */
	public boolean isOwnerDetailsPageDisplayed() {
		try {
			return page.locator(PAGE_HEADING).first().textContent().trim().equals("Owner Information");
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the page heading text
	 */
	public String getPageHeading() {
		return page.locator(PAGE_HEADING).first().textContent().trim();
	}

	/**
	 * Get owner information from the details table
	 */
	public OwnerDetails getOwnerDetails() {
		Locator table = page.locator(OWNER_INFO_TABLE);
		Locator rows = table.locator("tbody tr");

		String name = "";
		String address = "";
		String city = "";
		String telephone = "";

		int rowCount = rows.count();
		for (int i = 0; i < rowCount; i++) {
			Locator row = rows.nth(i);
			Locator cells = row.locator("td");

			if (cells.count() >= 2) {
				String label = cells.first().textContent().trim();
				String value = cells.nth(1).textContent().trim();

				switch (label) {
					case "Name":
						name = value;
						break;
					case "Address":
						address = value;
						break;
					case "City":
						city = value;
						break;
					case "Telephone":
						telephone = value;
						break;
				}
			}
		}

		return new OwnerDetails(name, address, city, telephone);
	}

	/**
	 * Click the Edit Owner link
	 */
	public EditOwnerPage clickEditOwner() {
		page.locator(EDIT_OWNER_LINK).click();
		return new EditOwnerPage(baseUrl);
	}

	/**
	 * Click the Add New Pet link
	 */
	public AddPetPage clickAddNewPet() {
		page.locator(ADD_PET_LINK).click();
		return new AddPetPage(baseUrl);
	}

	/**
	 * Check if pets and visits section is displayed
	 */
	public boolean isPetsAndVisitsSectionDisplayed() {
		return page.locator(PETS_VISITS_HEADING).isVisible();
	}

	/**
	 * Get the number of pets for this owner
	 */
	public int getPetCount() {
		try {
			return page.locator(PET_ROWS).count();
		}
		catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Get information about all pets
	 */
	public List<PetInfo> getAllPetsInfo() {
		List<PetInfo> pets = new ArrayList<>();
		Locator petRows = page.locator(PET_ROWS);
		int count = petRows.count();

		for (int i = 0; i < count; i++) {
			Locator row = petRows.nth(i);

			// Pet info is in the first cell with dl/dt/dd structure
			Locator petInfoCell = row.locator("td:first-child");

			String name = "";
			String birthDate = "";
			String type = "";

			try {
				// Extract pet information from definition list structure
				Locator nameElement = petInfoCell.locator("dd:first-of-type");
				if (nameElement.isVisible()) {
					name = nameElement.textContent().trim();
				}

				Locator birthDateElement = petInfoCell.locator("dd:nth-of-type(2)");
				if (birthDateElement.isVisible()) {
					birthDate = birthDateElement.textContent().trim();
				}

				Locator typeElement = petInfoCell.locator("dd:nth-of-type(3)");
				if (typeElement.isVisible()) {
					type = typeElement.textContent().trim();
				}

				pets.add(new PetInfo(name, birthDate, type));
			}
			catch (Exception e) {
				// Skip this pet if we can't parse the information
			}
		}

		return pets;
	}

	/**
	 * Click Edit Pet link for a specific pet
	 * @param petIndex zero-based index of the pet
	 */
	public EditPetPage clickEditPet(int petIndex) {
		page.locator(EDIT_PET_LINKS).nth(petIndex).click();
		return new EditPetPage(baseUrl);
	}

	/**
	 * Click Add Visit link for a specific pet
	 * @param petIndex zero-based index of the pet
	 */
	public AddVisitPage clickAddVisit(int petIndex) {
		page.locator(ADD_VISIT_LINKS).nth(petIndex).click();
		return new AddVisitPage(baseUrl);
	}

	/**
	 * Check if Edit Owner link is displayed
	 */
	public boolean isEditOwnerLinkDisplayed() {
		return page.locator(EDIT_OWNER_LINK).isVisible();
	}

	/**
	 * Check if Add New Pet link is displayed
	 */
	public boolean isAddNewPetLinkDisplayed() {
		return page.locator(ADD_PET_LINK).isVisible();
	}

	/**
	 * Data class to hold owner details information
	 */
	public static class OwnerDetails {

		public final String name;

		public final String address;

		public final String city;

		public final String telephone;

		public OwnerDetails(String name, String address, String city, String telephone) {
			this.name = name;
			this.address = address;
			this.city = city;
			this.telephone = telephone;
		}

		@Override
		public String toString() {
			return String.format("OwnerDetails{name='%s', address='%s', city='%s', telephone='%s'}", name, address,
					city, telephone);
		}

	}

	/**
	 * Data class to hold pet information
	 */
	public static class PetInfo {

		public final String name;

		public final String birthDate;

		public final String type;

		public PetInfo(String name, String birthDate, String type) {
			this.name = name;
			this.birthDate = birthDate;
			this.type = type;
		}

		@Override
		public String toString() {
			return String.format("PetInfo{name='%s', birthDate='%s', type='%s'}", name, birthDate, type);
		}

	}

}