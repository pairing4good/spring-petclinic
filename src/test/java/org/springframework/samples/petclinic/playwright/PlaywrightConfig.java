/*
 * Copyright 2012-2019 the original author or authors.
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

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;

/**
 * Configuration class for Playwright browser setup and management.
 *
 * @author Copilot
 */
public class PlaywrightConfig {

	/**
	 * Create a new Playwright instance with browser launch options for testing.
	 * @return configured Playwright instance
	 */
	public static Playwright createPlaywright() {
		return Playwright.create();
	}

	/**
	 * Launch browser with test configuration.
	 * @param playwright the Playwright instance
	 * @return configured Browser instance
	 */
	public static Browser launchBrowser(Playwright playwright) {
		return playwright.chromium()
			.launch(new BrowserType.LaunchOptions().setHeadless(true) // Run headless in
																		// CI
				.setSlowMo(50) // Add slight delay for reliability
				.setExecutablePath(Paths.get("/usr/bin/chromium-browser"))); // Use system
																				// chromium
	}

	/**
	 * Create new browser context with test settings.
	 * @param browser the Browser instance
	 * @return new BrowserContext
	 */
	public static BrowserContext createContext(Browser browser) {
		return browser
			.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720).setIgnoreHTTPSErrors(true));
	}

	/**
	 * Create new page in the browser context.
	 * @param context the BrowserContext instance
	 * @return new Page
	 */
	public static Page createPage(BrowserContext context) {
		Page page = context.newPage();
		// Set default timeout
		page.setDefaultTimeout(30000); // 30 seconds
		return page;
	}

}