import { test, expect } from '@playwright/test';
import { HomePage } from './pages/HomePage.js';

test.describe('Homepage and Navigation Tests', () => {
  let homePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
    await homePage.goto();
  });

  test('As a visitor, I want to view the homepage, so that I can see the Pet Clinic welcome message', async ({ page }) => {
    await homePage.verifyPageStructure();
    await expect(page).toHaveTitle(/PetClinic/);
    await expect(homePage.welcomeHeading).toBeVisible();
    await expect(homePage.petImage).toBeVisible();
    await expect(homePage.springLogo).toBeVisible();
  });

  test('As a visitor, I want to see the main navigation menu, so that I can access different sections of the application', async ({ page }) => {
    await homePage.verifyNavigationPresent();
    
    // Verify all navigation links are present and clickable
    await expect(homePage.homeLink).toBeVisible();
    await expect(homePage.findOwnersLink).toBeVisible();
    await expect(homePage.veterinariansLink).toBeVisible();
    await expect(homePage.errorLink).toBeVisible();
  });

  test('As a visitor, I want to navigate to Find Owners from the homepage, so that I can search for pet owners', async ({ page }) => {
    await homePage.navigateToFindOwners();
    await expect(page).toHaveURL(/\/owners\/find/);
    await expect(page.getByRole('heading', { name: 'Find Owners' })).toBeVisible();
  });

  test('As a visitor, I want to navigate to Veterinarians from the homepage, so that I can view veterinarian information', async ({ page }) => {
    await homePage.navigateToVeterinarians();
    await expect(page).toHaveURL(/\/vets\.html/);
    await expect(page.getByRole('heading', { name: 'Veterinarians' })).toBeVisible();
  });

  test('As a visitor, I want to navigate back to home using the Home link, so that I can return to the main page', async ({ page }) => {
    // Navigate away first
    await homePage.navigateToFindOwners();
    await expect(page).toHaveURL(/\/owners\/find/);
    
    // Navigate back to home
    await homePage.navigateToHome();
    await expect(page).toHaveURL(/localhost:8080\/$/);
    await expect(homePage.welcomeHeading).toBeVisible();
  });

  test('As a visitor, I want to navigate using the browser back button, so that I can use standard browser navigation', async ({ page }) => {
    // Navigate to Find Owners
    await homePage.navigateToFindOwners();
    await expect(page).toHaveURL(/\/owners\/find/);
    
    // Use browser back button
    await page.goBack();
    await expect(page).toHaveURL(/localhost:8080\/$/);
    await expect(homePage.welcomeHeading).toBeVisible();
  });

  test('As a visitor, I want to see the Pet Clinic logo, so that I can identify the application branding', async ({ page }) => {
    await expect(homePage.logo).toBeVisible();
    
    // Test logo click functionality
    await homePage.navigateToFindOwners(); // Go to different page first
    await homePage.logo.click();
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/localhost:8080\/$/);
  });

  test('As a visitor, I want the page to load quickly, so that I have a good user experience', async ({ page }) => {
    const startTime = Date.now();
    await homePage.goto();
    const loadTime = Date.now() - startTime;
    
    // Page should load within 5 seconds
    expect(loadTime).toBeLessThan(5000);
    
    // All critical elements should be visible
    await homePage.verifyHomePageContent();
  });

  test('As a visitor, I want the page to be responsive, so that I can view it on different screen sizes', async ({ page }) => {
    // Test mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await homePage.goto();
    await expect(homePage.welcomeHeading).toBeVisible();
    
    // Test tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await homePage.goto();
    await expect(homePage.welcomeHeading).toBeVisible();
    
    // Test desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 });
    await homePage.goto();
    await expect(homePage.welcomeHeading).toBeVisible();
  });
});