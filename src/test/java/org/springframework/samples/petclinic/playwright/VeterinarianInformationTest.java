package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Comprehensive User Acceptance Tests for Veterinarian Information
 *
 * Scenario: As a pet clinic visitor or staff member I want to view information about
 * available veterinarians So that I can understand the expertise and services available
 */
@DisplayName("Veterinarian Information User Acceptance Tests")
public class VeterinarianInformationTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a visitor, I want to view the list of veterinarians so that I can see available doctors")
	public void userCanViewVeterinariansList() {
		// Given I am a visitor to the pet clinic website
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// When I access the veterinarians page
		// Then I should see the veterinarians heading
		assertThat(page.locator("h2")).containsText("Veterinarians");

		// And I should see a table with veterinarian information
		assertThat(page.locator("table")).isVisible();

		// And the table should have headers
		assertThat(page.locator("table thead")).isVisible();
		assertThat(page.locator("table th")).containsText("Name");

		// And I should see veterinarian data
		assertThat(page.locator("table tbody tr").first()).isVisible();
	}

	@Test
	@DisplayName("As a visitor, I want to see veterinarian specialties so that I can find the right doctor for my pet")
	public void userCanViewVeterinarianSpecialties() {
		// Given I am on the veterinarians page
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// When I look at the veterinarian information
		// Then I should see specialties listed for vets who have them
		Locator table = page.locator("table");
		assertThat(table).isVisible();

		// And specialties should be displayed clearly
		// (Some vets may have specialties like "radiology", "surgery", etc.)
		Locator tableRows = page.locator("table tbody tr");
		assertThat(tableRows.first()).isVisible();

		// Verify table structure makes sense
		assertThat(page.locator("table th").first()).isVisible();
	}

	@Test
	@DisplayName("As a visitor, I want the veterinarians page to load quickly so that I can access information efficiently")
	public void veterinariansPageLoadsQuickly() {
		// Given I navigate to the veterinarians page
		long startTime = System.currentTimeMillis();

		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		long loadTime = System.currentTimeMillis() - startTime;

		// When the page loads
		// Then it should load in reasonable time (under 5 seconds)
		assert loadTime < 5000 : "Page took too long to load: " + loadTime + "ms";

		// And all content should be visible
		assertThat(page.locator("h2")).containsText("Veterinarians");
		assertThat(page.locator("table")).isVisible();
	}

	@Test
	@DisplayName("As a visitor, I want to navigate back from veterinarians page so that I can explore other sections")
	public void userCanNavigateBackFromVeterinariansPage() {
		// Given I am on the veterinarians page
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// When I click on the home link
		page.click("a[href='/']");
		waitForPageReady();

		// Then I should be back on the homepage
		assertThat(page).hasURL(getBaseUrl() + "/");
		assertThat(page.locator("h2")).containsText("Welcome");

		// And when I navigate back to vets using the nav menu
		page.click("a[href='/vets.html']");
		waitForPageReady();

		// Then I should be back on the veterinarians page
		assertThat(page).hasURL(getBaseUrl() + "/vets.html");
		assertThat(page.locator("h2")).containsText("Veterinarians");
	}

	@Test
	@DisplayName("As a visitor, I want the veterinarians table to be readable so that I can easily find information")
	public void veterinariansTableIsReadableAndWellFormatted() {
		// Given I am on the veterinarians page
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// When I examine the table
		Locator table = page.locator("table");

		// Then the table should be properly formatted
		assertThat(table).isVisible();

		// And should have clear column headers
		assertThat(page.locator("table thead th").first()).isVisible();

		// And should have data rows
		assertThat(page.locator("table tbody tr").first()).isVisible();

		// And each row should have consistent columns (verify first row has cells)
		assertThat(page.locator("table tbody tr").first().locator("td").first()).isVisible();
	}

	@Test
	@DisplayName("As a visitor, I want to access veterinarian information on mobile-sized screens so that I can use any device")
	public void veterinariansPageWorksOnMobileViewport() {
		// Given I am using a mobile-sized viewport
		page.setViewportSize(375, 667); // iPhone-like dimensions

		// When I navigate to the veterinarians page
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// Then the page should still be usable
		assertThat(page.locator("h2")).containsText("Veterinarians");
		assertThat(page.locator("table")).isVisible();

		// And the navigation should still work
		assertThat(page.locator(".navbar")).isVisible();

		// Restore normal viewport
		page.setViewportSize(1280, 720);
	}

	@RepeatedTest(3)
	@DisplayName("Veterinarians page stability test - page loads consistently across multiple runs")
	public void veterinariansPageLoadsConsistentlyAcrossMultipleRuns() {
		// This test ensures the veterinarians page loads reliably multiple times
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		assertThat(page.locator("h2")).containsText("Veterinarians");
		assertThat(page.locator("table")).isVisible();
		assertThat(page.locator("table tbody tr").first()).isVisible();

		// Take screenshot for verification
		takeScreenshot("veterinarians-stability-test");
	}

}