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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Acceptance Tests for Appointment Booking (API Level)
 *
 * Story: As a pet owner, I want to book a veterinary appointment, so that I can bring my
 * pet for medical care.
 *
 * Note: This class focuses on fast API-level tests for appointment booking. End-to-end
 * browser tests are in AppointmentBookingBrowserUAT.
 */
class AppointmentBookingUAT extends BaseUserAcceptanceTest {

	private Owner testOwner;

	private Pet testPet;

	@BeforeEach
	void setUp() {
		// Create a test owner with a pet for appointment booking
		testOwner = new Owner();
		testOwner.setFirstName("Sarah");
		testOwner.setLastName("Connor");
		testOwner.setAddress("123 Future Street");
		testOwner.setCity("Los Angeles");
		testOwner.setTelephone("5551234567");
		testOwner = ownerRepository.save(testOwner);

		// Create a pet type
		List<PetType> petTypes = petTypeRepository.findPetTypes();
		PetType dogType = petTypes.stream().filter(pt -> "dog".equals(pt.getName())).findFirst().orElse(null);
		if (dogType == null) {
			dogType = new PetType();
			dogType.setName("dog");
			dogType = petTypeRepository.save(dogType);
		}

		// Create a test pet
		testPet = new Pet();
		testPet.setName("Rex");
		testPet.setBirthDate(LocalDate.of(2020, 6, 15));
		testPet.setType(dogType);
		testOwner.addPet(testPet);
		testOwner = ownerRepository.save(testOwner);

		// Get the saved pet with ID
		testPet = testOwner.getPets().iterator().next();
	}

	@Test
	@DisplayName("As a pet owner, I want to book a veterinary appointment, so that I can bring my pet for medical care")
	void petOwnerCanBookVeterinaryAppointment() {
		// Given I am a pet owner with a registered pet
		String visitDate = LocalDate.now().toString();
		String description = "Annual checkup and vaccination";

		// When I book an appointment for my pet
		MultiValueMap<String, String> visitData = new LinkedMultiValueMap<>();
		visitData.add("date", visitDate);
		visitData.add("description", description);

		String bookingUrl = "/owners/" + testOwner.getId() + "/pets/" + testPet.getId() + "/visits/new";
		ResponseEntity<String> response = restTemplate().postForEntity(bookingUrl, visitData, String.class);

		// Then the appointment should be successfully booked
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND); // Redirect
																			// after
																			// successful
																			// booking

		// And I should be redirected to my owner details page
		String location = response.getHeaders().getLocation().toString();
		assertThat(location).isEqualTo("/owners/" + testOwner.getId());

		// And I should see the appointment in my pet's visit history
		ResponseEntity<String> detailsResponse = restTemplate().getForEntity(location, String.class);
		assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(detailsResponse.getBody()).contains(description);
		assertThat(detailsResponse.getBody()).contains(visitDate);
	}

	@Test
	@DisplayName("As a pet owner, I cannot book an appointment without providing a description")
	void petOwnerCannotBookAppointmentWithoutDescription() {
		// Given I am a pet owner trying to book an appointment without description
		MultiValueMap<String, String> incompleteVisitData = new LinkedMultiValueMap<>();
		incompleteVisitData.add("date", LocalDate.now().toString());
		incompleteVisitData.add("description", ""); // Empty description

		// When I try to book an appointment without description
		String bookingUrl = "/owners/" + testOwner.getId() + "/pets/" + testPet.getId() + "/visits/new";
		ResponseEntity<String> response = restTemplate().postForEntity(bookingUrl, incompleteVisitData, String.class);

		// Then the appointment booking should fail
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Returns form
																		// with errors
		assertThat(response.getBody()).contains("createOrUpdateVisitForm"); // Should
																			// return to
																			// the form
	}

	@Test
	@DisplayName("As a pet owner, I can view my pet's visit history")
	void petOwnerCanViewPetVisitHistory() {
		// Given I have previously booked appointments for my pet
		String firstVisit = "Routine checkup";
		String secondVisit = "Follow-up examination";

		// Book first appointment
		MultiValueMap<String, String> firstVisitData = new LinkedMultiValueMap<>();
		firstVisitData.add("date", LocalDate.now().minusDays(30).toString());
		firstVisitData.add("description", firstVisit);

		String bookingUrl = "/owners/" + testOwner.getId() + "/pets/" + testPet.getId() + "/visits/new";
		restTemplate().postForEntity(bookingUrl, firstVisitData, String.class);

		// Book second appointment
		MultiValueMap<String, String> secondVisitData = new LinkedMultiValueMap<>();
		secondVisitData.add("date", LocalDate.now().minusDays(15).toString());
		secondVisitData.add("description", secondVisit);

		restTemplate().postForEntity(bookingUrl, secondVisitData, String.class);

		// When I view my owner details
		ResponseEntity<String> response = restTemplate().getForEntity("/owners/" + testOwner.getId(), String.class);

		// Then I should see all my pet's visit history
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains(firstVisit);
		assertThat(response.getBody()).contains(secondVisit);
		assertThat(response.getBody()).contains("Previous Visits");
	}

	@Test
	@DisplayName("As a pet owner, I can access the appointment booking form")
	void petOwnerCanAccessAppointmentBookingForm() {
		// Given I am a pet owner who wants to book an appointment
		// When I access the appointment booking form
		String formUrl = "/owners/" + testOwner.getId() + "/pets/" + testPet.getId() + "/visits/new";
		ResponseEntity<String> response = restTemplate().getForEntity(formUrl, String.class);

		// Then I should see the appointment booking form
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("New Visit");
		assertThat(response.getBody()).contains("Date");
		assertThat(response.getBody()).contains("Description");
		assertThat(response.getBody()).contains(testPet.getName());
	}

}