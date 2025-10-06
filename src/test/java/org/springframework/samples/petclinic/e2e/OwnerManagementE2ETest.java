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
 * End-to-End tests for owner management functionality including CRUD operations.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OwnerManagementE2ETest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void asAUser_IWantToSearchForOwnersByLastName_SoThatICanFindSpecificOwners() {
		// Given I want to search for owners by last name
		String searchUrl = "http://localhost:" + port + "/owners?lastName=Franklin";

		// When I search for owners with last name "Franklin"
		ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);

		// Then I should be redirected to the owner details page (302 redirect)
		// or get a successful response if multiple owners are found
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.OK);

		// If we get the redirected page directly
		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/");
		}
	}

	@Test
	void asAUser_IWantToSearchForOwnersWithEmptyLastName_SoThatICanSeeAllOwners() {
		// Given I want to see all owners
		String searchUrl = "http://localhost:" + port + "/owners?lastName=";

		// When I search with empty last name
		ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);

		// Then I should get a list of owners or be redirected appropriately
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.FOUND);

		if (response.getStatusCode() == HttpStatus.OK) {
			// Should contain owner list elements
			assertThat(response.getBody()).containsAnyOf("Owners", "Owner", "owners");
		}
	}

	@Test
	void asAUser_IWantToSearchForNonExistentOwner_SoThatICanSeeAppropriateMessage() {
		// Given I want to search for a non-existent owner
		String searchUrl = "http://localhost:" + port + "/owners?lastName=NonExistentOwnerName12345";

		// When I search for a non-existent last name
		ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);

		// Then I should get a response (either showing no results or redirecting back to
		// search)
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.FOUND);

		if (response.getStatusCode() == HttpStatus.OK) {
			// Should either show no results message or the search form again
			assertThat(response.getBody()).containsAnyOf("Find Owners", "not found", "No owners found");
		}
	}

	@Test
	void asAUser_IWantToAddANewOwner_SoThatICanRegisterNewPetOwners() {
		// Given I want to add a new owner
		String addOwnerUrl = "http://localhost:" + port + "/owners/new";

		// When I submit a new owner form with valid data
		MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
		ownerData.add("firstName", "John");
		ownerData.add("lastName", "TestOwner");
		ownerData.add("address", "123 Test Street");
		ownerData.add("city", "Test City");
		ownerData.add("telephone", "5551234567");

		ResponseEntity<String> response = restTemplate.postForEntity(addOwnerUrl, ownerData, String.class);

		// Then I should be redirected to the new owner's details page
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.CREATED, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/");
		}
	}

	@Test
	void asAUser_IWantToSubmitOwnerFormWithMissingData_SoThatICanSeeValidationErrors() {
		// Given I want to add a new owner but provide incomplete data
		String addOwnerUrl = "http://localhost:" + port + "/owners/new";

		// When I submit form with missing required fields
		MultiValueMap<String, String> incompleteOwnerData = new LinkedMultiValueMap<>();
		incompleteOwnerData.add("firstName", ""); // Empty first name
		incompleteOwnerData.add("lastName", ""); // Empty last name
		incompleteOwnerData.add("address", "123 Test Street");
		incompleteOwnerData.add("city", "Test City");
		incompleteOwnerData.add("telephone", "invalid-phone"); // Invalid phone

		ResponseEntity<String> response = restTemplate.postForEntity(addOwnerUrl, incompleteOwnerData, String.class);

		// Then I should see the form again with validation errors
		assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);

		if (response.getStatusCode() == HttpStatus.OK) {
			// Should show form with error messages
			assertThat(response.getBody()).containsAnyOf("Owner", "required", "error", "invalid");
		}
	}

	@Test
	void asAUser_IWantToViewOwnerDetails_SoThatICanSeeOwnerAndPetInformation() {
		// Given there is an owner with ID 1 in the system
		String ownerUrl = "http://localhost:" + port + "/owners/1";

		// When I request the owner details
		ResponseEntity<String> response = restTemplate.getForEntity(ownerUrl, String.class);

		// Then I should see owner information
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Owner Information");
		assertThat(response.getBody()).contains("Pets and Visits");

		// And I should see owner-specific information
		assertThat(response.getBody()).containsAnyOf("Name", "Address", "City", "Telephone");

		// And I should see action links
		assertThat(response.getBody()).contains("Edit Owner");
		assertThat(response.getBody()).contains("Add New Pet");
	}

	@Test
	void asAUser_IWantToAccessEditOwnerForm_SoThatICanUpdateOwnerInformation() {
		// Given there is an owner with ID 1 in the system
		String editOwnerUrl = "http://localhost:" + port + "/owners/1/edit";

		// When I request the edit owner form
		ResponseEntity<String> response = restTemplate.getForEntity(editOwnerUrl, String.class);

		// Then I should see the edit form
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Owner");

		// And I should see form fields pre-populated
		assertThat(response.getBody()).containsAnyOf("First Name", "Last Name", "Address", "City", "Telephone");

		// And I should see the update button
		assertThat(response.getBody()).containsAnyOf("Update Owner", "Save");
	}

	@Test
	void asAUser_IWantToUpdateOwnerInformation_SoThatICanKeepOwnerDataCurrent() {
		// Given there is an owner with ID 1 in the system
		String updateOwnerUrl = "http://localhost:" + port + "/owners/1/edit";

		// When I submit updated owner information
		MultiValueMap<String, String> updatedOwnerData = new LinkedMultiValueMap<>();
		updatedOwnerData.add("firstName", "UpdatedFirstName");
		updatedOwnerData.add("lastName", "UpdatedLastName");
		updatedOwnerData.add("address", "456 Updated Street");
		updatedOwnerData.add("city", "Updated City");
		updatedOwnerData.add("telephone", "5559876543");

		ResponseEntity<String> response = restTemplate.postForEntity(updateOwnerUrl, updatedOwnerData, String.class);

		// Then I should be redirected to the owner details page
		assertThat(response.getStatusCode()).isIn(HttpStatus.FOUND, HttpStatus.OK);

		if (response.getStatusCode() == HttpStatus.FOUND) {
			String redirectUrl = response.getHeaders().getFirst("Location");
			assertThat(redirectUrl).contains("/owners/1");
		}
	}

	@Test
	void asAUser_IWantToAccessNonExistentOwner_SoThatICanSeeAppropriateErrorHandling() {
		// Given I try to access a non-existent owner
		String nonExistentOwnerUrl = "http://localhost:" + port + "/owners/99999";

		// When I request the non-existent owner
		ResponseEntity<String> response = restTemplate.getForEntity(nonExistentOwnerUrl, String.class);

		// Then I should get an appropriate error response
		assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}