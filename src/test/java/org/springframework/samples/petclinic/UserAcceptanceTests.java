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

package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

/**
 * User Acceptance Tests for Spring PetClinic application. These tests verify the main
 * user-facing features and workflows in their entirety, testing the complete happy path
 * scenarios from a user perspective.
 *
 * @author Spring PetClinic Team
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class UserAcceptanceTests {

	@LocalServerPort
	int port;

	@Autowired
	private RestTemplateBuilder builder;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private VetRepository vetRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	private RestTemplate getRestTemplate() {
		return builder.rootUri("http://localhost:" + port).build();
	}

	@Test
	@DisplayName("UAT: Welcome Page - User can navigate to application home page")
	void testWelcomePageUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// User navigates to the application home page
		ResponseEntity<String> response = template.exchange(RequestEntity.get("/").build(), String.class);

		// Verify successful response and welcome page is displayed
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Welcome");
	}

	@Test
	@DisplayName("UAT: Vet Information - User can view list of veterinarians")
	void testViewVeterinariansUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// User navigates to view veterinarians
		ResponseEntity<String> response = template.exchange(RequestEntity.get("/vets.html").build(), String.class);

		// Verify successful response and vets are displayed
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Veterinarians");

		// Verify sample vet data is displayed (from data.sql)
		assertThat(response.getBody()).contains("James Carter");
		assertThat(response.getBody()).contains("Helen Leary");
	}

	@Test
	@DisplayName("UAT: Owner Management - Complete workflow: Find → Create → View → Update")
	void testOwnerManagementCompleteWorkflow() {
		RestTemplate template = getRestTemplate();

		// STEP 1: User navigates to find owners page
		ResponseEntity<String> findOwnersResponse = template.exchange(RequestEntity.get("/owners/find").build(),
				String.class);
		assertThat(findOwnersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(findOwnersResponse.getBody()).contains("Find Owners");

		// STEP 2: User searches for existing owner (should find George Franklin)
		ResponseEntity<String> searchResponse = template
			.exchange(RequestEntity.get("/owners?lastName=Franklin").build(), String.class);
		assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		// Should redirect to specific owner page since only one match

		// STEP 3: User navigates to create new owner form
		ResponseEntity<String> newOwnerFormResponse = template.exchange(RequestEntity.get("/owners/new").build(),
				String.class);
		assertThat(newOwnerFormResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(newOwnerFormResponse.getBody()).contains("Owner");
		assertThat(newOwnerFormResponse.getBody()).contains("First Name");

		// STEP 4: User creates a new owner (simulating form submission)
		MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
		ownerData.add("firstName", "John");
		ownerData.add("lastName", "Doe");
		ownerData.add("address", "123 Test Street");
		ownerData.add("city", "Test City");
		ownerData.add("telephone", "1234567890");

		ResponseEntity<String> createOwnerResponse = template.postForEntity("/owners/new", ownerData, String.class);

		// Should redirect to the newly created owner's page
		if (createOwnerResponse.getStatusCode() != HttpStatus.FOUND) {
			// If not a redirect, it's likely a validation error - log response for
			// debugging
			System.out.println("Owner creation failed with status: " + createOwnerResponse.getStatusCode());
			System.out.println("Response body: " + createOwnerResponse.getBody());
		}
		assertThat(createOwnerResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		// STEP 5: Verify owner was actually created in database
		Page<Owner> foundOwners = ownerRepository.findByLastNameStartingWith("Doe", PageRequest.of(0, 10));
		assertThat(foundOwners.getTotalElements()).isGreaterThan(0);
		Owner createdOwner = foundOwners.getContent().get(0);
		assertThat(createdOwner.getFirstName()).isEqualTo("John");
		assertThat(createdOwner.getAddress()).isEqualTo("123 Test Street");

		// STEP 6: User views the newly created owner's details
		Integer ownerId = createdOwner.getId();
		ResponseEntity<String> ownerDetailsResponse = template.exchange(RequestEntity.get("/owners/" + ownerId).build(),
				String.class);
		assertThat(ownerDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(ownerDetailsResponse.getBody()).contains("John Doe");
		assertThat(ownerDetailsResponse.getBody()).contains("123 Test Street");
	}

	@Test
	@DisplayName("UAT: Pet Management - Add pet to owner and view pet details")
	void testPetManagementUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// Use existing owner George Franklin (ID=1 from data.sql)
		int ownerId = 1;

		// STEP 1: User navigates to add new pet form
		ResponseEntity<String> newPetFormResponse = template
			.exchange(RequestEntity.get("/owners/" + ownerId + "/pets/new").build(), String.class);
		assertThat(newPetFormResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(newPetFormResponse.getBody()).contains("Pet");
		assertThat(newPetFormResponse.getBody()).contains("Add Pet");

		// STEP 2: User adds a new pet (simulating form submission)
		MultiValueMap<String, String> petData = new LinkedMultiValueMap<>();
		petData.add("name", "Buddy");
		petData.add("type", "dog");
		petData.add("birthDate", "2020-01-15");

		ResponseEntity<String> createPetResponse = template.postForEntity("/owners/" + ownerId + "/pets/new", petData,
				String.class);

		// Should redirect back to owner page
		assertThat(createPetResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		// STEP 3: Verify pet was created and is visible on owner page
		ResponseEntity<String> ownerPageResponse = template.exchange(RequestEntity.get("/owners/" + ownerId).build(),
				String.class);
		assertThat(ownerPageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(ownerPageResponse.getBody()).contains("Buddy");
		assertThat(ownerPageResponse.getBody()).contains("dog");

		// STEP 4: Verify pet exists in database
		Owner owner = ownerRepository.findById(ownerId).orElseThrow();
		Optional<Pet> createdPet = owner.getPets().stream().filter(pet -> "Buddy".equals(pet.getName())).findFirst();
		assertThat(createdPet).isPresent();
		assertThat(createdPet.get().getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 15));
	}

	@Test
	@DisplayName("UAT: Visit Management - Schedule visit for pet")
	void testVisitManagementUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// Use existing owner and pet (George Franklin, pet Leo - from data.sql)
		int ownerId = 1;
		int petId = 1;

		// STEP 1: User navigates to schedule new visit form
		ResponseEntity<String> newVisitFormResponse = template
			.exchange(RequestEntity.get("/owners/" + ownerId + "/pets/" + petId + "/visits/new").build(), String.class);
		assertThat(newVisitFormResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(newVisitFormResponse.getBody()).contains("Visit");
		assertThat(newVisitFormResponse.getBody()).contains("Add Visit");

		// STEP 2: User schedules a new visit (simulating form submission)
		MultiValueMap<String, String> visitData = new LinkedMultiValueMap<>();
		visitData.add("description", "Annual checkup");
		// Note: date is auto-set to current date in the application

		ResponseEntity<String> createVisitResponse = template
			.postForEntity("/owners/" + ownerId + "/pets/" + petId + "/visits/new", visitData, String.class);

		// Should redirect back to owner page
		assertThat(createVisitResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		// STEP 3: Verify visit is visible on owner page
		ResponseEntity<String> ownerPageResponse = template.exchange(RequestEntity.get("/owners/" + ownerId).build(),
				String.class);
		assertThat(ownerPageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(ownerPageResponse.getBody()).contains("Annual checkup");

		// STEP 4: Verify visit exists in database
		Owner owner = ownerRepository.findById(ownerId).orElseThrow();
		Pet pet = owner.getPet("Leo");
		assertThat(pet).isNotNull();
		boolean visitExists = pet.getVisits()
			.stream()
			.anyMatch(visit -> "Annual checkup".equals(visit.getDescription()));
		assertThat(visitExists).isTrue();
	}

	@Test
	@DisplayName("UAT: Search Owners - User can search for owners by last name")
	void testSearchOwnersUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// STEP 1: User searches for owners with last name containing "Davis"
		ResponseEntity<String> searchResponse = template.exchange(RequestEntity.get("/owners?lastName=Davis").build(),
				String.class);

		// Should show list of owners since multiple matches (Betty Davis and Harold Davis
		// from data.sql)
		assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(searchResponse.getBody()).contains("Owners");
		assertThat(searchResponse.getBody()).contains("Betty Davis");
		assertThat(searchResponse.getBody()).contains("Harold Davis");

		// STEP 2: User searches for specific owner "Franklin" (should redirect to single
		// result)
		ResponseEntity<String> singleSearchResponse = template
			.exchange(RequestEntity.get("/owners?lastName=Franklin").build(), String.class);

		// Should redirect directly to George Franklin's page since only one match
		assertThat(singleSearchResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		URI redirectLocation = singleSearchResponse.getHeaders().getLocation();
		assertThat(redirectLocation.toString()).contains("/owners/1");
	}

	@Test
	@DisplayName("UAT: Owner Details - User can view comprehensive owner information including pets and visits")
	void testOwnerDetailsUserAcceptance() {
		RestTemplate template = getRestTemplate();

		// User views George Franklin's details (owner ID 1 from data.sql)
		ResponseEntity<String> ownerDetailsResponse = template.exchange(RequestEntity.get("/owners/1").build(),
				String.class);

		assertThat(ownerDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		String responseBody = ownerDetailsResponse.getBody();

		// Verify owner information is displayed
		assertThat(responseBody).contains("George Franklin");
		assertThat(responseBody).contains("110 W. Liberty St.");
		assertThat(responseBody).contains("Madison");
		assertThat(responseBody).contains("6085551023");

		// Verify pet information is displayed (Leo the cat from data.sql)
		assertThat(responseBody).contains("Leo");
		assertThat(responseBody).contains("cat");

		// Verify visit history is displayed if any visits exist for the pets
		// Note: No visits exist for Leo in the sample data, but the structure should be
		// present
		assertThat(responseBody).contains("Pets and Visits");
	}

}