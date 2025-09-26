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

	public BasePage(String baseUrl) {
		this.baseUrl = baseUrl;
		initializeBrowser();
		createContext();
	}

	private static void initializeBrowser() {
		if (playwright == null) {
			playwright = Playwright.create();
		}
		if (browser == null) {
			// Try different browser strategies in order of preference
			browser = createBrowser();
		}
	}

	private static Browser createBrowser() {
		// Strategy 1: Try system Chrome
		try {
			if (Files.exists(Paths.get("/usr/bin/google-chrome"))) {
				return playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
						.setExecutablePath(Paths.get("/usr/bin/google-chrome")));
			}
		}
		catch (Exception e) {
			// Continue to next strategy
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
			// Continue to next strategy
		}

		// Strategy 3: Try Firefox
		try {
			if (Files.exists(Paths.get("/usr/bin/firefox"))) {
				return playwright.firefox()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
						.setExecutablePath(Paths.get("/usr/bin/firefox")));
			}
		}
		catch (Exception e) {
			// Continue to next strategy
		}

		// Strategy 4: Try default browsers (will trigger download if needed)
		try {
			return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
		}
		catch (Exception e) {
			try {
				return playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
			}
			catch (Exception e2) {
				throw new RuntimeException("No browser could be launched. Please install Chrome, Chromium, or Firefox",
						e2);
			}
		}
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