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
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Paths;

/**
 * Base class for Playwright E2E tests providing common setup and configuration. Uses
 * system-installed browsers to avoid download issues.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BasePlaywrightTest {

	@LocalServerPort
	protected int port;

	protected Playwright playwright;

	protected Browser browser;

	protected BrowserContext context;

	protected Page page;

	@BeforeEach
	void setUpPlaywright() {
		playwright = Playwright.create();
		// Use system-installed browser with custom executable path to avoid download
		// issues
		browser = playwright.chromium()
			.launch(new BrowserType.LaunchOptions().setExecutablePath(Paths.get("/usr/bin/google-chrome"))
				.setHeadless(true));
		context = browser.newContext();
		page = context.newPage();
	}

	@AfterEach
	void tearDownPlaywright() {
		if (page != null)
			page.close();
		if (context != null)
			context.close();
		if (browser != null)
			browser.close();
		if (playwright != null)
			playwright.close();
	}

	/**
	 * Get the base URL for the application
	 */
	protected String getBaseUrl() {
		return "http://localhost:" + port;
	}

	/**
	 * Navigate to a specific path and wait for the page to load
	 */
	protected void navigateToPath(String path) {
		page.navigate(getBaseUrl() + path);
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Navigate to the home page
	 */
	protected void navigateToHome() {
		navigateToPath("/");
	}

}