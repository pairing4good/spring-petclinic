const { test, expect } = require('@playwright/test');
const { HomePage } = require('./pages/HomePage');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { VeterinariansPage } = require('./pages/VeterinariansPage');

test.describe('Responsive Design Tests', () => {
  const viewports = [
    { name: 'Desktop', width: 1920, height: 1080 },
    { name: 'Tablet', width: 768, height: 1024 },
    { name: 'Mobile', width: 375, height: 667 }
  ];

  viewports.forEach(viewport => {
    test(`As a user, I should see a responsive layout on ${viewport.name} (${viewport.width}x${viewport.height})`, async ({ page }) => {
      await page.setViewportSize({ width: viewport.width, height: viewport.height });
      
      const homePage = new HomePage(page);
      await homePage.goto();
      await homePage.waitForLoad();
      
      // Verify navigation is present and functional
      expect(await homePage.isNavigationMenuVisible()).toBe(true);
      
      // Check if navbar toggle button is visible on mobile
      if (viewport.width < 992) { // Bootstrap's lg breakpoint
        const toggleButton = page.locator('.navbar-toggler');
        expect(await toggleButton.isVisible()).toBe(true);
        
        // Click toggle to open mobile menu
        await toggleButton.click();
        await page.waitForTimeout(300); // Wait for animation
      }
      
      // Verify main content is visible
      expect(await homePage.isWelcomeHeaderVisible()).toBe(true);
      expect(await homePage.isSpringLogoVisible()).toBe(true);
      
      // Test navigation on this viewport
      await homePage.clickFindOwners();
      const findOwnersPage = new FindOwnersPage(page);
      await findOwnersPage.waitForLoad();
      expect(await findOwnersPage.isSearchFormVisible()).toBe(true);
    });
  });

  test('As a user, I should be able to use forms on mobile devices', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 }); // iPhone SE
    
    const findOwnersPage = new FindOwnersPage(page);
    await findOwnersPage.goto();
    
    // Test form interaction on mobile
    expect(await findOwnersPage.isSearchFormVisible()).toBe(true);
    await findOwnersPage.searchByLastName('test');
    
    // Form should work regardless of viewport size
    await page.waitForLoadState('networkidle');
  });

  test('As a user, I should see properly formatted tables on different screen sizes', async ({ page }) => {
    const veterinariansPage = new VeterinariansPage(page);
    
    for (const viewport of viewports) {
      await page.setViewportSize({ width: viewport.width, height: viewport.height });
      
      await veterinariansPage.goto();
      await veterinariansPage.waitForLoad();
      
      expect(await veterinariansPage.isVetsTableVisible()).toBe(true);
      
      // Verify table has proper structure
      const table = page.locator('table.table-striped');
      expect(await table.isVisible()).toBe(true);
      
      // Check that table content is accessible
      const vetCount = await veterinariansPage.getVetCount();
      expect(vetCount).toBeGreaterThan(0);
    }
  });
});