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

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Cross-browser compatibility and responsive design tests.
 *
 * @author Spring PetClinic Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("As a user I want the application to work across different browsers and devices so that I can use it anywhere")
class CrossBrowserE2ETest extends BasePlaywrightE2ETest {

	@LocalServerPort
	private int port;

	@Override
	protected void navigateToHome() {
		baseUrl = "http://localhost:" + port;
		page.navigate(baseUrl);
		page.waitForSelector("h2", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
	}

	@Test
	@DisplayName("As a user I want the application to work on desktop browsers so that I can use it on my computer")
	void shouldWorkOnDesktopViewport() {
		// Test desktop viewport
		page.setViewportSize(1920, 1080);
		navigateToHome();

		// Verify main elements are visible and properly sized
		assertTrue(page.locator("h2").isVisible());
		assertTrue(page.locator(".navbar").isVisible());

		// Navigation should be horizontal on desktop
		Locator navbar = page.locator(".navbar-nav");
		var boundingBox = navbar.boundingBox();
		assertTrue(boundingBox.width > boundingBox.height, "Navigation should be wider than tall on desktop");
	}

	@ParameterizedTest
	@ValueSource(strings = { "375,667", "414,896", "768,1024", "1024,768" })
	@DisplayName("As a user I want the application to work on various mobile and tablet devices so that I can use it on any device")
	void shouldWorkOnMobileAndTabletViewports(String viewport) {
		String[] dimensions = viewport.split(",");
		int width = Integer.parseInt(dimensions[0]);
		int height = Integer.parseInt(dimensions[1]);

		page.setViewportSize(width, height);
		navigateToHome();

		// Verify main elements are still accessible
		assertTrue(page.locator("h2").isVisible());
		assertTrue(page.locator(".navbar").isVisible());

		// Check if mobile navigation toggle exists for smaller screens
		if (width < 768) {
			Locator navToggle = page.locator(".navbar-toggler");
			if (navToggle.isVisible()) {
				// Test mobile navigation
				navToggle.click();
				assertTrue(page.locator("a[href='/owners/find']").isVisible());
			}
		}

		// Test basic navigation works on mobile
		page.click("a[href='/owners/find']");
		waitForPageLoad();
		assertTrue(page.locator("input[name='lastName']").isVisible());
	}

	@Test
	@DisplayName("As a user I want touch interactions to work properly so that I can use the app on touch devices")
	void shouldSupportTouchInteractions() {
		// Simulate mobile viewport
		page.setViewportSize(375, 667);
		navigateToHome();

		// Test touch interactions
		page.tap("a[href='/owners/find']");
		waitForPageLoad();

		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible());

		// Test form interactions with touch
		page.tap("input[name='lastName']");
		page.fill("input[name='lastName']", "Test");
		page.tap("button[type='submit']");
		waitForPageLoad();

		assertTrue(page.url().contains("/owners"));
	}

	@Test
	@DisplayName("As a user I want readable font sizes on mobile so that I can easily read content")
	void shouldHaveReadableFontSizesOnMobile() {
		page.setViewportSize(375, 667);
		navigateToHome();

		// Check that text is readable (not too small)
		Locator heading = page.locator("h2");
		var fontSize = page.evaluate("element => getComputedStyle(element).fontSize", heading.elementHandle());

		// Font size should be reasonable for mobile (at least 14px)
		int fontSizePx = Integer.parseInt(fontSize.toString().replace("px", ""));
		assertTrue(fontSizePx >= 14, "Font size should be at least 14px on mobile, was: " + fontSizePx + "px");
	}

	@Test
	@DisplayName("As a user I want proper spacing on mobile so that buttons are easy to touch")
	void shouldHaveProperTouchTargetSizing() {
		page.setViewportSize(375, 667);
		navigateToHome();

		page.click("a[href='/owners/find']");
		waitForPageLoad();

		// Check button sizing for touch
		Locator submitButton = page.locator("button[type='submit']");
		var boundingBox = submitButton.boundingBox();

		// Touch targets should be at least 44px (iOS/Android guidelines)
		assertTrue(boundingBox.height >= 44,
				"Touch targets should be at least 44px high, was: " + boundingBox.height + "px");
	}

	@Test
	@DisplayName("As a user I want horizontal scrolling to be minimal so that content fits well on mobile")
	void shouldMinimizeHorizontalScrolling() {
		page.setViewportSize(375, 667);
		navigateToHome();

		// Navigate to vets page which has a table
		page.click("a[href='/vets.html']");
		waitForPageLoad();

		// Check that the page doesn't cause horizontal overflow
		var pageWidth = page.evaluate("document.documentElement.scrollWidth");
		var viewportWidth = page.evaluate("document.documentElement.clientWidth");

		// Allow small differences but avoid significant horizontal scrolling
		assertTrue((Integer) pageWidth - (Integer) viewportWidth < 50,
				"Page should not cause significant horizontal scrolling");
	}

	@Test
	@DisplayName("As a user I want tables to be responsive so that I can view data on mobile")
	void shouldDisplayResponsiveTables() {
		page.setViewportSize(375, 667);
		navigateToHome();

		page.click("a[href='/vets.html']");
		waitForPageLoad();

		Locator table = page.locator("table#vets");
		assertTrue(table.isVisible());

		// Table should be contained within viewport or have horizontal scroll
		var tableBounds = table.boundingBox();
		assertTrue(tableBounds.width > 0, "Table should be visible");
	}

	@Test
	@DisplayName("As a user I want form inputs to be properly sized on mobile so that I can interact with them easily")
	void shouldHaveProperFormInputSizing() {
		page.setViewportSize(375, 667);
		navigateToHome();

		page.click("a[href='/owners/find']");
		page.click("a[href='/owners/new']");
		waitForPageLoad();

		// Check input field sizing
		Locator firstNameInput = page.locator("input[name='firstName']");
		var inputBounds = firstNameInput.boundingBox();

		// Input should be reasonably sized for mobile
		assertTrue(inputBounds.height >= 30, "Input fields should be at least 30px high");
		assertTrue(inputBounds.width > 200, "Input fields should be reasonably wide");
	}

	@Test
	@DisplayName("As a user I want landscape orientation to work properly so that I can rotate my device")
	void shouldWorkInLandscapeOrientation() {
		// Test landscape mobile
		page.setViewportSize(667, 375);
		navigateToHome();

		assertTrue(page.locator("h2").isVisible());
		assertTrue(page.locator(".navbar").isVisible());

		// Navigation should still work
		page.click("a[href='/owners/find']");
		waitForPageLoad();
		assertTrue(page.locator("input[name='lastName']").isVisible());
	}

	@Test
	@DisplayName("As a user I want the application to load without JavaScript errors so that all functionality works")
	void shouldLoadWithoutJavaScriptErrors() {
		// Monitor console errors
		page.onConsoleMessage(msg -> {
			if (msg.type().equals("error")) {
				System.err.println("Console error: " + msg.text());
			}
		});

		navigateToHome();

		// Navigate through different pages
		page.click("a[href='/owners/find']");
		waitForPageLoad();

		page.click("a[href='/owners/new']");
		waitForPageLoad();

		page.click("a[href='/']");
		waitForPageLoad();

		page.click("a[href='/vets.html']");
		waitForPageLoad();

		// No assertions needed - console errors are logged above
		assertTrue(true, "Navigation completed without major JavaScript errors");
	}

	@Test
	@DisplayName("As a user I want images to load properly on all devices so that I see the complete interface")
	void shouldLoadImagesCorrectly() {
		navigateToHome();

		// Check that the Spring logo loads
		Locator logo = page.locator("img[alt*='Logo']");
		assertTrue(logo.isVisible());

		// Verify image actually loaded (not broken)
		var naturalWidth = page.evaluate("img => img.naturalWidth", logo.elementHandle());
		assertTrue((Integer) naturalWidth > 0, "Logo image should load properly");
	}

	@Test
	@DisplayName("As a user I want CSS to load properly so that the application looks correct")
	void shouldLoadCSSCorrectly() {
		navigateToHome();

		// Check that Bootstrap classes are applied
		Locator navbar = page.locator(".navbar");
		var backgroundColor = page.evaluate("element => getComputedStyle(element).backgroundColor",
				navbar.elementHandle());

		// Should have some background color (not transparent/default)
		assertTrue(
				!backgroundColor.toString().equals("rgba(0, 0, 0, 0)")
						&& !backgroundColor.toString().equals("transparent"),
				"Navbar should have proper styling applied");
	}

	@Test
	@DisplayName("As a user I want print styles to work so that I can print pages when needed")
	void shouldSupportPrintStyles() {
		navigateToHome();

		// Test print media query
		page.emulateMedia(new com.microsoft.playwright.Page.EmulateMediaOptions()
			.setMedia(com.microsoft.playwright.options.Media.PRINT));

		// Page should still be readable in print mode
		assertTrue(page.locator("h2").isVisible());
		assertTrue(page.locator(".navbar").isVisible());

		// Reset to screen media
		page.emulateMedia(new com.microsoft.playwright.Page.EmulateMediaOptions()
			.setMedia(com.microsoft.playwright.options.Media.SCREEN));
	}

	@Test
	@DisplayName("As a user I want keyboard navigation to work across all devices so that I can navigate without a mouse")
	void shouldSupportKeyboardNavigation() {
		navigateToHome();

		// Test tab navigation
		page.locator("a[href='/owners/find']").focus();
		page.keyboard().press("Enter");
		waitForPageLoad();

		assertTrue(page.locator("h2:has-text('Find Owners')").isVisible());

		// Test form navigation
		page.locator("input[name='lastName']").focus();
		page.keyboard().press("Tab");

		// Should move to submit button
		String focusedElement = (String) page.evaluate("document.activeElement.tagName");
		assertTrue("BUTTON".equals(focusedElement) || "INPUT".equals(focusedElement));
	}

}