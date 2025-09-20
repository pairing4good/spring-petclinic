package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Comprehensive User Acceptance Tests for Owner Management
 *
 * Scenario: As a pet clinic staff member I want to find, view, and manage pet owners So
 * that I can maintain accurate customer records
 */
@DisplayName("Owner Management User Acceptance Tests")
public class OwnerManagementTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As staff, I want to search for owners by last name so that I can find existing customers")
	public void userCanSearchForOwnersByLastName() {
		// Given I am on the find owners page
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();

		// When I enter a last name and search
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see search results
		// If single result, should go to owner details
		// If multiple results, should show owners list
		assertThat(page.locator("h2")).hasText("Owner Information");
	}

	@Test
	@DisplayName("As staff, I want to search for all owners so that I can browse the customer database")
	public void userCanSearchForAllOwners() {
		// Given I am on the find owners page
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();

		// When I search without entering a last name (empty search)
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see a list of all owners
		assertThat(page.locator("table")).isVisible();
		assertThat(page.locator("h2")).containsText("Owners");

		// And I should see owner names in the table
		assertThat(page.locator("table tbody tr").first()).isVisible();
	}

	@Test
	@DisplayName("As staff, I want to view owner details so that I can see their pets and contact information")
	public void userCanViewOwnerDetails() {
		// Given I search for a specific owner
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// When I view the owner details
		// Then I should see owner information
		assertThat(page.locator("h2")).containsText("Owner Information");

		// And I should see contact details
		assertThat(page.locator("table")).isVisible();

		// And I should see the owner's name
		assertThat(page.locator("table")).containsText("Franklin");

		// And I should see an "Edit Owner" button
		assertThat(page.locator("a")).containsText("Edit Owner");

		// And I should see an "Add New Pet" button
		assertThat(page.locator("a")).containsText("Add New Pet");
	}

	@Test
	@DisplayName("As staff, I want to create a new owner so that I can register new customers")
	public void userCanCreateNewOwner() {
		// Given I am searching for owners
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();

		// When I click "Add Owner"
		page.click("a[href='/owners/new']");
		waitForPageReady();

		// Then I should see the new owner form
		assertThat(page).hasURL(getBaseUrl() + "/owners/new");
		assertThat(page.locator("h2")).containsText("Owner");

		// And I should see all required fields
		assertThat(page.locator("input[name='firstName']")).isVisible();
		assertThat(page.locator("input[name='lastName']")).isVisible();
		assertThat(page.locator("input[name='address']")).isVisible();
		assertThat(page.locator("input[name='city']")).isVisible();
		assertThat(page.locator("input[name='telephone']")).isVisible();

		// When I fill in the new owner details
		page.fill("input[name='firstName']", "John");
		page.fill("input[name='lastName']", "TestOwner");
		page.fill("input[name='address']", "123 Test Street");
		page.fill("input[name='city']", "Test City");
		page.fill("input[name='telephone']", "1234567890");

		// And I submit the form
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should be redirected to the owner details page
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("table")).containsText("John");
		assertThat(page.locator("table")).containsText("TestOwner");
	}

	@Test
	@DisplayName("As staff, I want to edit owner information so that I can update customer details")
	public void userCanEditOwnerInformation() {
		// Given I am viewing an owner's details
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();
		page.fill("input[name='lastName']", "Franklin");
		page.click("button[type='submit']");
		waitForPageReady();

		// When I click "Edit Owner"
		page.click("a:has-text('Edit Owner')");
		waitForPageReady();

		// Then I should see the edit owner form
		assertThat(page.locator("h2")).containsText("Owner");

		// And the form should be populated with existing data
		assertThat(page.locator("input[name='firstName']")).hasValue("George");
		assertThat(page.locator("input[name='lastName']")).hasValue("Franklin");

		// When I update the address
		page.fill("input[name='address']", "Updated Address 456");

		// And I submit the form
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see the updated information
		assertThat(page.locator("h2")).containsText("Owner Information");
		assertThat(page.locator("table")).containsText("Updated Address 456");
	}

	@Test
	@DisplayName("As staff, I want to handle invalid owner searches gracefully so that I get helpful feedback")
	public void userHandlesInvalidSearchesGracefully() {
		// Given I am searching for a non-existent owner
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();

		// When I search for an owner that doesn't exist
		page.fill("input[name='lastName']", "NonExistentOwner9999");
		page.click("button[type='submit']");
		waitForPageReady();

		// Then I should see a helpful message
		assertThat(page.locator("h2")).containsText("Owners");
		// The system should handle this gracefully (empty results)
	}

	@RepeatedTest(3)
	@DisplayName("Owner search stability test - search functionality works consistently")
	public void ownerSearchWorksConsistentlyAcrossMultipleRuns() {
		// This test ensures owner search works reliably multiple times
		page.navigate(getBaseUrl() + "/owners/find");
		waitForPageReady();

		page.click("button[type='submit']");
		waitForPageReady();

		assertThat(page.locator("table")).isVisible();
		assertThat(page.locator("h2")).containsText("Owners");

		// Take screenshot for verification
		takeScreenshot("owner-search-stability-test");
	}

}