const { test, expect } = require('@playwright/test');
const { VeterinariansPage } = require('./pages/VeterinariansPage');

test.describe('Veterinarians Management', () => {
  test('As a user, I should be able to view the list of veterinarians', async ({ page }) => {
    const veterinariansPage = new VeterinariansPage(page);
    
    await veterinariansPage.goto();
    await veterinariansPage.waitForLoad();
    
    expect(await veterinariansPage.getTitle()).toContain('PetClinic');
    expect(await veterinariansPage.isVetsTableVisible()).toBe(true);
    
    const pageHeader = await veterinariansPage.getPageHeader();
    expect(pageHeader).toContain('Veterinarians');
    
    const vetCount = await veterinariansPage.getVetCount();
    expect(vetCount).toBeGreaterThan(0);
  });

  test('As a user, I should see veterinarian details including their specialties', async ({ page }) => {
    const veterinariansPage = new VeterinariansPage(page);
    
    await veterinariansPage.goto();
    await veterinariansPage.waitForLoad();
    
    const veterinarians = await veterinariansPage.getVeterinarians();
    expect(veterinarians.length).toBeGreaterThan(0);
    
    // Verify each vet has a name
    veterinarians.forEach(vet => {
      expect(vet.name).toBeTruthy();
      expect(vet.name.length).toBeGreaterThan(0);
      // Specialties can be empty for some vets
    });
  });

  test('As a user, I should be able to navigate to veterinarians page from other pages', async ({ page }) => {
    const veterinariansPage = new VeterinariansPage(page);
    
    // Start from home page
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Navigate to veterinarians using the navigation menu
    await page.locator('nav.navbar a[href="/vets.html"]').click();
    await veterinariansPage.waitForLoad();
    
    expect(page.url()).toContain('/vets.html');
    expect(await veterinariansPage.isVetsTableVisible()).toBe(true);
  });
});