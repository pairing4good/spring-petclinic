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

package org.springframework.samples.petclinic.e2e.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for all Playwright E2E tests Handles browser setup, configuration, and
 * common test utilities
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "server.error.include-message=ALWAYS" })
public abstract class BaseE2ETest {

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@LocalServerPort
	protected int port;

	protected String baseUrl;

	@BeforeAll
	static void launchBrowser() {
		// Set up Playwright browser
		playwright = Playwright.create();

		// Configure browser with appropriate options
		BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(true) // Run
																									// headless
																									// in
																									// CI,
																									// can
																									// be
																									// overridden
																									// for
																									// local
																									// development
			.setSlowMo(0); // No slow motion for faster test execution

		browser = playwright.chromium().launch(launchOptions);
	}

	@AfterAll
	static void closeBrowser() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void createContextAndPage() {
		// Set up base URL using the random port from Spring Boot test
		baseUrl = "http://localhost:" + port;

		// Create a new browser context for each test (isolation)
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720).setLocale("en-US"));

		// Enable request/response logging for debugging
		context.onRequest(request -> System.out.println(">> " + request.method() + " " + request.url()));
		context.onResponse(response -> System.out.println("<< " + response.status() + " " + response.url()));

		// Create a new page
		page = context.newPage();

		// Set default timeouts
		page.setDefaultTimeout(30000); // 30 seconds for actions
		page.setDefaultNavigationTimeout(30000); // 30 seconds for navigation
	}

	@AfterEach
	void closeContext() {
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Navigate to the application home page
	 */
	protected void navigateToHomePage() {
		page.navigate(baseUrl);
		page.waitForLoadState();
	}

	/**
	 * Take a screenshot for debugging failed tests
	 */
	protected void takeScreenshot(String name) {
		page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("target/" + name + ".png")));
	}

	/**
	 * Wait for the application to be ready
	 */
	protected void waitForApplicationReady() {
		// Wait for the Spring PetClinic logo/navigation to be visible
		page.waitForSelector("nav", new Page.WaitForSelectorOptions().setTimeout(10000));
	}

	/**
	 * Utility method to generate unique test data
	 */
	protected String generateUniqueId() {
		return "test_" + System.currentTimeMillis();
	}

	/**
	 * Utility method to generate test owner data
	 */
	protected TestOwnerData generateTestOwnerData() {
		String uniqueId = generateUniqueId();
		return new TestOwnerData("Test" + uniqueId, "Owner" + uniqueId, "123 Test Street", "Test City",
				"555" + uniqueId.substring(uniqueId.length() - 7));
	}

	/**
	 * Test data class for owner information
	 */
	public static class TestOwnerData {

		public final String firstName;

		public final String lastName;

		public final String address;

		public final String city;

		public final String telephone;

		public TestOwnerData(String firstName, String lastName, String address, String city, String telephone) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
			this.city = city;
			this.telephone = telephone;
		}

	}

}