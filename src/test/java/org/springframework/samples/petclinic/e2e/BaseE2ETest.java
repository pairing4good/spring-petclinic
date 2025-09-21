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

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for end-to-end tests using Playwright. Provides common setup and teardown
 * for browser automation tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb", "spring.h2.console.enabled=false" })
public abstract class BaseE2ETest {

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@LocalServerPort
	protected int port;

	protected String baseUrl() {
		return "http://localhost:" + port;
	}

	@BeforeAll
	static void launchBrowser() {
		try {
			playwright = Playwright.create();

			// Configure browser options
			BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50); // Slight
																														// delay
																														// to
																														// make
																														// tests
																														// more
																														// stable

			browser = playwright.chromium().launch(launchOptions);
		}
		catch (Exception e) {
			System.err.println("Failed to initialize Playwright browsers. Error: " + e.getMessage());
			System.err.println("Skipping E2E tests due to browser installation issues.");
			// Set browser to null so tests will be skipped
			browser = null;
		}
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
		// Skip test if browser initialization failed
		if (browser == null) {
			org.junit.jupiter.api.Assumptions.assumeTrue(false, "Playwright browser not available, skipping E2E test");
			return;
		}

		// Create new context for test isolation
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720).setLocale("en-US"));

		// Configure timeouts and retry behavior
		context.setDefaultTimeout(30000);
		context.setDefaultNavigationTimeout(30000);

		page = context.newPage();

		// Navigate to home page before each test
		page.navigate(baseUrl());
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	@AfterEach
	void closeContext() {
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Helper method to wait for navigation and ensure page is fully loaded
	 */
	protected void navigateAndWait(String url) {
		page.navigate(baseUrl() + url);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Helper method to click and wait for navigation
	 */
	protected void clickAndWait(String selector) {
		page.click(selector);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Helper method to fill form field and verify it was filled
	 */
	protected void fillField(String selector, String value) {
		page.fill(selector, value);
		// Verify the field was filled correctly
		String actualValue = page.inputValue(selector);
		if (!value.equals(actualValue)) {
			throw new AssertionError(
					"Expected field " + selector + " to have value '" + value + "' but was '" + actualValue + "'");
		}
	}

}