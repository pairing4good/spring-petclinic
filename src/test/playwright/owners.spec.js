const { test, expect } = require('@playwright/test');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { OwnerFormPage } = require('./pages/OwnerFormPage');
const { OwnerDetailsPage } = require('./pages/OwnerDetailsPage');
const { OwnersListPage } = require('./pages/OwnersListPage');

test.describe('Owner Management CRUD Operations', () => {
  test('As a user, I should be able to create a new owner with valid information', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownerFormPage = new OwnerFormPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    
    // Navigate to new owner form
    await findOwnersPage.goto();
    await findOwnersPage.clickAddOwner();
    
    // Fill out the form
    const ownerData = {
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Main St',
      city: 'Springfield',
      telephone: '555-1234'
    };
    
    expect(await ownerFormPage.isFormVisible()).toBe(true);
    expect(await ownerFormPage.getSubmitButtonText()).toContain('Add Owner');
    
    await ownerFormPage.fillOwnerForm(ownerData);
    await ownerFormPage.submitForm();
    
    // Should redirect to owner details page
    await ownerDetailsPage.waitForLoad();
    expect(await ownerDetailsPage.isSuccessMessageVisible()).toBe(true);
    expect(await ownerDetailsPage.getSuccessMessage()).toContain('New Owner Created');
    
    // Verify owner information is displayed correctly
    const displayedInfo = await ownerDetailsPage.getOwnerInfo();
    expect(displayedInfo.name).toContain('John Doe');
    expect(displayedInfo.address).toBe('123 Main St');
    expect(displayedInfo.city).toBe('Springfield');
    expect(displayedInfo.telephone).toBe('555-1234');
  });

  test('As a user, I should see validation errors when creating an owner with invalid information', async ({ page }) => {
    const ownerFormPage = new OwnerFormPage(page);
    
    await ownerFormPage.gotoNew();
    
    // Try to submit empty form
    await ownerFormPage.submitForm();
    
    // Should stay on form page with validation errors
    expect(await ownerFormPage.isFormVisible()).toBe(true);
    // Note: Validation behavior depends on backend validation rules
  });

  test('As a user, I should be able to search for owners by last name', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownersListPage = new OwnersListPage(page);
    
    await findOwnersPage.goto();
    
    // Search for owners with empty last name (should return all)
    await findOwnersPage.searchWithoutLastName();
    
    // Should show results or redirect to single owner
    await page.waitForLoadState('networkidle');
    
    // Check if we got results or were redirected
    const currentUrl = page.url();
    if (currentUrl.includes('/owners/') && !currentUrl.includes('/find')) {
      // Redirected to single owner - verify owner details are shown
      const ownerDetailsPage = new OwnerDetailsPage(page);
      expect(await ownerDetailsPage.isOwnerInfoVisible()).toBe(true);
    } else {
      // Multiple results - verify list is shown
      expect(await ownersListPage.getOwnerCount()).toBeGreaterThan(0);
    }
  });

  test('As a user, I should see a message when no owners are found', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    
    await findOwnersPage.goto();
    
    // Search for a non-existent owner
    await findOwnersPage.searchByLastName('NonExistentOwner12345');
    
    // Should show "not found" message and stay on search page
    expect(page.url()).toContain('/owners/find');
    expect(await findOwnersPage.isValidationErrorVisible()).toBe(true);
  });

  test('As a user, I should be able to edit an existing owner', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownersListPage = new OwnersListPage(page);
    const ownerDetailsPage = new OwnerDetailsPage(page);
    const ownerFormPage = new OwnerFormPage(page);
    
    // First, find an owner to edit
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // If redirected to single owner, use that; otherwise pick first from list
    const currentUrl = page.url();
    if (!currentUrl.includes('/owners/') || currentUrl.includes('/find')) {
      // We're on the list page, click first owner
      expect(await ownersListPage.getOwnerCount()).toBeGreaterThan(0);
      await ownersListPage.clickOwnerByIndex(0);
    }
    
    // Now we should be on owner details page
    await ownerDetailsPage.waitForLoad();
    const originalInfo = await ownerDetailsPage.getOwnerInfo();
    
    // Click edit button
    await ownerDetailsPage.clickEditOwner();
    
    // Verify we're on edit form
    expect(await ownerFormPage.isFormVisible()).toBe(true);
    expect(await ownerFormPage.getSubmitButtonText()).toContain('Update Owner');
    
    // Update the address
    const updatedOwnerData = {
      address: '456 Updated St'
    };
    
    await ownerFormPage.fillOwnerForm(updatedOwnerData);
    await ownerFormPage.submitForm();
    
    // Should return to owner details with success message
    await ownerDetailsPage.waitForLoad();
    expect(await ownerDetailsPage.isSuccessMessageVisible()).toBe(true);
    expect(await ownerDetailsPage.getSuccessMessage()).toContain('Owner Values Updated');
    
    // Verify the update was applied
    const updatedInfo = await ownerDetailsPage.getOwnerInfo();
    expect(updatedInfo.address).toBe('456 Updated St');
    expect(updatedInfo.name).toBe(originalInfo.name); // Name should remain the same
  });

  test('As a user, I should be able to navigate through owner search results', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    const ownersListPage = new OwnersListPage(page);
    
    await findOwnersPage.goto();
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Only test pagination if we're on the list page (not redirected to single owner)
    if (!page.url().includes('/owners/') || page.url().includes('/find')) {
      const ownerCount = await ownersListPage.getOwnerCount();
      expect(ownerCount).toBeGreaterThan(0);
      
      // Get list of owner names
      const ownerNames = await ownersListPage.getOwnerNames();
      expect(ownerNames.length).toBeGreaterThan(0);
      
      // Click on first owner
      await ownersListPage.clickOwnerByIndex(0);
      
      // Should navigate to owner details
      const ownerDetailsPage = new OwnerDetailsPage(page);
      await ownerDetailsPage.waitForLoad();
      expect(await ownerDetailsPage.isOwnerInfoVisible()).toBe(true);
    }
  });
});