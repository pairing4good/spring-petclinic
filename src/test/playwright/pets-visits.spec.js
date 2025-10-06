const { test, expect } = require('@playwright/test');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { OwnerDetailsPage } = require('./pages/OwnerDetailsPage');
const { PetFormPage } = require('./pages/PetFormPage');
const { VisitFormPage } = require('./pages/VisitFormPage');

test.describe('Pet and Visit Management', () => {
  test('As a user, I should be able to add a new pet to an existing owner', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const petFormPage = new PetFormPage(page);
    
    // Find an owner first
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Navigate to owner details
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      // We're on the list page, click first owner
      const ownersListPage = require('./pages/OwnersListPage').OwnersListPage;
      const listPage = new ownersListPage(page);
      await listPage.clickOwnerByIndex(0);
    }
    
    // Now on owner details, add a new pet
    await ownerDetailsPage.waitForLoad();
    const initialPetCount = await ownerDetailsPage.getPetCount();
    
    await ownerDetailsPage.clickAddNewPet();
    
    // Fill pet form
    expect(await petFormPage.isFormVisible()).toBe(true);
    expect(await petFormPage.getFormTitle()).toContain('Pet');
    
    // Get available pet types
    const petTypes = await petFormPage.getPetTypes();
    expect(petTypes.length).toBeGreaterThan(0);
    
    const petData = {
      name: 'Fluffy',
      birthDate: '2020-01-01',
      type: petTypes[0] // Use first available type
    };
    
    await petFormPage.fillPetForm(petData);
    await petFormPage.submitForm();
    
    // Should return to owner details with new pet
    await ownerDetailsPage.waitForLoad();
    expect(await ownerDetailsPage.isSuccessMessageVisible()).toBe(true);
    
    const newPetCount = await ownerDetailsPage.getPetCount();
    expect(newPetCount).toBe(initialPetCount + 1);
    
    // Verify pet appears in the list
    const pets = await ownerDetailsPage.getPets();
    const addedPet = pets.find(pet => pet.name === 'Fluffy');
    expect(addedPet).toBeTruthy();
  });

  test('As a user, I should see validation errors when adding a pet with invalid data', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const petFormPage = new PetFormPage(page);
    
    // Find an owner first
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Navigate to owner details
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      const ownersListPage = require('./pages/OwnersListPage').OwnersListPage;
      const listPage = new ownersListPage(page);
      await listPage.clickOwnerByIndex(0);
    }
    
    await ownerDetailsPage.clickAddNewPet();
    
    // Try to submit empty form
    await petFormPage.submitForm();
    
    // Should stay on form with validation errors
    expect(await petFormPage.isFormVisible()).toBe(true);
  });

  test('As a user, I should be able to add a visit to a pet', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const visitFormPage = new VisitFormPage(page);
    
    // Find an owner with pets
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Navigate to owner details
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      const ownersListPage = require('./pages/OwnersListPage').OwnersListPage;
      const listPage = new ownersListPage(page);
      await listPage.clickOwnerByIndex(0);
    }
    
    await ownerDetailsPage.waitForLoad();
    
    // Check if owner has pets, if not, add one first
    let petCount = await ownerDetailsPage.getPetCount();
    if (petCount === 0) {
      // Add a pet first
      const petFormPage = new PetFormPage(page);
      await ownerDetailsPage.clickAddNewPet();
      
      const petTypes = await petFormPage.getPetTypes();
      const petData = {
        name: 'TestPet',
        birthDate: '2020-01-01',
        type: petTypes[0]
      };
      
      await petFormPage.fillPetForm(petData);
      await petFormPage.submitForm();
      await ownerDetailsPage.waitForLoad();
      petCount = 1;
    }
    
    expect(petCount).toBeGreaterThan(0);
    
    // Get the first pet and add a visit
    const pets = await ownerDetailsPage.getPets();
    const petName = pets[0].name;
    
    // Find and click the "Add Visit" link for this pet
    await page.locator(`tr:has-text("${petName}") a:has-text("Add Visit")`).click();
    
    // Fill visit form
    expect(await visitFormPage.isFormVisible()).toBe(true);
    expect(await visitFormPage.getFormTitle()).toContain('Visit');
    
    const visitData = {
      date: '2023-01-01',
      description: 'Regular checkup'
    };
    
    await visitFormPage.fillVisitForm(visitData);
    await visitFormPage.submitForm();
    
    // Should return to owner details
    await ownerDetailsPage.waitForLoad();
    expect(await ownerDetailsPage.isSuccessMessageVisible()).toBe(true);
  });

  test('As a user, I should see validation errors when adding a visit with invalid data', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const visitFormPage = new VisitFormPage(page);
    
    // Find an owner with pets
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Navigate to owner details
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      const ownersListPage = require('./pages/OwnersListPage').OwnersListPage;
      const listPage = new ownersListPage(page);
      await listPage.clickOwnerByIndex(0);
    }
    
    await ownerDetailsPage.waitForLoad();
    
    // Ensure we have a pet to work with
    let petCount = await ownerDetailsPage.getPetCount();
    if (petCount === 0) {
      // Add a pet first
      const petFormPage = new PetFormPage(page);
      await ownerDetailsPage.clickAddNewPet();
      
      const petTypes = await petFormPage.getPetTypes();
      const petData = {
        name: 'TestPet',
        birthDate: '2020-01-01',
        type: petTypes[0]
      };
      
      await petFormPage.fillPetForm(petData);
      await petFormPage.submitForm();
      await ownerDetailsPage.waitForLoad();
    }
    
    const pets = await ownerDetailsPage.getPets();
    const petName = pets[0].name;
    
    // Click Add Visit for the pet
    await page.locator(`tr:has-text("${petName}") a:has-text("Add Visit")`).click();
    
    // Try to submit empty visit form
    await visitFormPage.submitForm();
    
    // Should stay on form with validation errors or handle gracefully
    expect(await visitFormPage.isFormVisible()).toBe(true);
  });

  test('As a user, I should see pet type options when adding a new pet', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const petFormPage = new PetFormPage(page);
    
    // Find an owner
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Navigate to owner details
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      const ownersListPage = require('./pages/OwnersListPage').OwnersListPage;
      const listPage = new ownersListPage(page);
      await listPage.clickOwnerByIndex(0);
    }
    
    await ownerDetailsPage.clickAddNewPet();
    
    // Check pet types are available
    const petTypes = await petFormPage.getPetTypes();
    expect(petTypes.length).toBeGreaterThan(0);
    
    // Common pet types that should be available
    const expectedTypes = ['dog', 'cat', 'bird', 'hamster', 'lizard', 'snake'];
    const hasCommonTypes = expectedTypes.some(type => 
      petTypes.some(availableType => 
        availableType.toLowerCase().includes(type)
      )
    );
    expect(hasCommonTypes).toBe(true);
  });
});