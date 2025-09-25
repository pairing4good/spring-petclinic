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

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all Playwright E2E tests. Provides common setup and teardown for browser
 * management and Spring Boot test context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
public abstract class BaseE2ETest {

	@LocalServerPort
	protected int port;

	protected String baseUrl;

	// Playwright instances - shared across all tests in a class
	private static Playwright playwright;

	private static Browser browser;

	// Per-test instances for isolation
	protected BrowserContext context;

	protected Page page;

	@BeforeAll
	static void launchBrowser() {
		try {
			playwright = Playwright.create();
			// Launch browser in headless mode for CI, can be overridden for local
			// development
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50)); // Small
																														// delay
																														// for
																														// more
																														// stable
																														// tests
		}
		catch (Exception e) {
			// Try to install browsers if they're not available
			try {
				com.microsoft.playwright.CLI.main(new String[] { "install", "chromium" });
				playwright = Playwright.create();
				browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50));
			}
			catch (Exception installException) {
				throw new RuntimeException("Failed to launch browser after installation attempt", installException);
			}
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
		baseUrl = "http://localhost:" + port;

		// Create new context for test isolation
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720)); // Set
																									// consistent
																									// viewport
																									// size

		// Create new page
		page = context.newPage();

		// Set reasonable timeouts
		page.setDefaultTimeout(10000); // 10 second default timeout
		page.setDefaultNavigationTimeout(30000); // 30 second navigation timeout
	}

	@AfterEach
	void closeContextAndPage() {
		if (page != null) {
			page.close();
		}
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Helper method to wait for page load
	 */
	protected void waitForPageLoad() {
		page.waitForLoadState();
	}

	/**
	 * Helper method to navigate to a path relative to base URL
	 */
	protected void navigateTo(String path) {
		page.navigate(baseUrl + path);
		waitForPageLoad();
	}

}