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

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End tests for homepage and basic navigation functionality.
 *
 * @author Copilot
 */
class HomePageE2ETest extends BaseE2ETest {

	@Test
	void asAVisitor_IWantToViewTheHomePage_SoThatICanSeeTheWelcomeMessage() {
		// Given I am a visitor to the PetClinic website
		// When I navigate to the home page
		navigateToHomePage();

		// Then I should see the welcome message
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome"))).isVisible();

		// And I should see the page title
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

		// And I should see the spring pet image
		assertThat(page.locator("img").first()).isVisible();
	}

	@Test
	void asAVisitor_IWantToSeeAllNavigationLinks_SoThatICanAccessDifferentSections() {
		// Given I am on the home page
		navigateToHomePage();

		// Then I should see all main navigation links
		assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home").setExact(false)))
			.isVisible();
		assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find Owners").setExact(false)))
			.isVisible();
		assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians").setExact(false)))
			.isVisible();
		assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error").setExact(false)))
			.isVisible();
	}

	@Test
	void asAUser_IWantToNavigateToFindOwners_SoThatICanSearchForPetOwners() {
		// Given I am on the home page
		navigateToHomePage();

		// When I click on the Find Owners link
		clickNavigationLink("Find Owners");

		// Then I should be redirected to the find owners page
		assertThat(page).hasURL(baseUrl() + "/owners/find");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Find Owners"))).isVisible();

		// And I should see the search form
		assertThat(page.getByLabel("Last Name")).isVisible();
		assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner"))).isVisible();
		assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add Owner"))).isVisible();
	}

	@Test
	void asAUser_IWantToNavigateToVeterinarians_SoThatICanViewVeterinarianInformation() {
		// Given I am on the home page
		navigateToHomePage();

		// When I click on the Veterinarians link
		clickNavigationLink("Veterinarians");

		// Then I should be redirected to the veterinarians page
		assertThat(page).hasURL(baseUrl() + "/vets.html");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Veterinarians"))).isVisible();

		// And I should see the veterinarians table
		assertThat(page.getByRole(AriaRole.TABLE)).isVisible();
		assertThat(page.getByRole(AriaRole.COLUMNHEADER, new Page.GetByRoleOptions().setName("Name"))).isVisible();
		assertThat(page.getByRole(AriaRole.COLUMNHEADER, new Page.GetByRoleOptions().setName("Specialties")))
			.isVisible();
	}

	@Test
	void asAUser_IWantToNavigateToErrorPage_SoThatICanSeeHowErrorsAreHandled() {
		// Given I am on the home page
		navigateToHomePage();

		// When I click on the Error link
		clickNavigationLink("Error");

		// Then I should be redirected to the error page
		assertThat(page).hasURL(baseUrl() + "/oups");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Something happened...")))
			.isVisible();

		// And I should see an error image
		assertThat(page.locator("img").first()).isVisible();
	}

	@Test
	void asAUser_IWantToReturnHomeFromAnyPage_SoThatICanStartOver() {
		// Given I am on the find owners page
		navigateToUrl("/owners/find");
		waitForHeading("Find Owners");

		// When I click on the Home link
		clickNavigationLink("Home");

		// Then I should be redirected to the home page
		assertThat(page).hasURL(baseUrl() + "/");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome"))).isVisible();
	}

	@Test
	void asAUser_IWantToNavigateWithBrowserBackButton_SoThatICanGoBackToPreviousPage() {
		// Given I start on the home page
		navigateToHomePage();

		// When I navigate to find owners page
		clickNavigationLink("Find Owners");
		waitForHeading("Find Owners");

		// And then use browser back button
		page.goBack();

		// Then I should be back on the home page
		assertThat(page).hasURL(baseUrl() + "/");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome"))).isVisible();
	}

	@Test
	void asAUser_IWantToNavigateWithBrowserForwardButton_SoThatICanGoForwardAfterGoingBack() {
		// Given I start on the home page and navigate to find owners
		navigateToHomePage();
		clickNavigationLink("Find Owners");
		waitForHeading("Find Owners");

		// When I go back
		page.goBack();
		waitForHeading("Welcome");

		// And then use browser forward button
		page.goForward();

		// Then I should be back on the find owners page
		assertThat(page).hasURL(baseUrl() + "/owners/find");
		assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Find Owners"))).isVisible();
	}

	@Test
	void asAUser_IWantToSeeConsistentLayoutAcrossPages_SoThatIHaveAUnifiedExperience() {
		// Test that navigation and footer are consistent across pages
		String[] testPages = { "/", "/owners/find", "/vets.html" };

		for (String testPage : testPages) {
			// Navigate to each page
			navigateToUrl(testPage);

			// Verify navigation is present
			assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home").setExact(false)))
				.isVisible();
			assertThat(
					page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Find Owners").setExact(false)))
				.isVisible();
			assertThat(
					page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Veterinarians").setExact(false)))
				.isVisible();
			assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Error").setExact(false)))
				.isVisible();

			// Verify footer logo is present
			assertThat(page.getByAltText("VMware Tanzu Logo")).isVisible();
		}
	}

}