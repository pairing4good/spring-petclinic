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

package org.springframework.samples.petclinic.acceptance;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for Playwright acceptance tests.
 * Provides common setup and teardown for browser automation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("playwright")
public abstract class BasePlaywrightTest {

	@LocalServerPort
	private int port;

	protected String baseUrl;

	private static Playwright playwright;

	private static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@BeforeAll
	static void launchBrowser() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
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
		baseUrl = "http://localhost:" + port;
		context = browser.newContext();
		page = context.newPage();
	}

	@AfterEach
	void closeContext() {
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Navigate to the application home page
	 */
	protected void navigateToHome() {
		page.navigate(baseUrl);
		page.waitForLoadState();
	}

	/**
	 * Navigate to a specific path within the application
	 */
	protected void navigateTo(String path) {
		page.navigate(baseUrl + path);
		page.waitForLoadState();
	}

	/**
	 * Click on a link by its text content
	 */
	protected void clickLink(String linkText) {
		page.locator("a").filter(new Locator.FilterOptions().setHasText(linkText)).first().click();
	}

	/**
	 * Click on a button by its text content
	 */
	protected void clickButton(String buttonText) {
		page.locator("button").filter(new Locator.FilterOptions().setHasText(buttonText)).first().click();
	}

}