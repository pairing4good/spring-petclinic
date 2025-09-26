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
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * TEMPORARY test to verify Playwright configuration and basic functionality. This test
 * will be DELETED once configuration is validated.
 *
 * As a developer, I want to verify Playwright setup, so that I can confirm basic
 * functionality works.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TempConfigurationTest {

	@LocalServerPort
	private int port;

	private static Playwright playwright;

	private static Browser browser;

	private Page page;

	@BeforeAll
	static void launchBrowser() {
		try {
			playwright = Playwright.create();
			// Try to use system Chrome first
			browser = playwright.chromium()
				.launch(new BrowserType.LaunchOptions().setHeadless(true)
					.setExecutablePath(Paths.get("/usr/bin/google-chrome")));
		}
		catch (Exception e) {
			// If system Chrome fails, try without executable path (default Playwright
			// behavior)
			try {
				browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			}
			catch (Exception e2) {
				throw new RuntimeException("Could not launch browser. Make sure Chrome or Chromium is installed.", e2);
			}
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
		page = browser.newPage();
	}

	@AfterEach
	void closeContext() {
		if (page != null) {
			page.close();
		}
	}

	@Test
	void tempTest_VerifyHomepageTitle() {
		// Navigate to homepage
		page.navigate("http://localhost:" + port);

		// Verify page title
		assertThat(page).hasTitle("PetClinic :: a Spring Framework demonstration");

		// Verify welcome heading is present
		assertThat(page.locator("h2")).hasText("Welcome");
	}

}