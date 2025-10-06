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

package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for Playwright user acceptance tests. Provides common setup and
 * configuration for browser-based testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("playwright")
public abstract class PlaywrightTestBase {

	@LocalServerPort
	protected int port;

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	protected String baseUrl;

	@BeforeAll
	static void launchBrowser() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(100)); // Small
																													// delay
																													// for
																													// stability
	}

	@AfterAll
	static void closeBrowser() {
		browser.close();
		playwright.close();
	}

	@BeforeEach
	void createContextAndPage() {
		baseUrl = "http://localhost:" + port;
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720));
		page = context.newPage();

		// Set default timeout for page operations
		page.setDefaultTimeout(10000);
		page.setDefaultNavigationTimeout(30000);
	}

	@AfterEach
	void closeContext() {
		context.close();
	}

	/**
	 * Navigate to the application home page
	 */
	protected void navigateToHomePage() {
		page.navigate(baseUrl);
		page.waitForLoadState();
	}

	/**
	 * Wait for element to be visible with timeout
	 */
	protected void waitForElement(String selector) {
		page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
	}

	/**
	 * Take a screenshot for debugging purposes
	 */
	protected void takeScreenshot(String name) {
		page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("screenshots/" + name + ".png")));
	}

}