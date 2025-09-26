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
 * Page Object for the Visit form (add visit) page
 */
public class VisitFormPage extends BasePage {

	public VisitFormPage(Page page) {
		super(page);
	}

	public Locator getVisitDateInput() {
		return page.locator("input[name='date']");
	}

	public Locator getDescriptionInput() {
		return page.locator("input[name='description'], textarea[name='description']");
	}

	public Locator getAddVisitButton() {
		return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Add Visit"));
	}

	public Locator getPetInfo() {
		return page.locator("table").first();
	}

	public void fillVisitForm(String date, String description) {
		getVisitDateInput().fill(date);
		getDescriptionInput().fill(description);
	}

	public void submitForm() {
		getAddVisitButton().click();
	}

	public boolean hasValidationErrors() {
		return page.locator(".has-error, .error, .is-invalid").count() > 0;
	}

	public String getPetName() {
		return getPetInfo().locator("td").first().textContent();
	}

	public String getOwnerName() {
		return page.locator("h2").textContent();
	}

}