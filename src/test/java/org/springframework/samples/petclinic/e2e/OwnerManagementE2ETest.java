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
package org.springframework.samples.petclinic.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.pages.FindOwnersPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerDetailsPage;
import org.springframework.samples.petclinic.e2e.pages.OwnerFormPage;
import org.springframework.samples.petclinic.e2e.pages.OwnersListPage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * As a user, I want to manage pet owners, so that I can create, find, view, and edit
 * owner information
 */
class OwnerManagementE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToSearchForAllOwners_SoThatICanSeeAllOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		OwnersListPage ownersListPage = new OwnersListPage(page);

		findOwnersPage.navigate();
		findOwnersPage.searchAllOwners();

		assertThat(page.url()).contains("/owners");
		assertThat(ownersListPage.getOwnersTable().isVisible()).isTrue();
		assertThat(ownersListPage.getOwnerCount()).isGreaterThan(0);
	}

	@Test
	void asAUser_IWantToSearchForOwnerByLastName_SoThatICanFindSpecificOwners() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		OwnersListPage ownersListPage = new OwnersListPage(page);

		findOwnersPage.navigate();
		findOwnersPage.searchByLastName("Davis");

		if (page.url().contains("/owners/")) {
			// Single result - redirected to owner details
			OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);
			assertThat(ownerDetailsPage.getOwnerName()).contains("Davis");
		}
		else {
			// Multiple results - on owners list page
			assertThat(page.url()).contains("/owners");
			assertThat(ownersListPage.getOwnersTable().isVisible()).isTrue();
		}
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatIGetAppropriateErrorMessage() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);

		findOwnersPage.navigate();
		findOwnersPage.searchByLastName("NonExistentLastName12345");

		assertThat(findOwnersPage.hasNoResultsMessage()).isTrue();
		assertThat(page.url()).contains("/owners/find");
	}

	@Test
	void asAUser_IWantToCreateANewOwner_SoThatICanAddOwnerInformation() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		OwnerFormPage ownerFormPage = new OwnerFormPage(page);
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);

		findOwnersPage.navigate();
		findOwnersPage.clickAddOwner();

		assertThat(page.url()).contains("/owners/new");
		assertThat(ownerFormPage.getFirstNameInput().isVisible()).isTrue();

		ownerFormPage.fillOwnerForm("John", "TestOwner", "123 Test St", "Test City", "555-1234");
		ownerFormPage.submitForm();

		assertThat(page.url()).contains("/owners/");
		assertThat(ownerDetailsPage.getOwnerName()).contains("John TestOwner");
	}

	@Test
	void asAUser_IWantToCreateOwnerWithValidationErrors_SoThatIGetAppropriateErrorMessages() {
		OwnerFormPage ownerFormPage = new OwnerFormPage(page);

		ownerFormPage.navigateToNewOwnerForm();
		ownerFormPage.fillOwnerForm("", "", "", "", "");
		ownerFormPage.submitForm();

		assertThat(page.url()).contains("/owners/new");
		assertThat(ownerFormPage.hasValidationErrors()).isTrue();
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeOwnerInformation() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		OwnersListPage ownersListPage = new OwnersListPage(page);
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);

		findOwnersPage.navigate();
		findOwnersPage.searchAllOwners();

		if (page.url().contains("/owners") && !page.url().matches(".*/owners/\\d+$")) {
			// On owners list page
			ownersListPage.clickOwnerLink(
					ownersListPage.getOwnersTable().locator("tbody tr").first().locator("td a").textContent());
		}

		assertThat(page.url()).matches(".*/owners/\\d+$");
		assertThat(ownerDetailsPage.getOwnerInfo().isVisible()).isTrue();
		assertThat(ownerDetailsPage.getEditOwnerButton().isVisible()).isTrue();
		assertThat(ownerDetailsPage.getAddNewPetButton().isVisible()).isTrue();
	}

	@Test
	void asAUser_IWantToEditOwnerInformation_SoThatICanUpdateOwnerDetails() {
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);
		OwnersListPage ownersListPage = new OwnersListPage(page);
		OwnerDetailsPage ownerDetailsPage = new OwnerDetailsPage(page);
		OwnerFormPage ownerFormPage = new OwnerFormPage(page);

		findOwnersPage.navigate();
		findOwnersPage.searchAllOwners();

		if (page.url().contains("/owners") && !page.url().matches(".*/owners/\\d+$")) {
			// On owners list page
			ownersListPage.clickOwnerLink(
					ownersListPage.getOwnersTable().locator("tbody tr").first().locator("td a").textContent());
		}

		ownerDetailsPage.clickEditOwner();

		assertThat(page.url()).contains("/edit");
		assertThat(ownerFormPage.isEditMode()).isTrue();

		ownerFormPage.getTelephoneInput().fill("555-9999");
		ownerFormPage.submitForm();

		assertThat(page.url()).matches(".*/owners/\\d+$");
	}

}