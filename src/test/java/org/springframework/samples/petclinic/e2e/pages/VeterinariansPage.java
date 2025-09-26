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
 * Page Object Model for the Veterinarians page of the Pet Clinic application. Provides
 * methods to view veterinarian information and handle pagination.
 */
public class VeterinariansPage extends BasePage {

	// Locators for veterinarians page elements - using specific, unambiguous selectors
	private static final String PAGE_HEADING = "h2";

	private static final String VETS_TABLE = "table#vets"; // Table with specific id

	private static final String VET_ROWS = "table#vets tbody tr"; // Vet rows in the table

	private static final String PAGINATION_CONTAINER = "div:has-text('pages')"; // Pagination
																				// section

	private static final String PAGE_LINKS = "a[href*='page=']"; // Links with page
																	// parameter

	private static final String NAVIGATION_ARROWS = "a[class*='fa']"; // Navigation arrows

	public VeterinariansPage(String baseUrl) {
		super(baseUrl);
		navigateTo("/vets.html");
	}

	/**
	 * Verify we're on the Veterinarians page by checking the heading
	 */
	public boolean isVeterinariansPageDisplayed() {
		try {
			return page.locator(PAGE_HEADING).textContent().trim().equals("Veterinarians");
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the page heading text
	 */
	public String getPageHeading() {
		return page.locator(PAGE_HEADING).textContent().trim();
	}

	/**
	 * Get the number of veterinarians displayed on the current page
	 */
	public int getVetCount() {
		return page.locator(VET_ROWS).count();
	}

	/**
	 * Get all veterinarian information from the current page
	 */
	public List<VetInfo> getAllVetsInfo() {
		List<VetInfo> vets = new ArrayList<>();
		Locator vetRows = page.locator(VET_ROWS);
		int count = vetRows.count();

		for (int i = 0; i < count; i++) {
			Locator row = vetRows.nth(i);
			Locator cells = row.locator("td");

			if (cells.count() >= 2) {
				String name = cells.nth(0).textContent().trim();
				String specialties = cells.nth(1).textContent().trim();

				vets.add(new VetInfo(name, specialties));
			}
		}

		return vets;
	}

	/**
	 * Get veterinarian information for a specific row
	 * @param rowIndex the zero-based index of the row
	 */
	public VetInfo getVetInfo(int rowIndex) {
		Locator row = page.locator(VET_ROWS).nth(rowIndex);
		Locator cells = row.locator("td");

		if (cells.count() >= 2) {
			String name = cells.nth(0).textContent().trim();
			String specialties = cells.nth(1).textContent().trim();

			return new VetInfo(name, specialties);
		}

		throw new RuntimeException("Invalid vet row data at index " + rowIndex);
	}

	/**
	 * Check if pagination is displayed
	 */
	public boolean isPaginationDisplayed() {
		try {
			return page.locator(PAGINATION_CONTAINER).isVisible();
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the current page number from pagination
	 */
	public int getCurrentPageNumber() {
		try {
			// Current page is usually shown as plain text without links
			String pageInfo = page.locator(PAGINATION_CONTAINER).textContent();
			// Extract current page from pagination text
			if (pageInfo.contains("[") && pageInfo.contains("]")) {
				String pagesSection = pageInfo.substring(pageInfo.indexOf("[") + 1, pageInfo.indexOf("]"));
				String[] pages = pagesSection.trim().split("\\s+");
				for (String pageStr : pages) {
					try {
						// Current page is not a link, so it's just text
						if (!pageStr.isEmpty() && Character.isDigit(pageStr.charAt(0))) {
							return Integer.parseInt(pageStr);
						}
					}
					catch (NumberFormatException e) {
						// Continue searching
					}
				}
			}
			return 1; // Default to page 1
		}
		catch (Exception e) {
			return 1;
		}
	}

	/**
	 * Navigate to a specific page
	 * @param pageNumber the page number to navigate to
	 */
	public void goToPage(int pageNumber) {
		page.locator("a[href*='page=" + pageNumber + "']").click();
		waitForElement(VETS_TABLE);
	}

	/**
	 * Click the next page navigation if available
	 */
	public boolean goToNextPage() {
		try {
			Locator nextLink = page.locator("a[class*='fa-step-forward'], a[class*='fa-fast-forward']").first();
			if (nextLink.isVisible()) {
				nextLink.click();
				waitForElement(VETS_TABLE);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click the previous page navigation if available
	 */
	public boolean goToPreviousPage() {
		try {
			Locator prevLink = page.locator("a[class*='fa-step-backward'], a[class*='fa-fast-backward']").first();
			if (prevLink.isVisible()) {
				prevLink.click();
				waitForElement(VETS_TABLE);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if the veterinarians table is displayed
	 */
	public boolean isVetsTableDisplayed() {
		return page.locator(VETS_TABLE).isVisible();
	}

	/**
	 * Data class to hold veterinarian information
	 */
	public static class VetInfo {

		public final String name;

		public final String specialties;

		public VetInfo(String name, String specialties) {
			this.name = name;
			this.specialties = specialties;
		}

		@Override
		public String toString() {
			return String.format("VetInfo{name='%s', specialties='%s'}", name, specialties);
		}

	}

}