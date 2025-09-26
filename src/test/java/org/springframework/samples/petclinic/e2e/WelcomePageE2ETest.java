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
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;
import org.springframework.samples.petclinic.e2e.pages.WelcomePage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * As a user, I want to navigate the welcome page, so that I can access all main features
 * of the application
 */
class WelcomePageE2ETest extends BaseE2ETest {

	@Test
	void asAUser_IWantToAccessTheWelcomePage_SoThatICanSeeTheMainPageContent() {
		WelcomePage welcomePage = new WelcomePage(page);

		welcomePage.navigate();

		assertThat(welcomePage.isWelcomePageDisplayed()).isTrue();
		assertThat(welcomePage.getTitle()).contains("PetClinic");
		assertThat(welcomePage.getWelcomeImage().isVisible()).isTrue();
	}

	@Test
	void asAUser_IWantToNavigateToFindOwners_SoThatICanSearchForOwners() {
		WelcomePage welcomePage = new WelcomePage(page);
		FindOwnersPage findOwnersPage = new FindOwnersPage(page);

		welcomePage.navigate();
		welcomePage.clickFindOwners();

		assertThat(page.url()).contains("/owners/find");
		assertThat(findOwnersPage.getLastNameInput().isVisible()).isTrue();
		assertThat(findOwnersPage.getFindOwnerButton().isVisible()).isTrue();
		assertThat(findOwnersPage.getAddOwnerButton().isVisible()).isTrue();
	}

	@Test
	void asAUser_IWantToNavigateToVeterinarians_SoThatICanViewVeterinaryInformation() {
		WelcomePage welcomePage = new WelcomePage(page);
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		welcomePage.navigate();
		welcomePage.clickVeterinarians();

		assertThat(page.url()).contains("/vets.html");
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
		assertThat(vetsPage.getVetCount()).isGreaterThan(0);
	}

	@Test
	void asAUser_IWantToUseNavigationLinks_SoThatICanMoveAroundTheApplication() {
		WelcomePage welcomePage = new WelcomePage(page);

		welcomePage.navigate();

		assertThat(welcomePage.getFindOwnersLink().isVisible()).isTrue();
		assertThat(welcomePage.getVeterinariansLink().isVisible()).isTrue();

		welcomePage.clickFindOwners();
		assertThat(page.url()).contains("/owners/find");

		page.goBack();
		assertThat(page.url()).endsWith("/");

		welcomePage.clickVeterinarians();
		assertThat(page.url()).contains("/vets.html");

		page.goBack();
		assertThat(page.url()).endsWith("/");
	}

	@Test
	void asAUser_IWantThePageToBeResponsive_SoThatItWorksOnDifferentScreenSizes() {
		WelcomePage welcomePage = new WelcomePage(page);

		page.setViewportSize(1200, 800);
		welcomePage.navigate();
		assertThat(welcomePage.isWelcomePageDisplayed()).isTrue();

		page.setViewportSize(768, 1024);
		welcomePage.navigate();
		assertThat(welcomePage.isWelcomePageDisplayed()).isTrue();

		page.setViewportSize(375, 667);
		welcomePage.navigate();
		assertThat(welcomePage.isWelcomePageDisplayed()).isTrue();

		// On mobile, navigation might be collapsed, so check if nav elements exist in DOM
		assertThat(welcomePage.getFindOwnersLink().count()).isGreaterThan(0);
		assertThat(welcomePage.getVeterinariansLink().count()).isGreaterThan(0);
	}

}