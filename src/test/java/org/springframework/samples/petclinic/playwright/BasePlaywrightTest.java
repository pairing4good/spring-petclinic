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
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for all Playwright E2E tests providing common setup and teardown.
 *
 * @author Copilot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
public abstract class BasePlaywrightTest {

	@LocalServerPort
	protected int port;

	protected String baseUrl;

	private static Playwright playwright;

	private static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@BeforeAll
	static void launchBrowser() {
		playwright = PlaywrightConfig.createPlaywright();
		browser = PlaywrightConfig.launchBrowser(playwright);
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
		context = PlaywrightConfig.createContext(browser);
		page = PlaywrightConfig.createPage(context);
		baseUrl = "http://localhost:" + port;
	}

	@AfterEach
	void closeContext() {
		if (context != null) {
			context.close();
		}
	}

}