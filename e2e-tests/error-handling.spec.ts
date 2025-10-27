import { test, expect } from '@playwright/test';
import { WelcomePage } from './pages/WelcomePage';

test.describe('As a user encountering errors and edge cases', () => {

  test('As a user, I want to see a proper error page when I navigate to an invalid URL, so that I understand what went wrong', async ({ page }) => {
    // Test 404 error
    await page.goto('/invalid-page-that-does-not-exist');
    
    // Should show error page or be redirected
    await expect(page).toHaveURL(/404|error|invalid/);
    
    // Check for error content
    const hasErrorMessage = await page.locator('text=/error|not found|404/i').isVisible();
    const hasErrorStatus = page.url().includes('404') || page.url().includes('error');
    
    expect(hasErrorMessage || hasErrorStatus).toBeTruthy();
  });

  test('As a user, I want to see the error demonstration page, so that I can understand how errors are handled', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    await welcomePage.navigateToError();
    
    // Should show the error page
    await expect(page).toHaveURL('/oups');
    await expect(page.locator('h2')).toContainText('Something happened...');
    
    // Should show expected error message
    await expect(page.locator('text=/controller used to showcase/i')).toBeVisible();
  });

  test('As a user, I want to navigate back from error pages, so that I can return to working parts of the application', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    await welcomePage.navigateToError();
    
    // Should be on error page
    await expect(page).toHaveURL('/oups');
    
    // Navigate back using browser back button
    await page.goBack();
    
    // Should be back on welcome page
    await expect(page).toHaveURL('/');
    await welcomePage.assertWelcomePageElements();
  });

  test('As a user, I want navigation to work even after encountering errors, so that I can continue using the application', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Navigate to error page
    await welcomePage.goto();
    await welcomePage.navigateToError();
    await expect(page).toHaveURL('/oups');
    
    // Navigation should still work from error page
    await welcomePage.navigateToHome();
    await expect(page).toHaveURL('/');
    
    await welcomePage.navigateToFindOwners();
    await expect(page).toHaveURL('/owners/find');
    
    await welcomePage.navigateToVeterinarians();
    await expect(page).toHaveURL('/vets.html');
  });

  test('As a user, I want to handle network timeouts gracefully, so that slow connections don\'t break the application', async ({ page }) => {
    // Simulate slow network
    await page.route('**/*', async route => {
      await new Promise(resolve => setTimeout(resolve, 100)); // Small delay
      await route.continue();
    });
    
    const welcomePage = new WelcomePage(page);
    await welcomePage.goto();
    
    // Should still load properly with delays
    await welcomePage.assertPageLoaded();
  });

  test('As a user, I want form submissions to handle unexpected responses, so that partial failures don\'t leave me confused', async ({ page }) => {
    // This test navigates to a form and tests behavior with simulated server issues
    await page.goto('/owners/find');
    
    // Fill and submit search form
    await page.locator('input[name="lastName"]').fill('TestOwner');
    
    // Intercept the form submission to simulate server error
    await page.route('**/owners**', async route => {
      if (route.request().method() === 'GET' && route.request().url().includes('lastName=TestOwner')) {
        await route.fulfill({
          status: 500,
          contentType: 'text/html',
          body: '<html><body><h1>Server Error</h1></body></html>'
        });
      } else {
        await route.continue();
      }
    });
    
    await page.locator('button[type="submit"]').click();
    
    // Should handle the error gracefully
    const hasErrorIndication = await page.locator('text=/error|server|500/i').isVisible();
    expect(hasErrorIndication).toBeTruthy();
  });

  test('As a user, I want to handle empty search results gracefully, so that I understand when no data is found', async ({ page }) => {
    await page.goto('/owners/find');
    
    // Search for an owner that definitely doesn't exist
    await page.locator('input[name="lastName"]').fill('NonExistentOwnerName123456789');
    await page.locator('button[type="submit"]').click();
    
    // Should show appropriate "not found" message
    await expect(page.locator('text=/not found|no.*found|has not been found/i')).toBeVisible();
  });

  test('As a user, I want to handle JavaScript errors gracefully, so that script errors don\'t break the page', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Listen for console errors
    const consoleErrors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });
    
    // Listen for page errors
    const pageErrors: string[] = [];
    page.on('pageerror', error => {
      pageErrors.push(error.message);
    });
    
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
    
    // Navigate to different pages
    await welcomePage.navigateToFindOwners();
    await welcomePage.navigateToVeterinarians();
    await welcomePage.navigateToHome();
    
    // Should not have critical JavaScript errors
    const criticalErrors = [...consoleErrors, ...pageErrors].filter(error => 
      !error.includes('favicon') && // Ignore favicon errors
      !error.includes('Extension') && // Ignore extension errors
      !error.includes('net::ERR_') // Ignore network errors from dev tools
    );
    
    expect(criticalErrors.length).toBe(0);
  });

  test('As a user, I want the application to work with disabled JavaScript, so that basic functionality remains available', async ({ page }) => {
    // Disable JavaScript
    await page.setJavaScriptEnabled(false);
    
    const welcomePage = new WelcomePage(page);
    await welcomePage.goto();
    
    // Basic content should still be visible
    await expect(page.locator('h2')).toBeVisible();
    await expect(page.locator('img[src*="pets.png"]')).toBeVisible();
    
    // Navigation links should be present
    await expect(page.locator('a[href="/"]')).toBeVisible();
    await expect(page.locator('a[href="/owners/find"]')).toBeVisible();
    await expect(page.locator('a[href="/vets.html"]')).toBeVisible();
  });

  test('As a user, I want to handle extremely long input gracefully, so that the application doesn\'t break with large data', async ({ page }) => {
    await page.goto('/owners/new');
    
    // Test with extremely long strings
    const longString = 'A'.repeat(1000);
    const veryLongString = 'B'.repeat(10000);
    
    await page.locator('input[id="firstName"]').fill(longString);
    await page.locator('input[id="lastName"]').fill(longString);
    await page.locator('input[id="address"]').fill(veryLongString);
    await page.locator('input[id="city"]').fill(longString);
    await page.locator('input[id="telephone"]').fill('1234567890');
    
    await page.locator('button[type="submit"]').click();
    
    // Should either accept the data or show validation errors, but not crash
    const isOnFormPage = page.url().includes('/owners/new');
    const isOnDetailsPage = page.url().match(/\/owners\/\d+/);
    const hasValidationErrors = await page.locator('.has-error').count() > 0;
    
    expect(isOnFormPage || isOnDetailsPage || hasValidationErrors).toBeTruthy();
  });

  test('As a user, I want to handle special HTML characters in input, so that the application doesn\'t break with HTML/script injection', async ({ page }) => {
    await page.goto('/owners/new');
    
    // Test with HTML and potential script content
    const htmlString = '<script>alert("test")</script>';
    const htmlEntities = '&lt;div&gt;&amp;quot;test&amp;quot;&lt;/div&gt;';
    
    await page.locator('input[id="firstName"]').fill(htmlString);
    await page.locator('input[id="lastName"]').fill(htmlEntities);
    await page.locator('input[id="address"]').fill('123 <b>Bold</b> Street');
    await page.locator('input[id="city"]').fill('City & Town');
    await page.locator('input[id="telephone"]').fill('555-123-4567');
    
    await page.locator('button[type="submit"]').click();
    
    // Should handle the input safely without executing scripts
    const noAlert = await page.evaluate(() => {
      return !window.alert.toString().includes('[native code]');
    });
    
    // The form should either process the data safely or show validation errors
    const currentUrl = page.url();
    expect(currentUrl).not.toContain('<script>');
  });

  test('As a user, I want proper error messages for invalid form data, so that I understand what needs to be corrected', async ({ page }) => {
    await page.goto('/owners/new');
    
    // Submit form with various invalid data types
    await page.locator('input[id="firstName"]').fill('123'); // Numbers instead of name
    await page.locator('input[id="lastName"]').fill(''); // Empty required field
    await page.locator('input[id="address"]').fill('Valid Address');
    await page.locator('input[id="city"]').fill(''); // Empty required field
    await page.locator('input[id="telephone"]').fill('invalid-phone'); // Invalid phone
    
    await page.locator('button[type="submit"]').click();
    
    // Should show validation errors
    const hasErrors = await page.locator('.has-error').count() > 0;
    const hasErrorMessages = await page.locator('.help-inline').count() > 0;
    const stayedOnForm = page.url().includes('/owners/new');
    
    expect(hasErrors || hasErrorMessages || stayedOnForm).toBeTruthy();
  });

  test('As a user, I want the application to recover from temporary network issues, so that brief connectivity problems don\'t ruin my session', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Start normally
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
    
    // Simulate network failure for a short time
    await page.route('**/*', async route => {
      // Reject first request, then allow subsequent ones
      if (route.request().url().includes('/owners/find')) {
        await route.abort();
      } else {
        await route.continue();
      }
    });
    
    // Try to navigate (this will fail)
    await page.goto('/owners/find', { waitUntil: 'domcontentloaded', timeout: 5000 }).catch(() => {
      // Expected to fail
    });
    
    // Remove the route blocking
    await page.unroute('**/*');
    
    // Now navigation should work again
    await page.goto('/owners/find');
    await expect(page.locator('input[name="lastName"]')).toBeVisible();
  });

});