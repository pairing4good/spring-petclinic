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
package org.springframework.samples.petclinic.e2e.config;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for all Playwright E2E tests providing common configuration and lifecycle
 * management.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PlaywrightTestBase {

	@LocalServerPort
	protected int port;

	protected static Playwright playwright;

	protected static Browser browser;

	protected Page page;

	protected String baseUrl;

	@BeforeAll
	static void launchBrowser() {
		try {
			playwright = Playwright.create();
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(0));
		}
		catch (Exception e) {
			System.err.println("Playwright browser installation failed: " + e.getMessage());
			System.err.println("E2E tests will be skipped. In CI/CD, browsers should be pre-installed.");
			// Don't throw exception, let individual tests handle the null browser
		}
	}

	@AfterAll
	static void closeBrowser() {
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void createPage() {
		// Skip tests if browser is not available
		Assumptions.assumeTrue(browser != null, "Playwright browser not available - skipping E2E test");

		baseUrl = "http://localhost:" + port;
		page = browser.newPage();
		// Set a reasonable timeout for tests
		page.setDefaultTimeout(10000);
	}

	@AfterEach
	void closePage() {
		if (page != null) {
			page.close();
		}
	}

	protected void navigateToHome() {
		page.navigate(baseUrl);
	}

}