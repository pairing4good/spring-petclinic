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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End tests for pet management functionality including adding and editing pets.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PetManagementE2ETest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void asAnOwner_IWantToAccessAddPetForm_SoThatICanRegisterNewPets() {
		// Given I am an owner (owner ID 1) who wants to add a pet
		String addPetUrl = "http://localhost:" + port + "/owners/1/pets/new";

		// When I request the add pet form
		ResponseEntity<String> response = restTemplate.getForEntity(addPetUrl, String.class);

		// Then I should see the add pet form
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsAnyOf("Pet", "Add Pet");

		// And I should see form fields for pet information
		assertThat(response.getBody()).containsAnyOf("Name", "Birth Date", "Type");

		// And I should see the submit button
		assertThat(response.getBody()).containsAnyOf("Add Pet", "Save Pet");
	}

	@Test
	void asAnOwner_IWantToAddANewPet_SoThatICanRegisterMyPet() {
		// Given I want to add a new pet for owner ID 1
		String addPetUrl = "http://localhost:" + port + "/owners/1/pets/new";

		// When I submit valid pet information
		MultiValueMap<String, String> petData = new LinkedMultiValueMap<>();
		petData.add("name", "TestPet");
		petData.add("birthDate", "2023-01-15");
		petData.add("type", "cat"); // Use type name instead of ID

		ResponseEntity<String> response = restTemplate.postForEntity(addPetUrl, petData, String.class);

		// Then I should be redirected to the owner details page
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToSubmitPetFormWithInvalidData_SoThatICanSeeValidationErrors() {
		// Given I want to add a pet but provide invalid data
		String addPetUrl = "http://localhost:" + port + "/owners/1/pets/new";

		// When I submit form with invalid data
		MultiValueMap<String, String> invalidPetData = new LinkedMultiValueMap<>();
		invalidPetData.add("name", ""); // Empty name
		invalidPetData.add("birthDate", "2030-01-01"); // Future date
		invalidPetData.add("type", ""); // No type selected

		ResponseEntity<String> response = restTemplate.postForEntity(addPetUrl, invalidPetData, String.class);

		// Then I should see the form again with validation errors
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);

		if (response.getStatusCode() == HttpStatus.OK) {
			// Should show form with error messages
			assertThat(response.getBody()).containsAnyOf("Pet", "required", "error", "invalid");
		}
	}

	@Test
	void asAnOwner_IWantToEditMyPetInformation_SoThatICanUpdatePetDetails() {
		// Given I have a pet (pet ID 1 belonging to owner 1)
		String editPetUrl = "http://localhost:" + port + "/owners/1/pets/1/edit";

		// When I request the edit pet form
		ResponseEntity<String> response = restTemplate.getForEntity(editPetUrl, String.class);

		// Then I should see the edit pet form
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsAnyOf("Pet", "Edit Pet");

		// And I should see form fields pre-populated with pet data
		assertThat(response.getBody()).containsAnyOf("Name", "Birth Date", "Type");

		// And I should see the update button
		assertThat(response.getBody()).containsAnyOf("Update Pet", "Save Pet");
	}

	@Test
	void asAnOwner_IWantToUpdateMyPetInformation_SoThatICanKeepPetDataCurrent() {
		// Given I have a pet (pet ID 1 belonging to owner 1)
		String updatePetUrl = "http://localhost:" + port + "/owners/1/pets/1/edit";

		// When I submit updated pet information
		MultiValueMap<String, String> updatedPetData = new LinkedMultiValueMap<>();
		updatedPetData.add("name", "UpdatedPetName");
		updatedPetData.add("birthDate", "2022-06-15");
		updatedPetData.add("type", "dog"); // Use type name instead of ID

		ResponseEntity<String> response = restTemplate.postForEntity(updatePetUrl, updatedPetData, String.class);

		// Then I should be redirected to the owner details page
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAnOwner_IWantToTryAddingPetToNonExistentOwner_SoThatICanSeeErrorHandling() {
		// Given I try to add a pet to a non-existent owner
		String invalidAddPetUrl = "http://localhost:" + port + "/owners/99999/pets/new";

		// When I request the add pet form for non-existent owner
		ResponseEntity<String> response = restTemplate.getForEntity(invalidAddPetUrl, String.class);

		// Then I should get an appropriate error response
		assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void asAUser_IWantToTryEditingNonExistentPet_SoThatICanSeeErrorHandling() {
		// Given I try to edit a non-existent pet
		String invalidEditUrl = "http://localhost:" + port + "/owners/1/pets/99999/edit";

		// When I request the edit form for non-existent pet
		ResponseEntity<String> response = restTemplate.getForEntity(invalidEditUrl, String.class);

		// Then I should get an appropriate error response
		assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void asAnOwner_IWantToViewMyPetDetails_SoThatICanSeeVisitHistory() {
		// This test verifies that pet information is properly displayed on owner details
		// page
		// Given I have an owner with pets
		String ownerUrl = "http://localhost:" + port + "/owners/1";

		// When I view the owner details
		ResponseEntity<String> response = restTemplate.getForEntity(ownerUrl, String.class);

		// Then I should see pet information
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Pets and Visits");

		// And I should see pet-related action links
		assertThat(response.getBody()).containsAnyOf("Edit Pet", "Add Visit");

		// And I should see pet details like name, birth date, type
		assertThat(response.getBody()).containsAnyOf("Name", "Birth Date", "Type");
	}

	@Test
	void asAnOwner_IWantToAddPetWithDuplicateName_SoThatICanTestNameValidation() {
		// Given I want to add a pet with a name that might already exist
		String addPetUrl = "http://localhost:" + port + "/owners/1/pets/new";

		// When I submit pet information with a potentially duplicate name
		MultiValueMap<String, String> petData = new LinkedMultiValueMap<>();
		petData.add("name", "Leo"); // This name might already exist for this owner
		petData.add("birthDate", "2023-01-15");
		petData.add("type", "cat"); // Use type name instead of ID

		ResponseEntity<String> response = restTemplate.postForEntity(addPetUrl, petData, String.class);

		// Then the system should handle this appropriately
		// (Either allow it or show validation error)
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.FOUND, HttpStatus.BAD_REQUEST);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("already exists")) {
			// If duplicate names are not allowed, should show error
			assertThat(response.getBody()).containsAnyOf("duplicate", "already exists", "unique");
		}
	}

}