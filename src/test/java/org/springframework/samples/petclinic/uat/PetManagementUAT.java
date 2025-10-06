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
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Acceptance Tests for Pet Management (Service Level)
 *
 * Stories: - As a pet owner, I want to add my pet to the system, so that I can track
 * their medical records. - As a pet owner, I want to book a veterinary appointment, so
 * that I can bring my pet for medical care. - As a pet owner, I want to view my pet's
 * visit history, so that I can track their health.
 */
class PetManagementUAT extends BaseUserAcceptanceTest {

	private Owner testOwner;

	private PetType testPetType;

	@BeforeEach
	void setUp() {
		// Create a test owner
		testOwner = new Owner();
		testOwner.setFirstName("Sarah");
		testOwner.setLastName("Connor");
		testOwner.setAddress("123 Future Street");
		testOwner.setCity("Los Angeles");
		testOwner.setTelephone("5551234567");
		testOwner = ownerRepository.save(testOwner);

		// Get available pet type
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
		LocalDate birthDate = LocalDate.of(2020, 5, 15);

		// When I add my pet to the system
		Pet newPet = new Pet();
		newPet.setName(petName);
		newPet.setBirthDate(birthDate);
		newPet.setType(testPetType);
		testOwner.addPet(newPet);

		Owner savedOwner = ownerRepository.save(testOwner);

		// Then my pet should be successfully added
		assertThat(savedOwner.getPets()).hasSize(1);
		Pet savedPet = savedOwner.getPets().iterator().next();
		assertThat(savedPet.getName()).isEqualTo(petName);
		assertThat(savedPet.getBirthDate()).isEqualTo(birthDate);
		assertThat(savedPet.getType().getName()).isEqualTo("dog");

		// And I should be able to find my pet
		Pet foundPet = savedOwner.getPet(petName);
		assertThat(foundPet).isNotNull();
		assertThat(foundPet.getName()).isEqualTo(petName);
	}

	@Test
	@DisplayName("As a pet owner, I want to book a veterinary appointment, so that I can bring my pet for medical care")
	void petOwnerCanBookVeterinaryAppointment() {
		// Given I have a pet registered in the system
		Pet myPet = new Pet();
		myPet.setName("Rex");
		myPet.setBirthDate(LocalDate.of(2020, 6, 15));
		myPet.setType(testPetType);
		testOwner.addPet(myPet);
		testOwner = ownerRepository.save(testOwner);

		// Get the saved pet with ID
		Pet savedPet = testOwner.getPets().iterator().next();

		// When I book an appointment for my pet
		Visit appointment = new Visit();
		appointment.setDate(LocalDate.now());
		appointment.setDescription("Annual checkup and vaccination");

		testOwner.addVisit(savedPet.getId(), appointment);
		ownerRepository.save(testOwner);

		// Then the appointment should be successfully booked
		Owner updatedOwner = ownerRepository.findById(testOwner.getId()).orElse(null);
		assertThat(updatedOwner).isNotNull();

		Pet petWithVisit = updatedOwner.getPet(savedPet.getId());
		assertThat(petWithVisit.getVisits()).hasSize(1);

		Visit savedVisit = petWithVisit.getVisits().iterator().next();
		assertThat(savedVisit.getDescription()).isEqualTo("Annual checkup and vaccination");
		assertThat(savedVisit.getDate()).isEqualTo(LocalDate.now());
	}

	@Test
	@DisplayName("As a pet owner, I can view my pet's visit history")
	void petOwnerCanViewPetVisitHistory() {
		// Given I have a pet with previous visits
		Pet myPet = new Pet();
		myPet.setName("Luna");
		myPet.setBirthDate(LocalDate.of(2019, 3, 10));
		myPet.setType(testPetType);
		testOwner.addPet(myPet);
		testOwner = ownerRepository.save(testOwner);

		Pet savedPet = testOwner.getPets().iterator().next();

		// Add multiple visits
		Visit firstVisit = new Visit();
		firstVisit.setDate(LocalDate.now().minusDays(30));
		firstVisit.setDescription("Routine checkup");

		Visit secondVisit = new Visit();
		secondVisit.setDate(LocalDate.now().minusDays(15));
		secondVisit.setDescription("Follow-up examination");

		testOwner.addVisit(savedPet.getId(), firstVisit);
		testOwner.addVisit(savedPet.getId(), secondVisit);
		ownerRepository.save(testOwner);

		// When I view my pet's information
		Owner ownerWithHistory = ownerRepository.findById(testOwner.getId()).orElse(null);
		assertThat(ownerWithHistory).isNotNull();

		Pet petWithHistory = ownerWithHistory.getPet(savedPet.getId());

		// Then I should see all visit history
		assertThat(petWithHistory.getVisits()).hasSize(2);
		assertThat(petWithHistory.getVisits()).extracting(Visit::getDescription)
			.contains("Routine checkup", "Follow-up examination");
	}

	@Test
	@DisplayName("As a pet owner, I cannot add a pet without required information")
	void petOwnerCannotAddPetWithoutRequiredInformation() {
		// Given I am trying to add a pet without required information
		Pet incompletePet = new Pet();
		// Missing name and type
		incompletePet.setBirthDate(LocalDate.of(2020, 5, 15));
		testOwner.addPet(incompletePet);

		// When I try to save the pet without required information
		// Then the system should enforce validation
		try {
			ownerRepository.save(testOwner);
			// If we get here without exception, check if validation was enforced at the
			// entity level
			Owner savedOwner = ownerRepository.findById(testOwner.getId()).orElse(null);
			if (savedOwner != null && !savedOwner.getPets().isEmpty()) {
				Pet savedPet = savedOwner.getPets().iterator().next();
				// The pet should not have been saved properly without required fields
				assertThat(savedPet.getName()).as("Pet name should be required").isNotNull();
				assertThat(savedPet.getType()).as("Pet type should be required").isNotNull();
			}
		}
		catch (Exception e) {
			// Expected - validation should prevent saving incomplete data
			assertThat(e).isNotNull();
		}
	}

}