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
 * Comprehensive acceptance tests covering all major user flows in the PetClinic
 * application. These tests validate the complete user journey through the application.
 */
@DisplayName("PetClinic Comprehensive Acceptance Tests")
public class PetClinicAcceptanceTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a clinic visitor, I want to search for existing owners, so that I can find their records")
	void shouldSearchForExistingOwners() {
		// Given I am a visitor to the PetClinic application
		navigateToHome();

		// When I navigate to Find Owners
		clickLink("Find Owners");

		// Then I should see the search form
		assertThat(page).hasURL(baseUrl + "/owners/find");
		assertThat(page.locator("h2")).containsText("Find Owners");

		// When I search for "Davis"
		page.locator("#lastName").fill("Davis");
		clickButton("Find Owner");

		// Then I should see matching owners
		assertThat(page).hasURL(baseUrl + "/owners?lastName=Davis");
		assertThat(page.locator("h2")).containsText("Owners");
		assertThat(page.locator("table")).containsText("Betty Davis");
		assertThat(page.locator("table")).containsText("Harold Davis");
	}

	@Test
	@DisplayName("As a clinic staff member, I want to view owner details and manage their pets, so that I can provide proper care")
	void shouldViewOwnerDetailsAndManagePets() {
		// Given I have found an owner
		navigateTo("/owners/find");
		page.locator("#lastName").fill("Davis");
		clickButton("Find Owner");

		// When I click on an owner's name
		page.locator("a")
			.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Betty Davis"))
			.click();

		// Then I should see their detailed information
		assertThat(page).hasURL(baseUrl + "/owners/2");
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("table")).containsText("Betty Davis");
		assertThat(page.locator("table")).containsText("638 Cardinal Ave.");
		assertThat(page.locator("table")).containsText("Sun Prairie");

		// And I should see their pets
		assertThat(page.locator("h2")).containsText("Pets and Visits");
		assertThat(page.locator("table")).containsText("Basil");
		assertThat(page.locator("table")).containsText("hamster");
	}

	@Test
	@DisplayName("As a clinic staff member, I want to add a new pet to an owner, so that I can track their care")
	void shouldAddNewPetToOwner() {
		// Given I am viewing an owner's details
		navigateTo("/owners/2");

		// When I click Add New Pet
		clickLink("Add New Pet");

		// Then I should see the new pet form
		assertThat(page).hasURL(baseUrl + "/owners/2/pets/new");
		assertThat(page.locator("h2")).containsText("New Pet");

		// When I fill in the pet details
		page.locator("input[name='name']").fill("Fluffy");
		page.locator("input[name='birthDate']").fill("2023-01-15");
		page.locator("select[name='type']").selectOption("cat");
		clickButton("Add Pet");

		// Then I should be back on the owner's page with the new pet
		assertThat(page).hasURL(baseUrl + "/owners/2");
		assertThat(page.locator("table")).containsText("Fluffy");
		assertThat(page.locator("table")).containsText("cat");
	}

	@Test
	@DisplayName("As a veterinarian, I want to add visits to pets, so that I can track medical history")
	void shouldAddVisitToPet() {
		// Given I am viewing an owner with pets
		navigateTo("/owners/2");

		// When I click Add Visit for a pet
		page.locator("a")
			.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Add Visit"))
			.first()
			.click();

		// Then I should see the visit form
		assertThat(page.locator("h2")).containsText("New Visit");

		// When I fill in visit details
		page.locator("input[name='date']").fill("2024-01-15");
		page.locator("textarea[name='description']").fill("Routine checkup - pet is healthy");
		clickButton("Add Visit");

		// Then I should see the visit in the history
		assertThat(page).hasURL(baseUrl + "/owners/2");
		assertThat(page.locator("table")).containsText("2024-01-15");
		assertThat(page.locator("table")).containsText("Routine checkup - pet is healthy");
	}

	@Test
	@DisplayName("As a clinic visitor, I want to view the list of veterinarians, so that I can see who is available")
	void shouldViewVeterinariansList() {
		// Given I am on the home page
		navigateToHome();

		// When I navigate to Veterinarians
		clickLink("Veterinarians");

		// Then I should see the veterinarians list
		assertThat(page).hasURL(baseUrl + "/vets.html");
		assertThat(page.locator("h2")).containsText("Veterinarians");
		assertThat(page.locator("table")).isVisible();

		// And I should see vet information
		assertThat(page.locator("table")).containsText("Name");
		assertThat(page.locator("table")).containsText("Specialties");
		assertThat(page.locator("table")).containsText("James Carter");
		assertThat(page.locator("table")).containsText("Helen Leary");
		assertThat(page.locator("table")).containsText("radiology");
		assertThat(page.locator("table")).containsText("surgery");
	}

	@Test
	@DisplayName("As a user, I want to see appropriate error pages when problems occur, so that I understand what happened")
	void shouldDisplayErrorPageGracefully() {
		// Given I am using the application
		navigateToHome();

		// When I trigger an error by clicking the Error link
		clickLink("Error");

		// Then I should see a user-friendly error page
		assertThat(page).hasURL(baseUrl + "/oups");
		assertThat(page.locator("h2")).containsText("Something happened...");

		// And I should still be able to navigate
		assertThat(page.locator("nav")).isVisible();
		clickLink("Home");
		assertThat(page).hasURL(baseUrl + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@Test
	@DisplayName("As a clinic staff member, I want to add a new owner, so that I can register new customers")
	void shouldAddNewOwner() {
		// Given I am on the Find Owners page
		navigateTo("/owners/find");

		// When I click Add Owner
		clickLink("Add Owner");

		// Then I should see the new owner form
		assertThat(page).hasURL(baseUrl + "/owners/new");
		assertThat(page.locator("h2")).containsText("Owner");

		// When I fill in owner details
		page.locator("#firstName").fill("John");
		page.locator("#lastName").fill("Doe");
		page.locator("#address").fill("123 Main St");
		page.locator("#city").fill("Madison");
		page.locator("#telephone").fill("6085551234");
		clickButton("Add Owner");

		// Then I should see the new owner's details
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("table")).containsText("John Doe");
		assertThat(page.locator("table")).containsText("123 Main St");
		assertThat(page.locator("table")).containsText("Madison");
	}

	@Test
	@DisplayName("As a user, I want navigation to work consistently throughout the application, so that I can move between features easily")
	void shouldHaveConsistentNavigationThroughoutApplication() {
		// Given I start at the home page
		navigateToHome();
		assertThat(page.locator("h2")).containsText("Welcome");

		// When I navigate through different sections
		clickLink("Find Owners");
		assertThat(page.locator("h2")).containsText("Find Owners");

		clickLink("Veterinarians");
		assertThat(page.locator("h2")).containsText("Veterinarians");

		clickLink("Home");
		assertThat(page.locator("h2")).containsText("Welcome");

		// Then all navigation should work consistently
		// And the navigation menu should always be present
		assertThat(page.locator("nav")).isVisible();
		assertThat(page.locator("a").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Home")))
			.isVisible();
		assertThat(page.locator("a")
			.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Find Owners"))).isVisible();
		assertThat(page.locator("a")
			.filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Veterinarians"))).isVisible();
	}

}