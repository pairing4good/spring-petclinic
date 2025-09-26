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
 * Page Object for the Veterinarians page
 */
public class VeterinariansPage extends BasePage {

	public VeterinariansPage(Page page) {
		super(page);
	}

	public void navigate() {
		navigateTo("/vets.html");
	}

	public Locator getVeterinariansTable() {
		return page.locator("table");
	}

	public Locator getVetRow(String vetName) {
		return page.locator("tr").filter(new Locator.FilterOptions().setHasText(vetName));
	}

	public int getVetCount() {
		return getVeterinariansTable().locator("tbody tr").count();
	}

	public Locator getPaginationControls() {
		return page.locator(".pagination, [data-testid='pagination']");
	}

	public void clickNextPage() {
		page.getByRole(com.microsoft.playwright.options.AriaRole.LINK, new Page.GetByRoleOptions().setName("Next"))
			.click();
	}

	public void clickPreviousPage() {
		page.getByRole(com.microsoft.playwright.options.AriaRole.LINK, new Page.GetByRoleOptions().setName("Previous"))
			.click();
	}

	public boolean isVetDisplayed(String vetName) {
		return getVetRow(vetName).isVisible();
	}

	public String getVetSpecialties(String vetName) {
		return getVetRow(vetName).locator("td").nth(1).textContent();
	}

}