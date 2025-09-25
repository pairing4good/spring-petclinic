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
package org.springframework.samples.petclinic.e2e.base;

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

/**
 * Base class for all E2E tests providing Playwright configuration and common setup.
 * 
 * This class provides:
 * - Playwright instance management
 * - Browser context setup with proper configurations
 * - Page isolation for each test
 * - Reusable methods for common actions
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseE2ETest {

	@LocalServerPort
	protected int port;

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@BeforeAll
	static void beforeAll() {
		playwright = Playwright.create();
		// Use Chromium with headless mode for CI/CD compatibility
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
	}

	@AfterAll
	static void afterAll() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void setUp() {
		// Create new context for each test to ensure isolation
		context = browser.newContext(new Browser.NewContextOptions()
			.setViewportSize(1280, 720) // Standard desktop viewport
			.setLocale("en-US"));

		page = context.newPage();

		// Set default timeout for better test stability
		page.setDefaultTimeout(30000); // 30 seconds
	}

	@AfterEach
	void tearDown() {
		if (page != null) {
			page.close();
		}
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Get the base URL for the application.
	 */
	protected String getBaseUrl() {
		return "http://localhost:" + port;
	}

	/**
	 * Navigate to a specific path relative to the base URL.
	 */
	protected void navigateToPath(String path) {
		page.navigate(getBaseUrl() + path);
	}

	/**
	 * Navigate to the home page.
	 */
	protected void navigateToHomePage() {
		navigateToPath("/");
	}

	/**
	 * Wait for page load state to be complete.
	 */
	protected void waitForPageLoad() {
		page.waitForLoadState();
	}

	/**
	 * Check if an element is visible with explicit wait.
	 */
	protected boolean isElementVisible(String selector) {
		try {
			page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
			return page.locator(selector).isVisible();
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Fill form field and wait for it to be ready.
	 */
	protected void fillField(String selector, String value) {
		page.locator(selector).waitFor();
		page.locator(selector).fill(value);
	}

	/**
	 * Click element and wait for navigation if expected.
	 */
	protected void clickAndWait(String selector) {
		page.locator(selector).click();
		waitForPageLoad();
	}

	/**
	 * Get text content from element with explicit wait.
	 */
	protected String getTextContent(String selector) {
		page.locator(selector).waitFor();
		return page.locator(selector).textContent();
	}

}