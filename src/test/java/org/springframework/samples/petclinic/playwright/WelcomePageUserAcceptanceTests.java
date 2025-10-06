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

package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * User acceptance tests for the PetClinic welcome page and basic navigation.
 *
 * These tests verify the core functionality that users expect when first visiting the
 * PetClinic application.
 */
class WelcomePageUserAcceptanceTests extends PlaywrightTestBase {

	@Test
	@DisplayName("As a user, I want to see the welcome page, so that I know the application is working and understand what it does")
	void shouldDisplayWelcomePageWithTitle() {
		// Given: A user visits the PetClinic application
		// When: They navigate to the home page
		navigateToHomePage();

		// Then: They should see the PetClinic title and welcome message
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");
		assertThat(page.locator("h2")).containsText("Welcome");
		assertThat(page.locator(".hero-unit")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to see the main navigation menu, so that I can easily navigate to different sections of the application")
	void shouldDisplayMainNavigationMenu() {
		// Given: A user is on the PetClinic home page
		navigateToHomePage();

		// When: They look at the navigation bar
		// Then: They should see all the main navigation options
		assertThat(page.locator("nav.navbar")).isVisible();

		// Verify Home link
		assertThat(page.locator("a[href='/']")).isVisible();

		// Verify Find Owners link
		assertThat(page.locator("a[href='/owners/find']")).isVisible();

		// Verify Veterinarians link
		assertThat(page.locator("a[href='/vets.html']")).isVisible();

		// Verify Error link (for demonstration purposes)
		assertThat(page.locator("a[href='/oups']")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners, so that I can search for pet owners")
	void shouldNavigateToFindOwners() {
		// Given: A user is on the PetClinic home page
		navigateToHomePage();

		// When: They click on the "Find owners" link
		page.click("a[href='/owners/find']");
		page.waitForLoadState();

		// Then: They should be taken to the owner search page
		assertThat(page).hasURL(baseUrl + "/owners/find");
		assertThat(page.locator("h2")).containsText("Find Owners");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians, so that I can view the list of available vets")
	void shouldNavigateToVeterinarians() {
		// Given: A user is on the PetClinic home page
		navigateToHomePage();

		// When: They click on the "Veterinarians" link
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// Then: They should be taken to the veterinarians list page
		assertThat(page).hasURL(baseUrl + "/vets.html");
		assertThat(page.locator("h2")).containsText("Veterinarians");
	}

	@Test
	@DisplayName("As a user, I want to return to home from any page, so that I can easily get back to the main page")
	void shouldNavigateBackToHomeFromVetsPage() {
		// Given: A user is on the veterinarians page
		navigateToHomePage();
		page.click("a[href='/vets.html']");
		page.waitForLoadState();

		// When: They click on the home link
		page.click("a[href='/']");
		page.waitForLoadState();

		// Then: They should be back on the welcome page
		assertThat(page).hasURL(baseUrl + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@Test
	@DisplayName("As a user, I want to see the Spring logo, so that I know this is a Spring Framework demonstration")
	void shouldDisplaySpringLogo() {
		// Given: A user visits the PetClinic application
		navigateToHomePage();

		// When: They look at the page footer
		// Then: They should see the Spring logo
		assertThat(page.locator("img[alt*='Logo']")).isVisible();
	}

}