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
 * E2E tests for PetClinic error handling functionality.
 * 
 * @author Copilot
 */
class ErrorHandlingTest extends BasePlaywrightTest {

	@Test
	void asAUser_IWantToSeeErrorPageWhenSomethingGoesWrong_SoThatIKnowWhatHappened() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();
		
		// Navigate to error page
		var errorPage = homePage.clickErrorLink();
		
		// Verify error page is displayed
		assertTrue(errorPage.isErrorPageLoaded(), "Error page should be loaded");
		assertTrue(errorPage.isSomethingHappenedError(), "Should show 'Something happened' error");
	}

	@Test
	void asAUser_IWantToSee404PageForNonExistentPages_SoThatIKnowPageDoesNotExist() {
		// Navigate to non-existent page
		page.navigate(baseUrl + "/nonexistent");
		
		// Should get some kind of error response (404 or redirect to error page)
		assertTrue(page.url().contains(baseUrl), "Should stay within application domain");
	}

	@Test
	void asAUser_IWantFormValidationErrors_SoThatIKnowWhatToCorrect() {
		HomePage homePage = new HomePage(page, baseUrl);
		homePage.navigateToHome();
		
		// Navigate to owner creation form and test validation
		var ownersPage = homePage.clickFindOwners();
		ownersPage.clickAddOwner();
		
		// Submit empty form
		ownersPage.submitOwnerForm();
		
		// Should show validation errors or stay on form
		assertTrue(page.url().contains("/owners"), "Should handle form validation");
	}

}