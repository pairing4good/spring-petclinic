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
package org.springframework.samples.petclinic.e2e.tests;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.e2e.config.PlaywrightTestBase;
import org.springframework.samples.petclinic.e2e.pages.HomePage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the PetClinic Home page functionality.
 */
class HomePageTests extends PlaywrightTestBase {

	@Test
	void asAVisitor_IWantToViewTheHomepage_SoThatICanSeeTheWelcomeMessage() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		assertTrue(homePage.isWelcomeHeadingVisible(), "Welcome heading should be visible on homepage");
		assertEquals("Welcome", homePage.getWelcomeHeadingText(), "Welcome heading text should be correct");
		assertTrue(homePage.getPageTitle().contains("PetClinic"), "Page title should contain 'PetClinic'");
	}

	@Test
	void asAVisitor_IWantToSeePageImages_SoThatThePageLooksComplete() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		assertTrue(homePage.isPetImageVisible(), "Pet image should be visible on homepage");
		assertTrue(homePage.isSpringLogoVisible(), "Spring logo should be visible on homepage");
	}

	@Test
	void asAVisitor_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		homePage.clickFindOwnersLink();

		assertTrue(homePage.isFindOwnersPageDisplayed(), "Should navigate to Find Owners page");
	}

	@Test
	void asAVisitor_IWantToNavigateToVeterinarians_SoThatICanViewVetInformation() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		homePage.clickVeterinariansLink();

		assertTrue(homePage.isVeterinariansPageDisplayed(), "Should navigate to Veterinarians page");
	}

	@Test
	void asAVisitor_IWantToNavigateToErrorPage_SoThatICanSeeHowErrorsAreHandled() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		homePage.clickErrorLink();

		assertTrue(homePage.isErrorPageDisplayed(), "Should navigate to Error page");
	}

	@Test
	void asAVisitor_IWantToReturnHome_SoThatICanNavigateBackFromOtherPages() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateTo();

		// Navigate away from home
		homePage.clickFindOwnersLink();
		assertFalse(homePage.isHomePageDisplayed(), "Should be away from home page");

		// Navigate back to home
		homePage.clickHomeLink();
		assertTrue(homePage.isHomePageDisplayed(), "Should return to home page");
	}

}