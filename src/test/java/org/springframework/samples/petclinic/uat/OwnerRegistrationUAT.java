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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Acceptance Tests for Owner Registration
 *
 * Story: As a pet owner, I want to register myself in the system, so that I can manage my
 * pet's information.
 */
class OwnerRegistrationUAT extends BaseUserAcceptanceTest {

	@Test
	@DisplayName("As a pet owner, I want to register myself in the system, so that I can manage my pet's information")
	void petOwnerCanRegisterInTheSystem() {
		// Given I am a new pet owner who wants to register
		String firstName = "Alice";
		String lastName = "Johnson";
		String address = "123 Main Street";
		String city = "Springfield";
		String telephone = "5551234567";

		// When I register in the system (using the service layer for reliable testing)
		Owner newOwner = new Owner();
		newOwner.setFirstName(firstName);
		newOwner.setLastName(lastName);
		newOwner.setAddress(address);
		newOwner.setCity(city);
		newOwner.setTelephone(telephone);

		Owner savedOwner = ownerRepository.save(newOwner);

		// Then my registration should be successful
		assertThat(savedOwner.getId()).isNotNull();
		assertThat(savedOwner.getFirstName()).isEqualTo(firstName);
		assertThat(savedOwner.getLastName()).isEqualTo(lastName);
		assertThat(savedOwner.getAddress()).isEqualTo(address);
		assertThat(savedOwner.getCity()).isEqualTo(city);
		assertThat(savedOwner.getTelephone()).isEqualTo(telephone);

		// And I should be able to find myself in the system
		Owner foundOwner = ownerRepository.findById(savedOwner.getId()).orElse(null);
		assertThat(foundOwner).isNotNull();
		assertThat(foundOwner.getFirstName()).isEqualTo(firstName);
		assertThat(foundOwner.getLastName()).isEqualTo(lastName);
	}

	@Test
	@DisplayName("As a pet owner, I cannot register with incomplete information")
	void petOwnerCannotRegisterWithIncompleteInformation() {
		// Given I am a new pet owner with incomplete information
		Owner incompleteOwner = new Owner();
		incompleteOwner.setFirstName("Bob");
		incompleteOwner.setLastName("Smith");
		// Missing required fields: address, city, telephone

		// When I try to register with incomplete information
		// Then the registration should fail due to validation
		try {
			ownerRepository.save(incompleteOwner);
			// If we get here without exception, the test should fail
			assert false : "Expected validation exception for incomplete owner data";
		}
		catch (Exception e) {
			// Expected - validation should prevent saving incomplete data
			assertThat(e).isNotNull();
		}
	}

	@Test
	@DisplayName("As a clinic staff member, I can search for registered owners")
	void clinicStaffCanSearchForRegisteredOwners() {
		// Given there is a registered owner in the system
		String lastName = "Wilson";
		MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
		ownerData.add("firstName", "Emma");
		ownerData.add("lastName", lastName);
		ownerData.add("address", "456 Oak Avenue");
		ownerData.add("city", "Riverside");
		ownerData.add("telephone", "5557654321");

		restTemplate().postForEntity("/owners/new", ownerData, String.class);

		// When I search for the owner by last name
		String searchUrl = "/owners?lastName=" + lastName;
		ResponseEntity<String> searchResponse = restTemplate().getForEntity(searchUrl, String.class);

		// Then I should find the owner in the search results
		assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(searchResponse.getBody()).contains(lastName);
		assertThat(searchResponse.getBody()).contains("Emma");
	}

}