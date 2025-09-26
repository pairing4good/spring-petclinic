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
import org.springframework.samples.petclinic.e2e.pages.VeterinariansPage;
import org.springframework.samples.petclinic.e2e.pages.WelcomePage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * As a pet owner, I want to view veterinarian information, so that I can learn about the
 * veterinarians and their specialties
 */
class VeterinariansE2ETest extends BaseE2ETest {

	@Test
	void asAPetOwner_IWantToNavigateToVeterinariansPage_SoThatICanViewVetInformation() {
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		vetsPage.navigate();

		assertThat(page.url()).contains("/vets.html");
		assertThat(page.title()).contains("PetClinic");
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
	}

	@Test
	void asAPetOwner_IWantToSeeListOfVeterinarians_SoThatICanViewAllAvailableVets() {
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		vetsPage.navigate();

		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
		assertThat(vetsPage.getVetCount()).isGreaterThan(0);

		// Check that table has proper headers
		assertThat(vetsPage.getVeterinariansTable().locator("th").count()).isGreaterThan(0);
	}

	@Test
	void asAPetOwner_IWantToViewVeterinarianSpecialties_SoThatICanFindVetsWithRelevantExpertise() {
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		vetsPage.navigate();

		// Get the first vet row and check if it has name and specialties
		if (vetsPage.getVetCount() > 0) {
			String firstVetName = vetsPage.getVeterinariansTable()
				.locator("tbody tr")
				.first()
				.locator("td")
				.first()
				.textContent();
			assertThat(firstVetName).isNotEmpty();

			// Check if specialties column exists (may be empty for some vets)
			assertThat(vetsPage.getVeterinariansTable().locator("tbody tr").first().locator("td").count())
				.isGreaterThanOrEqualTo(2);
		}
	}

	@Test
	void asAPetOwner_IWantToNavigateToVetsFromHomepage_SoThatICanEasilyFindVetInformation() {
		WelcomePage welcomePage = new WelcomePage(page);
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		welcomePage.navigate();
		welcomePage.clickVeterinarians();

		assertThat(page.url()).contains("/vets.html");
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
		assertThat(vetsPage.getVetCount()).isGreaterThan(0);
	}

	@Test
	void asAPetOwner_IWantVeterinariansPageToBeResponsive_SoThatICanViewItOnDifferentDevices() {
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		// Test desktop view
		page.setViewportSize(1200, 800);
		vetsPage.navigate();
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();

		// Test tablet view
		page.setViewportSize(768, 1024);
		vetsPage.navigate();
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();

		// Test mobile view
		page.setViewportSize(375, 667);
		vetsPage.navigate();
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
		assertThat(vetsPage.getVetCount()).isGreaterThan(0);
	}

	@Test
	void asAPetOwner_IWantToUseBrowserNavigationOnVetsPage_SoThatICanNavigateBackAndForward() {
		WelcomePage welcomePage = new WelcomePage(page);
		VeterinariansPage vetsPage = new VeterinariansPage(page);

		// Navigate from home to vets
		welcomePage.navigate();
		welcomePage.clickVeterinarians();
		assertThat(page.url()).contains("/vets.html");

		// Go back to home
		page.goBack();
		assertThat(page.url()).endsWith("/");
		assertThat(welcomePage.getWelcomeImage().isVisible()).isTrue();

		// Go forward to vets again
		page.goForward();
		assertThat(page.url()).contains("/vets.html");
		assertThat(vetsPage.getVeterinariansTable().isVisible()).isTrue();
	}

}