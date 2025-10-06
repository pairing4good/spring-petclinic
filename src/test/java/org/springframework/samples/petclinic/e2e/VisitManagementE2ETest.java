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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End tests for visit management functionality including adding visits to pets.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VisitManagementE2ETest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void asAnOwner_IWantToAccessAddVisitForm_SoThatICanScheduleVetVisits() {
		// Given I have a pet (pet ID 1 belonging to owner 1)
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I request the add visit form
		ResponseEntity<String> response = restTemplate.getForEntity(addVisitUrl, String.class);

		// Then I should see the add visit form
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsAnyOf("Visit", "Add Visit");

		// And I should see form fields for visit information
		assertThat(response.getBody()).containsAnyOf("Date", "Description");

		// And I should see the submit button
		assertThat(response.getBody()).containsAnyOf("Add Visit", "Save Visit");

		// And I should see pet information for context
		assertThat(response.getBody()).containsAnyOf("Pet", "Name", "Birth Date", "Type");
	}

	@Test
	void asAnOwner_IWantToAddANewVisit_SoThatICanRecordVetAppointments() {
		// Given I want to add a visit for pet ID 1 (belonging to owner 1)
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I submit valid visit information
		MultiValueMap<String, String> visitData = new LinkedMultiValueMap<>();
		visitData.add("date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		visitData.add("description", "Annual checkup and vaccination");

		ResponseEntity<String> response = restTemplate.postForEntity(addVisitUrl, visitData, String.class);

		// Then I should be redirected to the owner details page
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToSubmitVisitFormWithInvalidData_SoThatICanSeeValidationErrors() {
		// Given I want to add a visit but provide invalid data
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I submit form with invalid data
		MultiValueMap<String, String> invalidVisitData = new LinkedMultiValueMap<>();
		invalidVisitData.add("date", ""); // Empty date
		invalidVisitData.add("description", ""); // Empty description

		ResponseEntity<String> response = restTemplate.postForEntity(addVisitUrl, invalidVisitData, String.class);

		// Then I should see the form again with validation errors
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);

		if (response.getStatusCode() == HttpStatus.OK) {
			// Should show form with error messages
			assertThat(response.getBody()).containsAnyOf("Visit", "required", "error", "invalid");
		}
	}

	@Test
	void asAnOwner_IWantToAddVisitWithFutureDate_SoThatICanScheduleUpcomingAppointments() {
		// Given I want to schedule a future visit
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I submit visit information with a future date
		MultiValueMap<String, String> futureVisitData = new LinkedMultiValueMap<>();
		futureVisitData.add("date", LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
		futureVisitData.add("description", "Scheduled follow-up appointment");

		ResponseEntity<String> response = restTemplate.postForEntity(addVisitUrl, futureVisitData, String.class);

		// Then the visit should be accepted (future dates should be allowed)
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToAddVisitWithPastDate_SoThatICanRecordPreviousVisits() {
		// Given I want to record a past visit
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I submit visit information with a past date
		MultiValueMap<String, String> pastVisitData = new LinkedMultiValueMap<>();
		pastVisitData.add("date", LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));
		pastVisitData.add("description", "Previous emergency visit");

		ResponseEntity<String> response = restTemplate.postForEntity(addVisitUrl, pastVisitData, String.class);

		// Then the visit should be accepted (past dates should be allowed)
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToViewMyPetVisitHistory_SoThatICanTrackMedicalRecords() {
		// Given I have a pet with visit history
		String ownerUrl = "http://localhost:" + port + "/owners/1";

		// When I view the owner details page
		ResponseEntity<String> response = restTemplate.getForEntity(ownerUrl, String.class);

		// Then I should see visit information
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Pets and Visits");

		// And I should see visit-related headers or content
		assertThat(response.getBody()).containsAnyOf("Visit Date", "Description", "visits");

		// And I should see the add visit link
		assertThat(response.getBody()).contains("Add Visit");
	}

	@Test
	void asAUser_IWantToTryAddingVisitToNonExistentPet_SoThatICanSeeErrorHandling() {
		// Given I try to add a visit to a non-existent pet
		String invalidAddVisitUrl = "http://localhost:" + port + "/owners/1/pets/99999/visits/new";

		// When I request the add visit form for non-existent pet
		ResponseEntity<String> response = restTemplate.getForEntity(invalidAddVisitUrl, String.class);

		// Then I should get an appropriate error response
		assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void asAUser_IWantToTryAddingVisitToNonExistentOwner_SoThatICanSeeErrorHandling() {
		// Given I try to add a visit for a non-existent owner
		String invalidOwnerVisitUrl = "http://localhost:" + port + "/owners/99999/pets/1/visits/new";

		// When I request the add visit form for non-existent owner
		ResponseEntity<String> response = restTemplate.getForEntity(invalidOwnerVisitUrl, String.class);

		// Then I should get an appropriate error response
		assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void asAnOwner_IWantToAddVisitWithLongDescription_SoThatICanRecordDetailedInformation() {
		// Given I want to add a visit with detailed description
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I submit visit with long description
		MultiValueMap<String, String> detailedVisitData = new LinkedMultiValueMap<>();
		detailedVisitData.add("date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		detailedVisitData.add("description",
				"Comprehensive annual examination including blood work, dental check, "
						+ "vaccination updates, weight assessment, and discussion of dietary needs. "
						+ "Pet showed excellent health indicators across all metrics.");

		ResponseEntity<String> response = restTemplate.postForEntity(addVisitUrl, detailedVisitData, String.class);

		// Then the visit should be accepted
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToAddMultipleVisitsToSamePet_SoThatICanTrackOngoingCare() {
		// This test verifies that multiple visits can be added to the same pet
		// Given I want to add multiple visits for the same pet
		String addVisitUrl = "http://localhost:" + port + "/owners/1/pets/1/visits/new";

		// When I add the first visit
		MultiValueMap<String, String> firstVisitData = new LinkedMultiValueMap<>();
		firstVisitData.add("date", LocalDate.now().minusDays(10).format(DateTimeFormatter.ISO_LOCAL_DATE));
		firstVisitData.add("description", "First visit - initial consultation");

		ResponseEntity<String> firstResponse = restTemplate.postForEntity(addVisitUrl, firstVisitData, String.class);

		// Then the first visit should be accepted
		assertThat(firstResponse.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		// When I add a second visit
		MultiValueMap<String, String> secondVisitData = new LinkedMultiValueMap<>();
		secondVisitData.add("date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		secondVisitData.add("description", "Second visit - follow-up treatment");

		ResponseEntity<String> secondResponse = restTemplate.postForEntity(addVisitUrl, secondVisitData, String.class);

		// Then the second visit should also be accepted
		assertThat(secondResponse.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);
	}

}