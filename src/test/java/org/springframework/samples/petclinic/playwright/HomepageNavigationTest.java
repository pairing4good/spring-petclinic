package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Comprehensive User Acceptance Tests for Homepage Navigation
 *
 * Scenario: As a pet clinic visitor I want to navigate the main sections of the website
 * So that I can access all key features of the pet clinic system
 */
@DisplayName("Homepage Navigation User Acceptance Tests")
public class HomepageNavigationTest extends BasePlaywrightTest {

	@Test
	@DisplayName("As a visitor, I want to access the homepage so that I can see the welcome message and navigation options")
	public void userCanAccessHomepageAndSeeWelcomeContent() {
		// Given I am a visitor to the pet clinic website
		page.navigate(getBaseUrl());
		waitForPageReady();

		// When I load the homepage
		// Then I should see the main heading
		assertThat(page.locator("h2")).containsText("Welcome");

		// And I should see the main navigation menu
		assertThat(page.locator(".navbar")).isVisible();

		// And I should see links to key sections
		assertThat(page.locator("a[href='/']")).isVisible(); // Home
		assertThat(page.locator("a[href='/owners/find']")).isVisible(); // Find owners
		assertThat(page.locator("a[href='/vets.html']")).isVisible(); // Veterinarians

		// And I should see the Spring Petclinic branding
		assertThat(page.locator(".navbar-brand")).isVisible();
	}

	@Test
	@DisplayName("As a user, I want to navigate to Find Owners so that I can search for pet owners")
	public void userCanNavigateToFindOwnersPage() {
		// Given I am on the homepage
		page.navigate(getBaseUrl());
		waitForPageReady();

		// When I click on "Find owners" in the navigation
		page.click("a[href='/owners/find']");
		waitForPageReady();

		// Then I should be taken to the find owners page
		assertThat(page).hasURL(getBaseUrl() + "/owners/find");

		// And I should see the find owners form
		assertThat(page.locator("form")).isVisible();
		assertThat(page.locator("input[name='lastName']")).isVisible();

		// And I should see the page heading
		assertThat(page.locator("h2")).containsText("Find Owners");
	}

	@Test
	@DisplayName("As a user, I want to navigate to Veterinarians so that I can view available vets")
	public void userCanNavigateToVeterinariansPage() {
		// Given I am on the homepage
		page.navigate(getBaseUrl());
		waitForPageReady();

		// When I click on "Veterinarians" in the navigation
		page.click("a[href='/vets.html']");
		waitForPageReady();

		// Then I should be taken to the veterinarians page
		assertThat(page).hasURL(getBaseUrl() + "/vets.html");

		// And I should see the veterinarians table
		assertThat(page.locator("table")).isVisible();

		// And I should see the page heading
		assertThat(page.locator("h2")).containsText("Veterinarians");
	}

	@Test
	@DisplayName("As a user, I want to navigate to the Error page so that I can see how errors are handled")
	public void userCanNavigateToErrorPage() {
		// Given I am on the homepage
		page.navigate(getBaseUrl());
		waitForPageReady();

		// When I click on "Error" in the navigation
		page.click("a[href='/oups']");
		waitForPageReady();

		// Then I should be taken to the error page
		assertThat(page).hasURL(getBaseUrl() + "/oups");

		// And I should see an error message
		assertThat(page.locator("h2")).containsText("Something happened...");
	}

	@Test
	@DisplayName("As a user, I want to return to homepage from any page so that I can start over")
	public void userCanReturnToHomepageFromAnyPage() {
		// Given I am on the veterinarians page
		page.navigate(getBaseUrl() + "/vets.html");
		waitForPageReady();

		// When I click on the home link or brand
		page.click("a[href='/']");
		waitForPageReady();

		// Then I should be back on the homepage
		assertThat(page).hasURL(getBaseUrl() + "/");
		assertThat(page.locator("h2")).containsText("Welcome");
	}

	@RepeatedTest(3)
	@DisplayName("Navigation stability test - homepage loads consistently across multiple runs")
	public void homepageLoadsConsistentlyAcrossMultipleRuns() {
		// This test ensures the homepage loads reliably multiple times
		page.navigate(getBaseUrl());
		waitForPageReady();

		assertThat(page.locator("h2")).containsText("Welcome");
		assertThat(page.locator(".navbar")).isVisible();

		// Take screenshot for verification
		takeScreenshot("homepage-stability-test");
	}

}