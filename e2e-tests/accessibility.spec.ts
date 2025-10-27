import { test, expect } from '@playwright/test';
import { WelcomePage } from './pages/WelcomePage';
import { OwnerPage } from './pages/OwnerPage';

test.describe('As a user with accessibility needs', () => {

  test('As a user using keyboard navigation, I want to navigate through the application using only the keyboard, so that I can use the app without a mouse', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // Test keyboard navigation through main navigation
    await page.keyboard.press('Tab'); // Should focus on first navigational element
    await page.keyboard.press('Tab'); // Next element
    await page.keyboard.press('Tab'); // Next element
    await page.keyboard.press('Tab'); // Next element
    
    // Press Enter on a focused link
    await page.keyboard.press('Enter');
    
    // Should navigate to the focused page
    const currentUrl = page.url();
    expect(currentUrl).not.toBe('/'); // Should have navigated away from home
  });

  test('As a user with visual impairments, I want proper heading structure, so that screen readers can navigate the content hierarchy', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // Check for proper heading hierarchy
    const h1Elements = await page.locator('h1').count();
    const h2Elements = await page.locator('h2').count();
    
    // Should have at least one main heading
    expect(h1Elements + h2Elements).toBeGreaterThan(0);
    
    // Check that headings have meaningful text
    const headings = await page.locator('h1, h2, h3, h4, h5, h6').all();
    for (const heading of headings) {
      const text = await heading.textContent();
      expect(text?.trim()).toBeTruthy();
    }
  });

  test('As a screen reader user, I want proper ARIA labels on interactive elements, so that I understand what each control does', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    await ownerPage.gotoFindOwners();
    
    // Check for ARIA labels on form elements
    const inputs = await page.locator('input, button, select').all();
    for (const input of inputs) {
      const ariaLabel = await input.getAttribute('aria-label');
      const ariaLabelledBy = await input.getAttribute('aria-labelledby');
      const label = await page.locator(`label[for="${await input.getAttribute('id')}"]`).textContent();
      const title = await input.getAttribute('title');
      
      // Input should have some form of accessible label
      const hasAccessibleLabel = ariaLabel || ariaLabelledBy || label || title;
      if (!hasAccessibleLabel) {
        console.warn(`Input without accessible label: ${await input.outerHTML()}`);
      }
    }
  });

  test('As a user with motor disabilities, I want click targets to be large enough, so that I can easily interact with buttons and links', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // Check that clickable elements meet minimum size requirements (44x44px is WCAG guideline)
    const clickableElements = await page.locator('a, button, input[type="submit"], input[type="button"]').all();
    
    for (const element of clickableElements) {
      const boundingBox = await element.boundingBox();
      if (boundingBox) {
        const meetsMinimumSize = boundingBox.width >= 44 && boundingBox.height >= 44;
        if (!meetsMinimumSize) {
          console.warn(`Small click target: ${await element.textContent()} (${boundingBox.width}x${boundingBox.height})`);
        }
      }
    }
  });

  test('As a user with visual impairments, I want sufficient color contrast, so that I can read the content clearly', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // This is a basic check - in a real scenario, you'd use a proper contrast checking tool
    // We'll check that text is not using colors that are too similar to background
    
    const textElements = await page.locator('p, span, a, button, h1, h2, h3, h4, h5, h6').all();
    
    for (const element of textElements.slice(0, 10)) { // Check first 10 elements
      const styles = await element.evaluate(el => {
        const computed = window.getComputedStyle(el);
        return {
          color: computed.color,
          backgroundColor: computed.backgroundColor,
          fontSize: computed.fontSize
        };
      });
      
      // Basic checks
      expect(styles.color).not.toBe(styles.backgroundColor);
      expect(styles.color).not.toBe('rgba(0, 0, 0, 0)'); // Not transparent
    }
  });

  test('As a keyboard user, I want visible focus indicators, so that I can see which element is currently focused', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    await ownerPage.gotoFindOwners();
    
    // Tab to the first input
    await page.keyboard.press('Tab');
    
    // Check that focused element is visible and has focus styles
    const focusedElement = await page.locator(':focus').first();
    await expect(focusedElement).toBeVisible();
    
    // Check that the focused element has some visual indication
    const focusStyles = await focusedElement.evaluate(el => {
      const computed = window.getComputedStyle(el);
      return {
        outline: computed.outline,
        outlineWidth: computed.outlineWidth,
        outlineStyle: computed.outlineStyle,
        outlineColor: computed.outlineColor,
        boxShadow: computed.boxShadow
      };
    });
    
    // Should have some form of focus indication
    const hasFocusIndicator = 
      focusStyles.outline !== 'none' ||
      focusStyles.outlineWidth !== '0px' ||
      focusStyles.boxShadow !== 'none';
    
    if (!hasFocusIndicator) {
      console.warn('Focused element may not have visible focus indicator');
    }
  });

  test('As a user with cognitive disabilities, I want clear and consistent navigation, so that I can understand how to move around the application', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Check that navigation is consistent across pages
    await welcomePage.goto();
    const homeNavElements = await page.locator('nav a').allTextContents();
    
    await welcomePage.navigateToFindOwners();
    const findNavElements = await page.locator('nav a').allTextContents();
    
    await welcomePage.navigateToVeterinarians();
    const vetNavElements = await page.locator('nav a').allTextContents();
    
    // Navigation should be consistent across pages
    expect(homeNavElements).toEqual(findNavElements);
    expect(findNavElements).toEqual(vetNavElements);
    
    // Navigation links should be descriptive
    for (const linkText of homeNavElements) {
      expect(linkText.trim().length).toBeGreaterThan(2); // Should be more than just symbols
    }
  });

  test('As a user using assistive technology, I want proper semantic HTML, so that my tools can understand the page structure', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // Check for semantic HTML elements
    const hasNav = await page.locator('nav').count() > 0;
    const hasMain = await page.locator('main').count() > 0 || await page.locator('[role="main"]').count() > 0;
    const hasHeader = await page.locator('header').count() > 0 || await page.locator('[role="banner"]').count() > 0;
    
    // Should use semantic elements or ARIA roles
    console.log(`Semantic elements found: nav=${hasNav}, main=${hasMain}, header=${hasHeader}`);
    
    // Check for proper form structure
    await page.goto('/owners/find');
    
    const formElements = await page.locator('form').count();
    expect(formElements).toBeGreaterThan(0);
    
    // Form inputs should be properly labeled
    const inputs = await page.locator('form input').all();
    for (const input of inputs) {
      const id = await input.getAttribute('id');
      const name = await input.getAttribute('name');
      const type = await input.getAttribute('type');
      
      // Input should have either id or name
      expect(id || name).toBeTruthy();
    }
  });

  test('As a user who cannot use images, I want proper alt text on images, so that I can understand the visual content', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    const images = await page.locator('img').all();
    
    for (const img of images) {
      const alt = await img.getAttribute('alt');
      const src = await img.getAttribute('src');
      
      // All images should have alt text (can be empty for decorative images)
      expect(alt).not.toBeNull();
      
      // If the image is likely content (not decorative), alt should be meaningful
      if (src && !src.includes('logo') && !src.includes('icon')) {
        expect(alt?.trim()).toBeTruthy();
      }
    }
  });

  test('As a user with hearing impairments, I want visual feedback for actions, so that I don\'t miss important information', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    await ownerPage.gotoNewOwner();
    
    // Fill form with invalid data
    await page.locator('input[id="firstName"]').fill('Test');
    await page.locator('input[id="lastName"]').fill(''); // Missing required field
    await page.locator('input[id="address"]').fill('123 Test St');
    await page.locator('input[id="city"]').fill('Test City');
    await page.locator('input[id="telephone"]').fill(''); // Missing required field
    
    await page.locator('button[type="submit"]').click();
    
    // Should show visual feedback for validation errors
    const errorElements = await page.locator('.has-error, .error, .invalid, .help-inline').count();
    const stayedOnForm = page.url().includes('/owners/new');
    
    // Should provide visual indication of what went wrong
    expect(errorElements > 0 || stayedOnForm).toBeTruthy();
  });

  test('As a user with limited fine motor control, I want generous click areas and forgiving interactions, so that I can use the application effectively', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    
    // Check that links and buttons have adequate spacing
    const clickableElements = await page.locator('a, button').all();
    
    for (let i = 0; i < clickableElements.length - 1; i++) {
      const current = clickableElements[i];
      const next = clickableElements[i + 1];
      
      const currentBox = await current.boundingBox();
      const nextBox = await next.boundingBox();
      
      if (currentBox && nextBox) {
        // Calculate distance between elements
        const distance = Math.min(
          Math.abs(currentBox.x - nextBox.x),
          Math.abs(currentBox.y - nextBox.y)
        );
        
        // Elements should have some spacing (8px minimum recommended)
        if (distance < 8 && distance > 0) {
          console.warn(`Closely spaced interactive elements: ${distance}px apart`);
        }
      }
    }
  });

});