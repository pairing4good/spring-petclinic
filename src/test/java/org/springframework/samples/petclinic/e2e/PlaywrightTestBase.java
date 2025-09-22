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

/**
 * Base class for Playwright E2E tests providing common setup and teardown.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PlaywrightTestBase {

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@LocalServerPort
	protected int serverPort;

	protected String baseUrl;

	@BeforeAll
	static void setUpPlaywright() {
		playwright = Playwright.create();
		// Use Chromium for default tests, specific browser tests will override
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50)); // Small
																													// delay
																													// to
																													// ensure
																													// stability
	}

	@AfterAll
	static void tearDownPlaywright() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void setUp() {
		baseUrl = "http://localhost:" + serverPort;
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080)); // Full
																									// HD
																									// viewport
		page = context.newPage();

		// Add console error logging for debugging
		page.onConsoleMessage(msg -> {
			if (msg.type().equals("error")) {
				System.err.println("Console error: " + msg.text());
			}
		});
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
	 * Navigate to the application home page
	 */
	protected void goToHomePage() {
		page.navigate(baseUrl);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Navigate to a specific path within the application
	 */
	protected void navigateTo(String path) {
		page.navigate(baseUrl + path);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Wait for page to be fully loaded
	 */
	protected void waitForPageLoad() {
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Take a screenshot for debugging purposes
	 */
	protected void takeScreenshot(String name) {
		page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("screenshots/" + name + ".png")));
	}

}