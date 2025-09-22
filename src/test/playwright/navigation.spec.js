const { test, expect } = require('@playwright/test');
const { HomePage } = require('./pages/HomePage');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { VeterinariansPage } = require('./pages/VeterinariansPage');
const { ErrorPage } = require('./pages/ErrorPage');

test.describe('Navigation and Routing Tests', () => {
  test('As a user, I should be able to navigate to the home page and see the welcome content', async ({ page }) => {
    const homePage = new HomePage(page);
    
    await homePage.goto();
    await homePage.waitForLoad();
    
    expect(await homePage.getTitle()).toContain('PetClinic');
    expect(await homePage.isNavigationMenuVisible()).toBe(true);
    expect(await homePage.isWelcomeHeaderVisible()).toBe(true);
    expect(await homePage.hasCorrectLayout()).toBe(true);
  });

  test('As a user, I should be able to navigate between all main pages using the navigation menu', async ({ page }) => {
    const homePage = new HomePage(page);
    const findOwnersPage = new FindOwnersPage(page);
    const veterinariansPage = new VeterinariansPage(page);
    const errorPage = new ErrorPage(page);
    
    // Start at home page
    await homePage.goto();
    await homePage.waitForLoad();
    
    // Navigate to Find Owners
    await homePage.clickFindOwners();
    await findOwnersPage.waitForLoad();
    expect(page.url()).toContain('/owners/find');
    expect(await findOwnersPage.isSearchFormVisible()).toBe(true);
    
    // Navigate to Veterinarians
    await findOwnersPage.clickVeterinarians();
    await veterinariansPage.waitForLoad();
    expect(page.url()).toContain('/vets.html');
    expect(await veterinariansPage.isVetsTableVisible()).toBe(true);
    
    // Navigate to Error page
    await veterinariansPage.clickError();
    await errorPage.waitForLoad();
    expect(page.url()).toContain('/oups');
    expect(await errorPage.isErrorMessageVisible()).toBe(true);
    
    // Navigate back to Home
    await errorPage.clickHome();
    await homePage.waitForLoad();
    expect(page.url()).toBe('http://localhost:8080/');
    expect(await homePage.isWelcomeHeaderVisible()).toBe(true);
  });

  test('As a user, I should be able to use browser back and forward buttons for navigation', async ({ page }) => {
    const homePage = new HomePage(page);
    const findOwnersPage = new FindOwnersPage(page);
    
    // Navigate through pages
    await homePage.goto();
    await homePage.clickFindOwners();
    await findOwnersPage.waitForLoad();
    
    // Use browser back button
    await page.goBack();
    await homePage.waitForLoad();
    expect(page.url()).toBe('http://localhost:8080/');
    
    // Use browser forward button
    await page.goForward();
    await findOwnersPage.waitForLoad();
    expect(page.url()).toContain('/owners/find');
  });

  test('As a user, I should see a custom error page when accessing the error endpoint', async ({ page }) => {
    const errorPage = new ErrorPage(page);
    
    await errorPage.goto();
    await errorPage.waitForLoad();
    
    expect(await errorPage.isCustomErrorPage()).toBe(true);
    expect(await errorPage.getErrorMessage()).toContain('Something happened...');
    expect(await errorPage.getErrorDescription()).toContain('controller used to showcase');
  });

  test('As a user, I should see a 404 error when accessing a non-existent page', async ({ page }) => {
    await page.goto('/non-existent-page');
    
    // Should get a 404 response or redirect to error page
    const statusCode = page.url().includes('404') || page.url().includes('error');
    expect(statusCode || await page.locator('text=404').isVisible()).toBeTruthy();
  });
});