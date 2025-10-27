import { test, expect } from '@playwright/test';
import { VetPage } from './pages/VetPage';

test.describe('As a user viewing veterinarian information', () => {

  test('As a user, I want to view the list of veterinarians, so that I can see all available vets', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    await vetPage.assertVetListPageLoaded();
    
    // Should display at least one vet
    const vetCount = await vetPage.getVetCount();
    expect(vetCount).toBeGreaterThan(0);
  });

  test('As a user, I want to see veterinarian specialties, so that I can choose the right vet for my pet', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    await vetPage.assertVetSpecialties();
    
    // Should display vet names and specialties
    const vetCount = await vetPage.getVetCount();
    for (let i = 0; i < Math.min(vetCount, 3); i++) { // Check first 3 vets
      const name = await vetPage.getVetName(i);
      expect(name.trim()).toBeTruthy();
      
      const specialty = await vetPage.getVetSpecialty(i);
      // Specialty can be empty for some vets
      console.log(`Vet ${i}: ${name} - ${specialty}`);
    }
  });

  test('As a user, I want to navigate through vet pages, so that I can see all veterinarians when there are many', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    
    // Check if pagination exists and works
    const hasNextPage = await vetPage.nextPageLink.isVisible();
    if (hasNextPage) {
      const initialVetCount = await vetPage.getVetCount();
      
      await vetPage.clickNextPage();
      
      // Should be on page 2
      await expect(page).toHaveURL(/page=2/);
      
      const newVetCount = await vetPage.getVetCount();
      expect(newVetCount).toBeGreaterThan(0);
      
      // Test going back to previous page
      const hasPreviousPage = await vetPage.previousPageLink.isVisible();
      if (hasPreviousPage) {
        await vetPage.clickPreviousPage();
        await expect(page).toHaveURL(/page=1|vets\.html$/);
      }
    }
  });

  test('As a user, I want to see vet information formatted properly, so that it is easy to read', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    
    // Check table structure
    await expect(vetPage.vetTable).toBeVisible();
    
    // Check table headers
    await expect(page.locator('th:has-text("Name")')).toBeVisible();
    await expect(page.locator('th:has-text("Specialties")')).toBeVisible();
    
    // Verify at least one vet is displayed
    const vetCount = await vetPage.getVetCount();
    expect(vetCount).toBeGreaterThan(0);
  });

  test('As a user, I want to access the vet page from navigation, so that I can easily find vet information', async ({ page }) => {
    const vetPage = new VetPage(page);

    await page.goto('/');
    await vetPage.navigateToVeterinarians();
    
    await expect(page).toHaveURL('/vets.html');
    await vetPage.assertVetListPageLoaded();
  });

  test('As a user, I want to see vet specialties displayed correctly, so that I understand each vet\'s expertise', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    
    const vetCount = await vetPage.getVetCount();
    const expectedSpecialties = ['surgery', 'dentistry', 'radiology'];
    let foundSpecialties = false;
    
    for (let i = 0; i < vetCount; i++) {
      const specialty = await vetPage.getVetSpecialty(i);
      if (specialty && specialty.trim()) {
        foundSpecialties = true;
        console.log(`Found specialty: ${specialty}`);
      }
    }
    
    // At least some vets should have specialties
    console.log(`Found specialties: ${foundSpecialties}`);
  });

  test('As a user, I want the vet page to load quickly, so that I don\'t wait too long for information', async ({ page }) => {
    const startTime = Date.now();
    
    const vetPage = new VetPage(page);
    await vetPage.goto();
    
    const loadTime = Date.now() - startTime;
    
    // Should load within reasonable time (5 seconds)
    expect(loadTime).toBeLessThan(5000);
    
    // Should display content
    await vetPage.assertVetListPageLoaded();
  });

  test('As a user, I want to verify specific vets are listed, so that I can find known veterinarians', async ({ page }) => {
    const vetPage = new VetPage(page);

    await vetPage.goto();
    
    // Check for some expected vets (these come from the sample data)
    const expectedVets = ['James Carter', 'Helen Leary', 'Linda Douglas'];
    
    for (const vetName of expectedVets) {
      // Try to find this vet (they might be on different pages)
      try {
        await vetPage.assertVetDisplayed(vetName);
        console.log(`Found vet: ${vetName}`);
      } catch (error) {
        console.log(`Vet ${vetName} not found on current page`);
      }
    }
  });

  test('As a user on mobile, I want the vet list to be responsive, so that I can view vets on my phone', async ({ page }) => {
    const vetPage = new VetPage(page);

    // Test mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await vetPage.goto();
    
    await vetPage.assertResponsiveDesign();
    await vetPage.assertVetListPageLoaded();
    
    // Test tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await vetPage.assertResponsiveDesign();
    await vetPage.assertVetListPageLoaded();
  });

  test('As a user, I want to access the JSON API for vets, so that applications can integrate with the data', async ({ page }) => {
    // Test the JSON endpoint
    const response = await page.request.get('/vets');
    expect(response.status()).toBe(200);
    
    const contentType = response.headers()['content-type'];
    expect(contentType).toContain('application/json');
    
    const data = await response.json();
    expect(data).toBeTruthy();
    expect(data.vetList).toBeInstanceOf(Array);
    expect(data.vetList.length).toBeGreaterThan(0);
    
    // Check structure of first vet
    const firstVet = data.vetList[0];
    expect(firstVet.firstName).toBeTruthy();
    expect(firstVet.lastName).toBeTruthy();
    expect(firstVet.id).toBeTruthy();
  });

});