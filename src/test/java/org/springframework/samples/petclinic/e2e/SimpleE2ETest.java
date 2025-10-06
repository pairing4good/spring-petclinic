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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple E2E test to verify basic functionality without Playwright browser dependencies.
 * This serves as a foundation while browser setup is being configured.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleE2ETest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void asAVisitor_IWantToAccessTheHomePage_SoThatICanSeeTheApplication() {
		// Given I am a visitor to the PetClinic website
		String url = "http://localhost:" + port + "/";

		// When I request the home page
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Then I should receive a successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And the page should contain the welcome content
		assertThat(response.getBody()).contains("Welcome");
		assertThat(response.getBody()).contains("PetClinic :: a Spring Framework demonstration");
	}

	@Test
	void asAUser_IWantToAccessTheFindOwnersPage_SoThatICanSearchForOwners() {
		// Given I want to search for owners
		String url = "http://localhost:" + port + "/owners/find";

		// When I request the find owners page
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Then I should receive a successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And the page should contain the search form
		assertThat(response.getBody()).contains("Find Owners");
		assertThat(response.getBody()).contains("Last Name");
		assertThat(response.getBody()).contains("Find Owner");
		assertThat(response.getBody()).contains("Add Owner");
	}

	@Test
	void asAUser_IWantToAccessTheVeterinariansPage_SoThatICanViewVetInformation() {
		// Given I want to see veterinarian information
		String url = "http://localhost:" + port + "/vets.html";

		// When I request the veterinarians page
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Then I should receive a successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And the page should contain veterinarian information
		assertThat(response.getBody()).contains("Veterinarians");
	}

	@Test
	void asAUser_IWantToAccessTheErrorPage_SoThatICanSeeErrorHandling() {
		// Given I want to trigger the error page
		String url = "http://localhost:" + port + "/oups";

		// When I request the error page with HTML headers
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setAccept(java.util.List.of(org.springframework.http.MediaType.TEXT_HTML));
		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity,
				String.class);

		// Then I should receive an error response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

		// And the page should contain error information
		assertThat(response.getBody()).contains("Something happened...");
	}

	@Test
	void asAUser_IWantToAccessOwnerDetailsPage_SoThatICanViewOwnerInformation() {
		// Given there is an owner with ID 1 in the system
		String url = "http://localhost:" + port + "/owners/1";

		// When I request the owner details page
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Then I should receive a successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And the page should contain owner information
		assertThat(response.getBody()).contains("Owner Information");
		assertThat(response.getBody()).contains("Pets and Visits");
	}

	@Test
	void asAUser_IWantToAccessAddOwnerPage_SoThatICanAddNewOwners() {
		// Given I want to add a new owner
		String url = "http://localhost:" + port + "/owners/new";

		// When I request the add owner page
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Then I should receive a successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And the page should contain the owner form
		assertThat(response.getBody()).contains("Owner");
		assertThat(response.getBody()).contains("First Name");
		assertThat(response.getBody()).contains("Last Name");
		assertThat(response.getBody()).contains("Address");
		assertThat(response.getBody()).contains("City");
		assertThat(response.getBody()).contains("Telephone");
		assertThat(response.getBody()).contains("Add Owner");
	}

}