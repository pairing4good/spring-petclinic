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

		// When I submit my registration information
		MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
		ownerData.add("firstName", firstName);
		ownerData.add("lastName", lastName);
		ownerData.add("address", address);
		ownerData.add("city", city);
		ownerData.add("telephone", telephone);

		ResponseEntity<String> response = restTemplate().postForEntity("/owners/new", ownerData, String.class);

		// Then my registration should be successful
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND); // Redirect
																			// after
																			// successful
																			// creation

		// And I should be redirected to my owner details page
		String location = response.getHeaders().getLocation().toString();
		assertThat(location).matches(".*\\/owners\\/\\d+$"); // Should redirect to
																// /owners/{id}

		// And I should be able to view my information
		ResponseEntity<String> detailsResponse = restTemplate().getForEntity(location, String.class);
		assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(detailsResponse.getBody()).contains(firstName);
		assertThat(detailsResponse.getBody()).contains(lastName);
		assertThat(detailsResponse.getBody()).contains(address);
		assertThat(detailsResponse.getBody()).contains(city);
		assertThat(detailsResponse.getBody()).contains(telephone);
	}

	@Test
	@DisplayName("As a pet owner, I cannot register with incomplete information")
	void petOwnerCannotRegisterWithIncompleteInformation() {
		// Given I am a new pet owner with incomplete information
		MultiValueMap<String, String> incompleteData = new LinkedMultiValueMap<>();
		incompleteData.add("firstName", "Bob");
		incompleteData.add("lastName", "Smith");
		// Missing required fields: address, city, telephone

		// When I try to register with incomplete information
		ResponseEntity<String> response = restTemplate().postForEntity("/owners/new", incompleteData, String.class);

		// Then my registration should fail
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Returns form
																		// with errors
		assertThat(response.getBody()).contains("has-error"); // Should show validation
																// errors
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