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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for Playwright End-to-End tests providing common setup and utilities.
 *
 * @author Spring PetClinic Team
 */
public abstract class BasePlaywrightE2ETest {

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	protected static String baseUrl;

	protected static Properties playwrightConfig;

	@BeforeAll
	static void setUp() {
		// Load configuration
		playwrightConfig = loadConfiguration();
		baseUrl = playwrightConfig.getProperty("playwright.baseurl", "http://localhost:8080");

		// Initialize Playwright
		playwright = Playwright.create();

		// Set up browser based on configuration or system property
		String browserName = System.getProperty("playwright.browser", "chromium");
		boolean headless = Boolean.parseBoolean(playwrightConfig.getProperty("playwright.headless", "true"));

		BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);

		switch (browserName.toLowerCase()) {
			case "firefox":
				browser = playwright.firefox().launch(launchOptions);
				break;
			case "webkit":
			case "safari":
				browser = playwright.webkit().launch(launchOptions);
				break;
			case "chromium":
			case "chrome":
			default:
				browser = playwright.chromium().launch(launchOptions);
				break;
		}
	}

	@BeforeEach
	void setUpContext() {
		// Create new context for each test to ensure isolation
		context = browser.newContext();

		// Enable tracing for debugging
		String trace = playwrightConfig.getProperty("playwright.trace", "on-first-retry");
		if ("on".equals(trace) || "on-first-retry".equals(trace)) {
			context.tracing()
				.start(new com.microsoft.playwright.Tracing.StartOptions().setScreenshots(true)
					.setSnapshots(true)
					.setSources(true));
		}

		page = context.newPage();
		page.setViewportSize(1280, 720);

		// Set default timeout
		int timeout = Integer.parseInt(playwrightConfig.getProperty("playwright.timeout", "30000"));
		page.setDefaultTimeout(timeout);
	}

	@AfterEach
	void tearDownContext() {
		if (context != null) {
			// Save trace if enabled
			String trace = playwrightConfig.getProperty("playwright.trace", "on-first-retry");
			if ("on".equals(trace) || "on-first-retry".equals(trace)) {
				context.tracing()
					.stop(new com.microsoft.playwright.Tracing.StopOptions()
						.setPath(java.nio.file.Paths.get("target/playwright-traces/trace-"
								+ this.getClass().getSimpleName() + "-" + System.currentTimeMillis() + ".zip")));
			}
			context.close();
		}
	}

	@AfterAll
	static void tearDown() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	/**
	 * Navigate to the application home page
	 */
	protected void navigateToHome() {
		page.navigate(baseUrl);
		page.waitForSelector("h2", new Page.WaitForSelectorOptions().setTimeout(10000));
	}

	/**
	 * Wait for a page to load completely
	 */
	protected void waitForPageLoad() {
		page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
	}

	/**
	 * Take a screenshot for debugging purposes
	 */
	protected void takeScreenshot(String name) {
		page.screenshot(new Page.ScreenshotOptions()
			.setPath(java.nio.file.Paths.get("target/playwright-screenshots/" + name + ".png")));
	}

	/**
	 * Load Playwright configuration from properties file
	 */
	private static Properties loadConfiguration() {
		Properties props = new Properties();
		try (InputStream is = BasePlaywrightE2ETest.class.getResourceAsStream("/playwright.properties")) {
			if (is != null) {
				props.load(is);
			}
		}
		catch (IOException e) {
			// Use default configuration if file not found
		}
		return props;
	}

}