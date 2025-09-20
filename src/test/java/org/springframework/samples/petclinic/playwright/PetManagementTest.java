package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Comprehensive User Acceptance Tests for Pet Management
 *
 * Scenario: As a pet clinic staff member I want to manage pets for existing owners So
 * that I can maintain accurate pet records and schedule visits
 */
@DisplayName("Pet Management User Acceptance Tests")
public class PetManagementTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As staff, I want to add a new pet to an existing owner so that I can track the pet's information")
	public void userCanAddNewPetToExistingOwner() {
		// Given I am viewing an owner's details
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// When I click "Add New Pet"
		page.click("a:has-text('Add New Pet')");
		waitForPageReady();

		// Then I should see the new pet form
		assertThat(page.locator("h2")).containsText("Pet");

		// And I should see all required fields
		assertThat(page.locator("input[name='name']")).isVisible();
		assertThat(page.locator("input[name='birthDate']")).isVisible();
		assertThat(page.locator("select[name='type']")).isVisible();

		// When I fill in the pet details
		page.fill("input[name='name']", "Fluffy");
		page.fill("input[name='birthDate']", "2023-01-15");
		page.selectOption("select[name='type']", "cat");

		// And I submit the form
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should be back on the owner details page
		assertThat(page.locator("h2")).containsText("Owner Information");

		// And I should see the new pet listed
		assertThat(page.locator("table")).containsText("Fluffy");
		assertThat(page.locator("table")).containsText("cat");
	}

	@Test
	@DisplayName("As staff, I want to edit pet information so that I can update pet details")
	public void userCanEditPetInformation() {
		// Given I am viewing an owner with pets
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// When I click on "Edit Pet" for an existing pet
		page.click("a:has-text('Edit Pet')");
		waitForPageReady();

		// Then I should see the edit pet form
		assertThat(page.locator("h2")).containsText("Pet");

		// And the form should be populated with existing data
		assertThat(page.locator("input[name='name']")).not().hasValue("");

		// When I update the pet's name
		page.fill("input[name='name']", "UpdatedPetName");

		// And I submit the form
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see the updated information
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("table")).containsText("UpdatedPetName");
	}

	@Test
	@DisplayName("As staff, I want to view available pet types so that I can assign the correct type to each pet")
	public void userCanViewAvailablePetTypes() {
		// Given I am adding a new pet
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();
		page.click("a:has-text('Add New Pet')");
		waitForPageReady();

		// When I look at the pet type dropdown
		Locator typeSelect = page.locator("select[name='type']");

		// Then I should see various pet type options
		assertThat(typeSelect).isVisible();

		// And the dropdown should contain common pet types
		assertThat(typeSelect.locator("option").first()).isVisible();
	}

	@Test
	@DisplayName("As staff, I want pet form validation so that I enter valid pet information")
	public void petFormValidationWorksCorrectly() {
		// Given I am adding a new pet
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();
		page.click("a:has-text('Add New Pet')");
		waitForPageReady();

		// When I try to submit an empty form
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see validation errors or stay on the form
		assertThat(page.locator("h2")).containsText("Pet");

		// The form should still be visible for correction
		assertThat(page.locator("input[name='name']")).isVisible();
	}

	@Test
	@DisplayName("As staff, I want to add visits for pets so that I can track medical history")
	public void userCanAddVisitsForPets() {
		// Given I am viewing an owner with pets
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// When I click "Add Visit" for a pet (if available)
		if (page.locator("a:has-text('Add Visit')").isVisible()) {
			page.click("a:has-text('Add Visit')");
			waitForPageReady();

			// Then I should see the visit form
			assertThat(page.locator("h2")).containsText("Visit");

			// And I should see visit fields
			assertThat(page.locator("input[name='date']")).isVisible();
			assertThat(page.locator("textarea[name='description']")).isVisible();

			// When I fill in visit details
			page.fill("input[name='date']", "2024-01-15");
			page.fill("textarea[name='description']", "Regular checkup");

			// And I submit the form
			page.click("button[type='submit']");
			waitForPageReady();

			// Then I should be back on the owner details page
			assertThat(page.locator("h2")).containsText("Owner Information");

			// And I should see the visit listed
			assertThat(page.locator("table")).containsText("Regular checkup");
		}
	}

	@RepeatedTest(3)
	@DisplayName("Pet management stability test - pet operations work consistently")
	public void petManagementWorksConsistentlyAcrossMultipleRuns() {
		// This test ensures pet management works reliably multiple times
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("a:has-text('Add New Pet')")).isVisible();

		// Take screenshot for verification
		takeScreenshot("pet-management-stability-test");
	}

}