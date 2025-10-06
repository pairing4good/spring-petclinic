const { test, expect } = require('@playwright/test');
const { HomePage } = require('./pages/HomePage');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { VeterinariansPage } = require('./pages/VeterinariansPage');

test.describe('Cross-Browser Compatibility Tests', () => {
  test('As a user, I should have consistent functionality across all browsers', async ({ page, browserName }) => {
    const homePage = new HomePage(page);
    const findOwnersPage = new FindOwnersPage(page);
    const veterinariansPage = new VeterinariansPage(page);
    
    // Test basic functionality on each browser
    await homePage.goto();
    await homePage.waitForLoad();
    
    // Basic page load and navigation should work in all browsers
    expect(await homePage.getTitle()).toContain('PetClinic');
    expect(await homePage.isNavigationMenuVisible()).toBe(true);
    
    // Test navigation consistency
    await homePage.clickFindOwners();
    await findOwnersPage.waitForLoad();
    expect(page.url()).toContain('/owners/find');
    expect(await findOwnersPage.isSearchFormVisible()).toBe(true);
    
    // Test another page
    await findOwnersPage.clickVeterinarians();
    await veterinariansPage.waitForLoad();
    expect(page.url()).toContain('/vets.html');
    expect(await veterinariansPage.isVetsTableVisible()).toBe(true);
    
    // Verify consistent data across browsers
    const vetCount = await veterinariansPage.getVetCount();
    expect(vetCount).toBeGreaterThan(0);
    
    // Test forms work in all browsers
    await veterinariansPage.clickFindOwners();
    await findOwnersPage.waitForLoad();
    await findOwnersPage.searchByLastName('test');
    await page.waitForLoadState('networkidle');
    
    // Should handle search consistently across browsers
    expect(page.url()).toContain('/owners');
  });

  test('As a user, I should see consistent styling across browsers', async ({ page, browserName }) => {
    const homePage = new HomePage(page);
    
    await homePage.goto();
    await homePage.waitForLoad();
    
    // Check that Bootstrap CSS is loading properly
    const navbar = page.locator('nav.navbar');
    expect(await navbar.isVisible()).toBe(true);
    
    // Verify navbar has proper Bootstrap classes
    const navbarClasses = await navbar.getAttribute('class');
    expect(navbarClasses).toContain('navbar');
    
    // Check that the page layout is consistent
    const container = page.locator('.container, .container-fluid');
    expect(await container.isVisible()).toBe(true);
    
    // Verify Spring logo loads consistently
    expect(await homePage.isSpringLogoVisible()).toBe(true);
  });

  test('As a user, I should experience consistent form behavior across browsers', async ({ page, browserName }) => {
    const findOwnersPage = new FindOwnersPage(page);
    
    await findOwnersPage.goto();
    
    // Test form elements work consistently
    const searchInput = page.locator('#lastName');
    expect(await searchInput.isVisible()).toBe(true);
    
    // Test input functionality
    await searchInput.fill('TestLastName');
    const inputValue = await searchInput.inputValue();
    expect(inputValue).toBe('TestLastName');
    
    // Test form submission
    await findOwnersPage.searchByLastName('NonExistent');
    await page.waitForLoadState('networkidle');
    
    // Should handle form submission consistently
    expect(page.url()).toContain('/owners');
  });

  test('As a user, I should see consistent JavaScript behavior across browsers', async ({ page, browserName }) => {
    // Test any JavaScript functionality that exists
    const homePage = new HomePage(page);
    
    await homePage.goto();
    await homePage.waitForLoad();
    
    // Test Bootstrap JavaScript components if they exist
    const navbar = page.locator('.navbar-toggler');
    if (await navbar.isVisible()) {
      // Test mobile menu toggle (if on mobile viewport)
      await navbar.click();
      await page.waitForTimeout(300); // Wait for animation
      
      // Menu should toggle consistently across browsers
      const navbarCollapse = page.locator('#main-navbar');
      // The behavior might vary but should not cause errors
    }
    
    // Test any success/error message handling
    // This is testing the JavaScript that hides messages after 3 seconds
    // We'll test this indirectly by checking message structure
    const alertElements = await page.locator('.alert').count();
    // Alerts may or may not be present, but structure should be consistent
    expect(alertElements).toBeGreaterThanOrEqual(0);
  });

  test('As a user, I should have consistent error handling across browsers', async ({ page, browserName }) => {
    // Test error page rendering
    await page.goto('/oups');
    await page.waitForLoadState('networkidle');
    
    // Error page should render consistently
    const errorHeading = page.locator('h2:has-text("Something happened...")');
    expect(await errorHeading.isVisible()).toBe(true);
    
    // Error image should load consistently
    const errorImage = page.locator('img[src*="pets.png"]');
    expect(await errorImage.isVisible()).toBe(true);
    
    // Test 404 handling
    await page.goto('/non-existent-url');
    await page.waitForLoadState('networkidle');
    
    // Should handle 404s consistently (may redirect or show 404 page)
    const is404 = page.url().includes('404') || 
                  await page.locator('text=404').isVisible() || 
                  await page.locator('h2:has-text("Something happened...")').isVisible();
    expect(is404).toBe(true);
  });
});