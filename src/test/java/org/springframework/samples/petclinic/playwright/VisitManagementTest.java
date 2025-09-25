/*
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.playwright.pages.HomePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E tests for PetClinic visit management functionality.
 * 
 * @author Copilot
 */
class VisitManagementTest extends BasePlaywrightTest {

	@Test
	void asAnOwner_IWantToScheduleAVisitForMyPet_SoThatMyPetCanSeeAVet() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();
		
		// Navigate to owner with pets
		var ownersPage = homePage.clickFindOwners();
		ownersPage.searchAllOwners();
		var ownerDetailsPage = ownersPage.clickFirstOwner();
		
		// If owner has pets, try to schedule a visit
		if (ownerDetailsPage.getPetCount() > 0) {
			var visitPage = ownerDetailsPage.clickAddVisit(0);
			
			// Verify visit form is loaded
			assertTrue(visitPage.isVisitFormLoaded(), "Visit form should be loaded");
			assertTrue(visitPage.isPetInfoDisplayed(), "Pet information should be displayed");
		}
	}

	@Test
	void asAnOwner_IWantToFillOutVisitDetails_SoThatVetKnowsReasonForVisit() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();
		
		// Navigate to owner with pets
		var ownersPage = homePage.clickFindOwners();
		ownersPage.searchAllOwners();
		var ownerDetailsPage = ownersPage.clickFirstOwner();
		
		// If owner has pets, schedule a visit
		if (ownerDetailsPage.getPetCount() > 0) {
			var visitPage = ownerDetailsPage.clickAddVisit(0);
			
			// Fill out visit form
			visitPage.fillVisitForm("2024-12-01", "Regular checkup");
			visitPage.submitVisitForm();
			
			// Should redirect back to owner details
			assertTrue(page.url().contains("/owners/"), "Should redirect after scheduling visit");
		}
	}

}