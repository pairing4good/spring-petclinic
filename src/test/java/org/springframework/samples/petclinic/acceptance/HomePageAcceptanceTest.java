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

package org.springframework.samples.petclinic.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Acceptance tests for the home page and general navigation.
 * These tests validate the main application entry point and navigation structure.
 */
@DisplayName("Home Page and Navigation Acceptance Tests")
public class HomePageAcceptanceTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a visitor, I want to access the home page, so that I can see the PetClinic welcome page")
	void shouldDisplayWelcomePageWithCorrectTitle() {
		// Given I am a visitor to the PetClinic application
		// When I navigate to the home page
		navigateToHome();

		// Then I should see the welcome page with the correct title
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@Test
	@DisplayName("As a user, I want to see the main navigation menu, so that I can access all application features")
	void shouldDisplayMainNavigationMenu() {
		// Given I am on the home page
		navigateToHome();

		// When I look at the navigation
		// Then I should see all main navigation links
		assertThat(page.locator("nav")).isVisible();
		assertThat(page.locator("a").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Home"))).isVisible();
		assertThat(page.locator("a").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Find Owners"))).isVisible();
		assertThat(page.locator("a").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Veterinarians"))).isVisible();
		assertThat(page.locator("a").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Error"))).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to navigate to the Find Owners page, so that I can search for pet owners")
	void shouldNavigateToFindOwnersPage() {
		// Given I am on the home page
		navigateToHome();

		// When I click on the Find Owners link
		clickLink("Find Owners");

		// Then I should be on the Find Owners page
		assertThat(page).hasURL(baseUrl + "/owners/find");
		assertThat(page.locator("h2")).containsText("Find Owners");
	}

	@Test
	@DisplayName("As a user, I want to navigate to the Veterinarians page, so that I can view available veterinarians")
	void shouldNavigateToVeterinariansPage() {
		// Given I am on the home page
		navigateToHome();

		// When I click on the Veterinarians link
		clickLink("Veterinarians");

		// Then I should be on the Veterinarians page
		assertThat(page).hasURL(baseUrl + "/vets.html");
		assertThat(page.locator("h2")).containsText("Veterinarians");
	}

	@Test
	@DisplayName("As a user, I want to navigate back to home from any page, so that I can return to the main page")
	void shouldNavigateBackToHomeFromAnyPage() {
		// Given I am on the Find Owners page
		navigateTo("/owners/find");
		assertThat(page.locator("h2")).containsText("Find Owners");

		// When I click on the Home link
		clickLink("Home");

		// Then I should be back on the home page
		assertThat(page).hasURL(baseUrl + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

}