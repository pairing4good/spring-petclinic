package org.springframework.samples.petclinic.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for Playwright user acceptance tests. Provides shared setup and teardown for
 * browser automation testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BasePlaywrightTest {

	protected static Playwright playwright;

	protected static Browser browser;

	protected BrowserContext context;

	protected Page page;

	@LocalServerPort
	protected int port;

	protected String getBaseUrl() {
		return "http://localhost:" + port;
	}

	@BeforeAll
	static void initPlaywright() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(100) // Slight
																												// delay
																												// to
																												// improve
																												// stability
		);
	}

	@AfterAll
	static void closePlaywright() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void setUp() {
		context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720));
		page = context.newPage();

		// Set reasonable timeouts for stability
		page.setDefaultTimeout(10000); // 10 seconds
	}

	@AfterEach
	void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	/**
	 * Helper method to wait for page to be ready
	 */
	protected void waitForPageReady() {
		page.waitForLoadState(LoadState.NETWORKIDLE);
	}

	/**
	 * Helper method to take screenshot for debugging
	 */
	protected void takeScreenshot(String name) {
		page.screenshot(new Page.ScreenshotOptions()
			.setPath(java.nio.file.Paths.get("target/playwright-screenshots/" + name + ".png"))
			.setFullPage(true));
	}

}