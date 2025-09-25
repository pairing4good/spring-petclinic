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

import java.util.List;

/**
 * Base class for all Playwright E2E tests providing common setup and teardown.
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
	static void setupPlaywright() {
		// Install browsers programmatically if not available
		try {
			playwright = Playwright.create();
			
			// Configure browser options for CI/headless environment
			BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
				.setHeadless(true)
				.setArgs(List.of(
					"--no-sandbox",
					"--disable-dev-shm-usage",
					"--disable-web-security",
					"--disable-features=VizDisplayCompositor"
				));
			
			// Try different browsers in order of preference
			try {
				browser = playwright.chromium().launch(options);
			} catch (Exception e) {
				try {
					browser = playwright.firefox().launch(options);
				} catch (Exception e2) {
					browser = playwright.webkit().launch(options);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize Playwright. Please ensure browsers are installed.", e);
		}
	}

	@BeforeEach
	void setupTest() {
		// Create a new browser context for each test to ensure isolation
		context = browser.newContext();
		page = context.newPage();
		
		// Set default timeout for all operations
		page.setDefaultTimeout(30000); // 30 seconds
		page.setDefaultNavigationTimeout(30000); // 30 seconds
	}

	@AfterEach
	void teardownTest() {
		if (page != null) {
			page.close();
		}
		if (context != null) {
			context.close();
		}
	}

	@AfterAll
	static void teardownPlaywright() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	/**
	 * Get the base URL for the application
	 */
	protected String getBaseUrl() {
		return "http://localhost:" + port;
	}

	/**
	 * Navigate to a specific path
	 */
	protected void navigateTo(String path) {
		page.navigate(getBaseUrl() + path);
	}

	/**
	 * Navigate to home page
	 */
	protected void navigateToHome() {
		navigateTo("/");
	}

}