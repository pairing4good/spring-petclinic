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

package org.springframework.samples.petclinic.uat;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * End-to-End User Acceptance Test for Appointment Booking using Playwright
 *
 * Story: As a pet owner, I want to book a veterinary appointment through the web
 * interface, so that I can bring my pet for medical care with confidence in the booking
 * process.
 *
 * This test provides the "happy path" end-to-end verification using browser automation to
 * ensure the complete user journey works correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create-drop" })
@Transactional
class AppointmentBookingBrowserUAT {

	@LocalServerPort
	private int port;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	private static Playwright playwright;

	private static Browser browser;

	private BrowserContext context;

	private Page page;

	private String baseUrl;

	@BeforeAll
	static void setUpClass() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
	}

	@AfterAll
	static void tearDownClass() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void setUp() {
		baseUrl = "http://localhost:" + port;
		context = browser.newContext();
		page = context.newPage();

		// Set up test data
		setupTestData();
	}

	@AfterEach
	void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	private void setupTestData() {
		// Create pet type
		List<PetType> petTypes = petTypeRepository.findPetTypes();
		PetType dogType = petTypes.stream().filter(pt -> "dog".equals(pt.getName())).findFirst().orElse(null);
		if (dogType == null) {
			dogType = new PetType();
			dogType.setName("dog");
			petTypeRepository.save(dogType);
		}

		// Create owner with pet
		Owner owner = new Owner();
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("123 Main Street");
		owner.setCity("Springfield");
		owner.setTelephone("5551234567");

		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 5, 15));
		pet.setType(dogType);
		owner.addPet(pet);

		ownerRepository.save(owner);
	}

	@Test
	@DisplayName("Complete Happy Path: As a pet owner, I can navigate through the web interface to book an appointment")
	void completeAppointmentBookingHappyPath() {
		// Given I am on the Pet Clinic homepage
		page.navigate(baseUrl);
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

		// When I search for my owner information
		page.click("text=Find owners");
		assertThat(page).hasURL(baseUrl + "/owners/find");

		// And I search by last name
		page.fill("input[name='lastName']", "Smith");
		page.click("button[type='submit']");

		// Then I should see my owner information
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("text=Alice Smith")).isVisible();

		// And I should see my pet information
		assertThat(page.locator("text=Buddy")).isVisible();
		assertThat(page.locator("text=dog")).isVisible();

		// When I click to add a new visit for my pet
		page.click("text=Add Visit");

		// Then I should be on the new visit form
		assertThat(page.locator("h2")).containsText("New Visit");
		assertThat(page.locator("text=Buddy")).isVisible();

		// When I fill in the visit details
		String visitDate = LocalDate.now().toString();
		String visitDescription = "Annual checkup and vaccination - Browser Test";

		page.fill("input[name='date']", visitDate);
		page.fill("textarea[name='description']", visitDescription);

		// And I submit the visit form
		page.click("button[type='submit']");

		// Then I should be redirected back to the owner page
		assertThat(page.locator("h2")).containsText("Owner Information");

		// And I should see a success message
		assertThat(page.locator("text=Your visit has been booked")).isVisible();

		// And I should see the new visit in the visit history
		assertThat(page.locator("h2:has-text('Visits')")).isVisible();
		assertThat(page.locator("text=" + visitDescription)).isVisible();
		assertThat(page.locator("text=" + visitDate)).isVisible();
	}

	@Test
	@DisplayName("Validation: As a pet owner, I receive clear feedback when I try to book an appointment without required information")
	void appointmentBookingValidationFeedback() {
		// Given I am on the appointment booking page
		page.navigate(baseUrl);
		page.click("text=Find owners");
		page.fill("input[name='lastName']", "Smith");
		page.click("button[type='submit']");
		page.click("text=Add Visit");

		// When I try to submit the form without a description
		page.fill("input[name='date']", LocalDate.now().toString());
		// Leave description empty
		page.click("button[type='submit']");

		// Then I should see validation errors
		assertThat(page.locator("h2")).containsText("New Visit");
		// The form should still be displayed, indicating validation failed
		assertThat(page.locator("textarea[name='description']")).isVisible();
	}

	@Test
	@DisplayName("Navigation: As a pet owner, I can easily navigate between different sections of the application")
	void easyNavigationBetweenSections() {
		// Given I am on the homepage
		page.navigate(baseUrl);

		// When I navigate to different sections
		page.click("text=Veterinarians");
		assertThat(page.locator("h2")).containsText("Veterinarians");

		// And navigate back to find owners
		page.click("text=Find owners");
		assertThat(page).hasURL(baseUrl + "/owners/find");

		// And go to the homepage
		page.click("text=Home");
		assertThat(page).hasURL(baseUrl + "/");

		// Then the navigation should work smoothly
		assertThat(page.locator("text=Welcome")).isVisible();
	}

}