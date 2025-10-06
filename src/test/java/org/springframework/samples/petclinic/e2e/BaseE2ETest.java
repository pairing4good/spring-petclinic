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
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for all Playwright E2E tests providing common setup and utilities.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
		properties = { "spring.datasource.url=jdbc:h2:mem:testdb-e2e", "spring.jpa.hibernate.ddl-auto=create-drop" })
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
			// Use Chromium for consistent testing, can be configured for multiple
			// browsers
			browser = playwright.chromium()
				.launch(new BrowserType.LaunchOptions().setHeadless(true) // Set to false
																			// for
																			// debugging
					.setSlowMo(50)); // Small delay for stability
		}
		catch (Exception e) {
			// Skip tests if Playwright browsers are not available
			org.junit.jupiter.api.Assumptions.assumeTrue(false, "Playwright browsers not available: " + e.getMessage());
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
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720) // Standard
																								// desktop
																								// size
			.setIgnoreHTTPSErrors(true));

		page = context.newPage();

		// Set default timeouts
		page.setDefaultTimeout(10000); // 10 seconds
		page.setDefaultNavigationTimeout(30000); // 30 seconds for navigation
	}

	@AfterEach
	void closeContext() {
		if (context != null) {
			context.close();
		}
	}

	// Helper methods for common actions

	/**
	 * Navigate to home page and wait for it to load
	 */
	protected void navigateToHomePage() {
		page.navigate(baseUrl());
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
		// Wait for the welcome heading to ensure page is fully loaded
		page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome")).waitFor();
	}

	/**
	 * Navigate to a specific URL and wait for it to load
	 */
	protected void navigateToUrl(String path) {
		page.navigate(baseUrl() + path);
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
	}

	/**
	 * Click navigation link and wait for page to load
	 */
	protected void clickNavigationLink(String linkText) {
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(linkText)).click();
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
	}

	/**
	 * Fill form field by label text
	 */
	protected void fillFormField(String labelText, String value) {
		page.getByLabel(labelText).fill(value);
	}

	/**
	 * Click button by text
	 */
	protected void clickButton(String buttonText) {
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonText)).click();
	}

	/**
	 * Wait for heading with specific text to appear
	 */
	protected void waitForHeading(String headingText) {
		page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(headingText)).waitFor();
	}

	/**
	 * Check if text is visible on the page
	 */
	protected boolean isTextVisible(String text) {
		return page.getByText(text).isVisible();
	}

	/**
	 * Wait for text to be visible
	 */
	protected void waitForText(String text) {
		page.getByText(text).first().waitFor();
	}

}