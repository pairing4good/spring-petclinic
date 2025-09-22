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
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Base class for Playwright end-to-end tests providing common setup and utilities.
 *
 * @author Copilot Agent
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseE2ETest {

	@LocalServerPort
	protected int port;

	protected String baseUrl;

	protected Playwright playwright;

	protected Browser browser;

	protected BrowserContext context;

	protected Page page;

	@BeforeAll
	void setupPlaywright() {
		playwright = Playwright.create();

		// Configure browser based on environment
		String browserName = System.getProperty("browser", "chromium");
		boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

		BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless)
			.setTimeout(30000);

		switch (browserName.toLowerCase()) {
			case "firefox":
				browser = playwright.firefox().launch(launchOptions);
				break;
			case "webkit":
			case "safari":
				browser = playwright.webkit().launch(launchOptions);
				break;
			default:
				browser = playwright.chromium().launch(launchOptions);
		}
	}

	@BeforeEach
	void setupTest() {
		baseUrl = "http://localhost:" + port;

		// Create new browser context for each test to ensure isolation
		Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setViewportSize(1280, 720);

		context = browser.newContext(contextOptions);

		// Configure timeouts
		context.setDefaultTimeout(30000);
		context.setDefaultNavigationTimeout(30000);

		page = context.newPage();

		// Setup console error tracking
		page.onConsoleMessage(msg -> {
			if (msg.type().equals("error")) {
				System.err.println("Console error: " + msg.text());
			}
		});
	}

	@AfterEach
	void teardownTest() {
		if (context != null) {
			context.close();
		}
	}

	@AfterAll
	void teardownPlaywright() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	// Utility methods for common operations
	protected void navigateToHome() {
		page.navigate(baseUrl);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	protected void navigateToFindOwners() {
		page.navigate(baseUrl + "/owners/find");
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	protected void navigateToVeterinarians() {
		page.navigate(baseUrl + "/vets.html");
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	protected void navigateToAddOwner() {
		page.navigate(baseUrl + "/owners/new");
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	protected void waitForPageLoad() {
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	protected void assertPageTitle(String expectedTitle) {
		Assertions.assertEquals(expectedTitle, page.title());
	}

	protected void assertUrlContains(String expectedUrlPart) {
		Assertions.assertTrue(page.url().contains(expectedUrlPart));
	}

	protected void assertElementVisible(String selector) {
		Assertions.assertTrue(page.locator(selector).isVisible());
	}

	protected void assertElementHasText(String selector, String expectedText) {
		Assertions.assertEquals(expectedText, page.locator(selector).textContent());
	}

	protected void assertElementContainsText(String selector, String expectedText) {
		Assertions.assertTrue(page.locator(selector).textContent().contains(expectedText));
	}

}