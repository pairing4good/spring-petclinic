import { test, expect } from '@playwright/test';
import { WelcomePage } from './pages/WelcomePage';

test.describe('As a user visiting the Pet Clinic website', () => {
  
  test('As a visitor, I want to see the welcome page, so that I can understand what the application is about', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
  });

  test('As a visitor, I want to navigate through the main menu, so that I can access different sections of the application', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    await welcomePage.assertNavigation();
    
    // Test navigation to different sections
    await welcomePage.navigateToFindOwners();
    await expect(page).toHaveURL('/owners/find');
    
    await welcomePage.navigateToVeterinarians();
    await expect(page).toHaveURL('/vets.html');
    
    await welcomePage.navigateToHome();
    await expect(page).toHaveURL('/');
  });

  test('As a visitor, I want the website to be responsive, so that I can use it on different devices', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Test desktop viewport
    await page.setViewportSize({ width: 1200, height: 800 });
    await welcomePage.goto();
    await welcomePage.assertResponsiveDesign();
    
    // Test tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await welcomePage.assertResponsiveDesign();
    
    // Test mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await welcomePage.assertResponsiveDesign();
  });

  test('As a visitor, I want to see the error page when something goes wrong, so that I get helpful feedback', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    await welcomePage.goto();
    await welcomePage.navigateToError();
    
    // Should show the error page
    await expect(page).toHaveURL('/oups');
    await expect(page.locator('h2')).toContainText('Something happened...');
  });

});