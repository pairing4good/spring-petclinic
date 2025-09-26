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
 * Page Object Model for the Veterinarians page
 */
public class VeterinariansPage extends BasePage {

	public VeterinariansPage(Page page) {
		super(page);
	}

	// Page elements
	private Locator pageHeading() {
		return page.locator("h2").filter(new Locator.FilterOptions().setHasText("Veterinarians"));
	}

	private Locator veterinariansTable() {
		return page.locator("table");
	}

	private Locator veterinarianRows() {
		return veterinariansTable().locator("tbody tr");
	}

	// Pagination elements
	private Locator paginationContainer() {
		return page.locator(".pagination, [class*='page']").first();
	}

	private Locator nextPageLink() {
		return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("2"));
	}

	private Locator previousPageLink() {
		return page.locator("text=Previous");
	}

	private Locator nextNavLink() {
		return page.locator("text=Next");
	}

	// Verification methods
	public boolean isPageHeadingVisible() {
		return pageHeading().isVisible();
	}

	public String getPageHeadingText() {
		return pageHeading().textContent();
	}

	public boolean isVeterinariansTableVisible() {
		return veterinariansTable().isVisible();
	}

	public int getVeterinarianCount() {
		return veterinarianRows().count();
	}

	// Get veterinarian information
	public boolean hasVeterinarianWithName(String name) {
		return veterinarianRows().filter(new Locator.FilterOptions().setHasText(name)).count() > 0;
	}

	public String getVeterinarianSpecialties(String veterinarianName) {
		Locator vetRow = veterinarianRows().filter(new Locator.FilterOptions().setHasText(veterinarianName));
		if (vetRow.count() > 0) {
			// Get the specialties column (second column)
			return vetRow.locator("td").nth(1).textContent().trim();
		}
		return "";
	}

	// Pagination methods
	public boolean isPaginationVisible() {
		return paginationContainer().isVisible();
	}

	public boolean hasNextPage() {
		return nextPageLink().isVisible();
	}

	public VeterinariansPage clickNextPage() {
		if (hasNextPage()) {
			nextPageLink().click();
			page.waitForLoadState();
		}
		return this;
	}

	public boolean hasPreviousPage() {
		return previousPageLink().isVisible();
	}

	public VeterinariansPage clickPreviousPage() {
		if (hasPreviousPage()) {
			previousPageLink().click();
			page.waitForLoadState();
		}
		return this;
	}

}