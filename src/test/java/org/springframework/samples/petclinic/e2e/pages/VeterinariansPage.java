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
 * Page Object for the Veterinarians page.
 */
public class VeterinariansPage extends BasePage {

	private final Locator veterinariansHeading;

	private final Locator veterinariansTable;

	private final Locator paginationInfo;

	private final Locator nextPageLink;

	private final Locator previousPageLink;

	public VeterinariansPage(Page page, String baseUrl) {
		super(page, baseUrl);
		this.veterinariansHeading = page.locator("h2:has-text('Veterinarians')");
		this.veterinariansTable = page.locator("table");
		this.paginationInfo = page.locator("text=pages");
		this.nextPageLink = page.locator("a:has-text('2')");
		this.previousPageLink = page.locator("text=Previous");
	}

	public void navigateTo() {
		page.navigate(baseUrl + "/vets.html");
		waitForPageLoad();
	}

	public boolean isVeterinariansHeadingVisible() {
		return veterinariansHeading.isVisible();
	}

	public boolean isVeterinariansTableVisible() {
		return veterinariansTable.isVisible();
	}

	public boolean isPaginationVisible() {
		return paginationInfo.isVisible();
	}

	public void clickNextPage() {
		if (nextPageLink.isVisible()) {
			nextPageLink.click();
			waitForPageLoad();
		}
	}

	public boolean isNextPageLinkVisible() {
		return nextPageLink.isVisible();
	}

	public int getVeterinarianCount() {
		// Count table rows excluding header
		return veterinariansTable.locator("tbody tr").count();
	}

	public String getVeterinarianName(int rowIndex) {
		return veterinariansTable.locator("tbody tr").nth(rowIndex).locator("td").first().textContent();
	}

	public String getVeterinarianSpecialties(int rowIndex) {
		return veterinariansTable.locator("tbody tr").nth(rowIndex).locator("td").last().textContent();
	}

	public boolean hasVeterinarianWithName(String name) {
		return veterinariansTable.locator("text=" + name).isVisible();
	}

}