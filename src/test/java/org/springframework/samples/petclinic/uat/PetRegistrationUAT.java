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
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Acceptance Tests for Pet Registration
 *
 * Story: As a pet owner, I want to add my pet to the system, so that I can track their
 * medical records.
 */
class PetRegistrationUAT extends BaseUserAcceptanceTest {

	private Owner testOwner;

	private PetType testPetType;

	@BeforeEach
	void setUp() {
		// Create a test owner for pet registration
		testOwner = new Owner();
		testOwner.setFirstName("John");
		testOwner.setLastName("Doe");
		testOwner.setAddress("123 Test Street");
		testOwner.setCity("Test City");
		testOwner.setTelephone("5551234567");
		testOwner = ownerRepository.save(testOwner);

		// Ensure we have a pet type available
		List<PetType> petTypes = petTypeRepository.findPetTypes();
		testPetType = petTypes.stream().filter(pt -> "dog".equals(pt.getName())).findFirst().orElse(null);
		if (testPetType == null) {
			testPetType = new PetType();
			testPetType.setName("dog");
			testPetType = petTypeRepository.save(testPetType);
		}
	}

	@Test
	@DisplayName("As a pet owner, I want to add my pet to the system, so that I can track their medical records")
	void petOwnerCanAddPetToTheSystem() {
		// Given I am a registered pet owner
		String petName = "Buddy";
		String birthDate = "2020-05-15";

		// When I add my pet to the system
		MultiValueMap<String, String> petData = new LinkedMultiValueMap<>();
		petData.add("name", petName);
		petData.add("birthDate", birthDate);
		petData.add("type", testPetType.getId().toString());

		String addPetUrl = "/owners/" + testOwner.getId() + "/pets/new";
		ResponseEntity<String> response = restTemplate().postForEntity(addPetUrl, petData, String.class);

		// Then my pet should be successfully added
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND); // Redirect
																			// after
																			// successful
																			// creation

		// And I should be redirected to my owner details page
		String location = response.getHeaders().getLocation().toString();
		assertThat(location).isEqualTo("/owners/" + testOwner.getId());

		// And I should see my pet in my owner details
		ResponseEntity<String> detailsResponse = restTemplate().getForEntity(location, String.class);
		assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(detailsResponse.getBody()).contains(petName);
		assertThat(detailsResponse.getBody()).contains("dog");
	}

	@Test
	@DisplayName("As a pet owner, I cannot add a pet without required information")
	void petOwnerCannotAddPetWithoutRequiredInformation() {
		// Given I am a registered pet owner with incomplete pet information
		MultiValueMap<String, String> incompletePetData = new LinkedMultiValueMap<>();
		incompletePetData.add("name", ""); // Empty name
		incompletePetData.add("birthDate", "2020-05-15");
		incompletePetData.add("type", testPetType.getId().toString());

		// When I try to add my pet with incomplete information
		String addPetUrl = "/owners/" + testOwner.getId() + "/pets/new";
		ResponseEntity<String> response = restTemplate().postForEntity(addPetUrl, incompletePetData, String.class);

		// Then the pet addition should fail
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Returns form
																		// with errors
		assertThat(response.getBody()).contains("createOrUpdatePetForm"); // Should return
																			// to the form
	}

	@Test
	@DisplayName("As a pet owner, I can update my pet's information")
	void petOwnerCanUpdatePetInformation() {
		// Given I have a pet registered in the system
		MultiValueMap<String, String> initialPetData = new LinkedMultiValueMap<>();
		initialPetData.add("name", "Max");
		initialPetData.add("birthDate", "2019-03-10");
		initialPetData.add("type", testPetType.getId().toString());

		String addPetUrl = "/owners/" + testOwner.getId() + "/pets/new";
		restTemplate().postForEntity(addPetUrl, initialPetData, String.class);

		// Get the owner details to find the pet ID
		ResponseEntity<String> ownerResponse = restTemplate().getForEntity("/owners/" + testOwner.getId(),
				String.class);
		String ownerDetails = ownerResponse.getBody();

		// Extract pet ID from the HTML (simple approach for UAT)
		// Look for edit pet link pattern: /owners/{ownerId}/pets/{petId}/edit
		String editLinkPattern = "/owners/" + testOwner.getId() + "/pets/";
		int startIndex = ownerDetails.indexOf(editLinkPattern) + editLinkPattern.length();
		int endIndex = ownerDetails.indexOf("/edit", startIndex);
		String petId = ownerDetails.substring(startIndex, endIndex);

		// When I update my pet's information
		MultiValueMap<String, String> updatedPetData = new LinkedMultiValueMap<>();
		updatedPetData.add("name", "Maxwell"); // Updated name
		updatedPetData.add("birthDate", "2019-03-10");
		updatedPetData.add("type", testPetType.getId().toString());

		String updatePetUrl = "/owners/" + testOwner.getId() + "/pets/" + petId + "/edit";
		ResponseEntity<String> updateResponse = restTemplate().postForEntity(updatePetUrl, updatedPetData,
				String.class);

		// Then the update should be successful
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		// And I should see the updated information
		ResponseEntity<String> updatedOwnerResponse = restTemplate().getForEntity("/owners/" + testOwner.getId(),
				String.class);
		assertThat(updatedOwnerResponse.getBody()).contains("Maxwell");
	}

}