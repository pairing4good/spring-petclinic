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
package org.springframework.samples.petclinic.e2e.pages;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Base page class for all Page Object Model classes. Provides common functionality for
 * browser management, navigation, and element interactions.
 */
public abstract class BasePage {

	protected Page page;

	protected String baseUrl;

	private static Playwright playwright;

	private static Browser browser;

	private BrowserContext context;

	/**
	 * Custom exception to indicate browser is not available for testing
	 */
	public static class BrowserNotAvailableException extends RuntimeException {

		public BrowserNotAvailableException(String message) {
			super(message);
		}

	}

	public BasePage(String baseUrl) {
		this.baseUrl = baseUrl;
		initializeBrowser();
		createContext();
	}

	private static void initializeBrowser() {
		if (playwright == null) {
			// Skip browser download by using environment variable
			System.setProperty("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
			playwright = Playwright.create();
		}
		if (browser == null) {
			// Try different browser strategies in order of preference
			browser = createBrowser();
		}
	}

	private static Browser createBrowser() {
		// Try system browsers first to avoid download issues

		// Strategy 1: Try system Chrome
		try {
			if (Files.exists(Paths.get("/usr/bin/google-chrome"))) {
				return playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
						.setExecutablePath(Paths.get("/usr/bin/google-chrome")));
			}
		}
		catch (Exception e) {
			System.err.println("Failed to launch Chrome: " + e.getMessage());
		}

		// Strategy 2: Try system Chromium
		try {
			if (Files.exists(Paths.get("/usr/bin/chromium"))) {
				return playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
						.setExecutablePath(Paths.get("/usr/bin/chromium")));
			}
		}
		catch (Exception e) {
			System.err.println("Failed to launch Chromium: " + e.getMessage());
		}

		// Strategy 3: Try system Firefox
		try {
			if (Files.exists(Paths.get("/usr/bin/firefox"))) {
				return playwright.firefox()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
						.setExecutablePath(Paths.get("/usr/bin/firefox")));
			}
		}
		catch (Exception e) {
			System.err.println("Failed to launch Firefox: " + e.getMessage());
		}

		// Strategy 4: Skip browser download and throw descriptive error
		// This follows requirement to skip tests that can't be fixed after attempts
		throw new BrowserNotAvailableException(
				"No suitable browser found. In CI environments, install browsers using 'playwright install'. "
						+ "For local development, install Chrome, Chromium, or Firefox. "
						+ "Tests will be skipped when browsers are not available.");
	}

	private void createContext() {
		context = browser.newContext();
		page = context.newPage();
	}

	/**
	 * Navigate to a specific path relative to the base URL
	 */
	public void navigateTo(String path) {
		String url = path.startsWith("http") ? path : baseUrl + path;
		page.navigate(url);
	}

	/**
	 * Wait for an element to be visible using data-testid
	 */
	public void waitForTestId(String testId) {
		page.waitForSelector("[data-testid='" + testId + "']",
				new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
	}

	/**
	 * Wait for an element to be visible using CSS selector
	 */
	public void waitForElement(String selector) {
		page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
	}

	/**
	 * Get the page title
	 */
	public String getTitle() {
		return page.title();
	}

	/**
	 * Check if we're on the expected page by title
	 */
	public boolean isOnPage(String expectedTitle) {
		return getTitle().contains(expectedTitle);
	}

	/**
	 * Navigate to home page
	 */
	public HomePage goHome() {
		page.locator("a.navbar-brand").click();
		return new HomePage(baseUrl);
	}

	/**
	 * Navigate to Find Owners page using main navigation
	 */
	public FindOwnersPage goToFindOwners() {
		page.locator("nav a[href='/owners/find']").click();
		return new FindOwnersPage(baseUrl);
	}

	/**
	 * Navigate to Veterinarians page using main navigation
	 */
	public VeterinariansPage goToVeterinarians() {
		page.locator("nav a[href='/vets.html']").click();
		return new VeterinariansPage(baseUrl);
	}

	/**
	 * Navigate to Error page using main navigation
	 */
	public ErrorPage goToError() {
		page.locator("nav a[href='/oups']").click();
		return new ErrorPage(baseUrl);
	}

	/**
	 * Close the current page context
	 */
	public void close() {
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Close all browser resources - call this in test cleanup
	 */
	public static void closeAll() {
		if (browser != null) {
			browser.close();
			browser = null;
		}
		if (playwright != null) {
			playwright.close();
			playwright = null;
		}
	}

	/**
	 * Get the underlying Playwright Page object for direct access when needed
	 */
	public Page getPage() {
		return page;
	}

}