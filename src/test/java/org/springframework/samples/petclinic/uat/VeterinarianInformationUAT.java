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
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Acceptance Tests for Veterinarian Information
 *
 * Story: As a clinic staff member, I want to view veterinarian information, so that I can
 * assist pet owners with scheduling appointments.
 */
class VeterinarianInformationUAT extends BaseUserAcceptanceTest {

	@BeforeEach
	void setUp() {
		// Note: Test data is loaded from data.sql, so we have vets available
		// If we need additional test vets, we would use ownerRepository.save() for owners
		// but VetRepository doesn't extend CrudRepository, so we rely on existing data
	}

	@Test
	@DisplayName("As a clinic staff member, I want to view veterinarian information, so that I can assist pet owners")
	void clinicStaffCanViewVeterinarianInformation() {
		// Given I am a clinic staff member
		// When I access the veterinarian list
		ResponseEntity<String> response = restTemplate().getForEntity("/vets.html", String.class);

		// Then I should see the list of veterinarians
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Veterinarians");

		// And I should see veterinarian details
		assertThat(response.getBody()).contains("James Carter");
		assertThat(response.getBody()).contains("Helen Leary");
		assertThat(response.getBody()).contains("Linda Douglas");
	}

	@Test
	@DisplayName("As a clinic staff member, I can view veterinarian specialties to help with appointment scheduling")
	void clinicStaffCanViewVeterinarianSpecialties() {
		// Given I am a clinic staff member looking for specialist information
		// When I access the veterinarian list
		ResponseEntity<String> response = restTemplate().getForEntity("/vets.html", String.class);

		// Then I should see veterinarian specialties
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("radiology");
		assertThat(response.getBody()).contains("surgery");
		assertThat(response.getBody()).contains("dentistry");
	}

	@Test
	@DisplayName("As a system integrator, I can access veterinarian data in JSON format for external systems")
	void systemCanAccessVeterinarianDataInJsonFormat() {
		// Given I need veterinarian data for system integration
		// When I request veterinarian data in JSON format
		ResponseEntity<String> response = restTemplate().getForEntity("/vets", String.class);

		// Then I should receive veterinarian data in JSON format
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType().toString()).contains("application/json");

		// And the JSON should contain veterinarian information
		String jsonBody = response.getBody();
		assertThat(jsonBody).contains("\"firstName\":\"James\"");
		assertThat(jsonBody).contains("\"lastName\":\"Carter\"");
		assertThat(jsonBody).contains("\"firstName\":\"Helen\"");
		assertThat(jsonBody).contains("\"lastName\":\"Leary\"");
	}

	@Test
	@DisplayName("As a clinic staff member, I can paginate through veterinarian list when there are many vets")
	void clinicStaffCanPaginateThroughVeterinarianList() {
		// Given I am a clinic staff member with access to veterinarian list
		// When I access the veterinarian list with pagination
		ResponseEntity<String> firstPageResponse = restTemplate().getForEntity("/vets.html?page=1", String.class);

		// Then I should see the first page of veterinarians
		assertThat(firstPageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(firstPageResponse.getBody()).contains("Veterinarians");

		// And the page should contain veterinarian information
		assertThat(firstPageResponse.getBody()).contains("James Carter");
	}

}