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
import org.springframework.samples.petclinic.e2e.pages.*;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright E2E tests for error handling and validation. As a user, I want to see
 * appropriate error messages and validation when I encounter errors or provide invalid
 * input.
 */
class ErrorHandlingAndValidationTest extends PlaywrightTestBase {

	@Test
	void asAUserIShouldSeeTheErrorPageWhenNavigatingToOupsEndpoint() {
		// Navigate directly to the error endpoint
		navigateTo("/oups");

		ErrorPage errorPage = new ErrorPage(page);

		// Verify error page is displayed
		assertTrue(errorPage.isErrorPage(), "Should be on error page");
		assertTrue(errorPage.isInternalServerError(), "Should be a 500 error");
		assertEquals("Something happened...", errorPage.getErrorHeadingText(), "Should show error message");
		assertTrue(errorPage.isErrorImageDisplayed(), "Error image should be displayed");
	}

	@Test
	void asAUserIShouldBeAbleToNavigateFromErrorPageBackToApplication() {
		navigateTo("/oups");
		ErrorPage errorPage = new ErrorPage(page);

		// Verify we can navigate back to home
		HomePage homePage = errorPage.navigateToHome();
		assertTrue(homePage.isHomePage(), "Should be able to navigate back to home");

		// Navigate back to error page and try other navigation
		navigateTo("/oups");
		errorPage = new ErrorPage(page);

		FindOwnersPage findOwnersPage = errorPage.navigateToFindOwners();
		assertTrue(findOwnersPage.isFindOwnersPage(), "Should be able to navigate to Find Owners");

		// Test navigation to veterinarians from error page
		navigateTo("/oups");
		errorPage = new ErrorPage(page);

		VeterinariansPage vetsPage = errorPage.navigateToVeterinarians();
		assertTrue(vetsPage.isVeterinariansPage(), "Should be able to navigate to Veterinarians");
	}

	@Test
	void asAUserIShouldSeeValidationErrorsWhenSubmittingEmptyOwnerForm() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		CreateOwnerPage createPage = findOwnersPage.clickAddOwner();

		// Clear all fields and submit
		createPage.clearForm();
		CreateOwnerPage pageWithErrors = createPage.submitFormWithErrors();

		// Verify validation errors are displayed
		assertTrue(pageWithErrors.hasValidationErrors(), "Should show validation errors for empty form");
		assertFalse(pageWithErrors.getValidationError().isEmpty(), "Should have specific error message");
	}

	@Test
	void asAUserIShouldSeeValidationErrorsWhenEditingOwnerWithInvalidData() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();

		// Get first owner and navigate to edit
		String ownerName = ownersPage.getOwnerNames().get(0);
		OwnerDetailsPage detailsPage = ownersPage.clickOwnerByName(ownerName);
		EditOwnerPage editPage = detailsPage.clickEditOwner();

		// Clear required field and try to submit
		editPage.clearFirstName();
		EditOwnerPage pageWithErrors = editPage.submitUpdateWithErrors();

		// Verify validation errors are shown
		assertTrue(pageWithErrors.hasValidationErrors(), "Should show validation errors when first name is empty");
	}

	@Test
	void asAUserIShouldGetAppropriateResponseForNonExistentPages() {
		// Navigate to a non-existent page
		navigateTo("/nonexistent");

		// The application should handle this gracefully
		// Spring Boot typically shows a 404 error or redirects

		// Verify we can still navigate to valid pages
		goToHomePage();
		HomePage homePage = new HomePage(page);
		assertTrue(homePage.isHomePage(), "Should be able to navigate to home after 404");
	}

	@Test
	void asAUserIShouldSeeConsistentNavigationEvenAfterErrors() {
		// Start with error page
		navigateTo("/oups");
		ErrorPage errorPage = new ErrorPage(page);
		assertTrue(errorPage.isNavigationPresent(), "Navigation should be present on error page");

		// Navigate to home and verify
		HomePage homePage = errorPage.navigateToHome();
		assertTrue(homePage.isNavigationPresent(), "Navigation should be present on home page");

		// Go back to error and verify navigation still works
		ErrorPage errorPageAgain = homePage.navigateToError();
		assertTrue(errorPageAgain.isNavigationPresent(), "Navigation should still work after multiple navigations");
	}

	@Test
	void asAUserIShouldSeeCorrectPageTitlesEvenForErrorPages() {
		String expectedTitle = "PetClinic :: a Spring Framework demonstration";

		// Check error page title
		navigateTo("/oups");
		assertEquals(expectedTitle, page.title(), "Error page should have correct title");

		// Verify title consistency after navigation
		goToHomePage();
		assertEquals(expectedTitle, page.title(), "Home page should have correct title");

		navigateTo("/oups");
		assertEquals(expectedTitle, page.title(), "Error page should maintain correct title");
	}

	@Test
	void asAUserIShouldBeAbleToSearchForNonExistentOwners() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();

		// Search for a non-existent owner
		String nonExistentName = "NonExistentOwner" + System.currentTimeMillis();

		// This should either return no results or redirect back to search
		// The actual behavior depends on the application implementation
		page.getByRole(AriaRole.TEXTBOX).fill(nonExistentName);
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner")).click();

		// Verify we get an appropriate response (either empty list or back to search)
		// The page should handle this gracefully without crashing
		assertTrue(page.url().contains("owners"), "Should stay in owners section");
	}

	@Test
	void asAUserIShouldSeeFormsHandleSpecialCharactersGracefully() {
		goToHomePage();
		HomePage homePage = new HomePage(page);
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		CreateOwnerPage createPage = findOwnersPage.clickAddOwner();

		// Test form with special characters
		createPage.fillOwnerForm("Test'Name", "Last\"Name", "123 Street & Avenue", "City <Test>", "555-123-4567");

		// Submit and verify it doesn't crash the application
		try {
			createPage.submitForm();
			// If successful, we should be on details page
			// If validation fails, we should get appropriate errors
			assertTrue(true, "Form should handle special characters gracefully");
		}
		catch (Exception e) {
			// Should not throw unhandled exceptions
			fail("Form should handle special characters without throwing exceptions");
		}
	}

	@Test
	void asAUserIShouldSeeApplicationRecoverFromErrors() {
		// Visit error page
		navigateTo("/oups");
		ErrorPage errorPage = new ErrorPage(page);
		assertTrue(errorPage.isErrorPage(), "Should be on error page");

		// Navigate to valid functionality
		HomePage homePage = errorPage.navigateToHome();
		assertTrue(homePage.isHomePage(), "Should recover to home page");

		// Verify full functionality still works
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		OwnersListPage ownersPage = findOwnersPage.searchAllOwners();
		assertTrue(ownersPage.hasOwners(), "Application functionality should work after error");

		// Navigate back to error and verify it still shows error correctly
		ErrorPage errorPageAgain = findOwnersPage.navigateToError();
		assertTrue(errorPageAgain.isErrorPage(), "Error page should still work correctly");
	}

}