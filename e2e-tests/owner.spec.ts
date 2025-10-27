import { test, expect } from '@playwright/test';
import { OwnerPage } from './pages/OwnerPage';

test.describe('As a user managing pet owners', () => {

  test('As a user, I want to find owners by last name, so that I can locate specific owners quickly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoFindOwners();
    await ownerPage.assertFindOwnersPageLoaded();

    // Search for existing owner
    await ownerPage.searchOwnerByLastName('Franklin');
    
    // Should show owner details or list
    await expect(page).toHaveURL(/\/owners/);
  });

  test('As a user, I want to view all owners, so that I can see the complete list of pet owners', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoFindOwners();
    await ownerPage.searchAllOwners();
    
    // Should display owners list
    await ownerPage.assertOwnerListDisplayed();
  });

  test('As a user, I want to search for non-existent owners, so that I get appropriate feedback', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoFindOwners();
    await ownerPage.searchOwnerByLastName('NonExistentOwner12345');
    
    // Should show "not found" message
    await ownerPage.assertNoOwnersFound();
  });

  test('As a user, I want to create a new owner, so that I can add new pet owners to the system', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoNewOwner();
    await ownerPage.assertNewOwnerFormLoaded();

    const ownerData = {
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Main Street',
      city: 'Springfield',
      telephone: '5551234567'
    };

    await ownerPage.createOwner(ownerData);
    
    // Should redirect to owner details page
    await expect(page).toHaveURL(/\/owners\/\d+/);
    await ownerPage.assertOwnerCreated(ownerData.firstName, ownerData.lastName);
  });

  test('As a user, I want to validate owner form fields, so that I cannot submit invalid data', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoNewOwner();
    
    // Try to submit form with missing required fields
    const incompleteData = {
      firstName: 'John',
      lastName: 'Doe',
      address: '', // Missing address
      city: 'Springfield',
      telephone: '' // Missing telephone
    };

    await ownerPage.createOwner(incompleteData);
    
    // Should show validation errors
    await ownerPage.assertFormValidationErrors();
  });

  test('As a user, I want to edit owner information, so that I can update owner details when needed', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    // First create an owner
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Jane',
      lastName: 'Smith',
      address: '456 Oak Avenue',
      city: 'Springfield',
      telephone: '5559876543'
    };
    await ownerPage.createOwner(ownerData);

    // Get the owner ID from URL
    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];
    expect(ownerId).toBeTruthy();

    // Navigate to edit form
    await ownerPage.gotoEditOwner(ownerId!);
    
    // Update owner information
    const updatedData = {
      firstName: 'Jane',
      lastName: 'Johnson', // Changed last name
      address: '789 Pine Street', // Changed address
      city: 'Springfield',
      telephone: '5559876543'
    };
    
    await ownerPage.updateOwner(updatedData);
    
    // Should redirect back to owner details
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await expect(page.locator('text=Johnson')).toBeVisible();
  });

  test('As a user, I want to view owner details, so that I can see complete owner information and their pets', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    // Create an owner first
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Bob',
      lastName: 'Wilson',
      address: '321 Elm Street',
      city: 'Springfield',
      telephone: '5551112222'
    };
    await ownerPage.createOwner(ownerData);

    // Should be on owner details page
    await ownerPage.assertOwnerDetailsPageLoaded();
    
    // Should show owner information
    await expect(page.locator('text=Bob Wilson')).toBeVisible();
    await expect(page.locator('text=321 Elm Street')).toBeVisible();
    await expect(page.locator('text=Springfield')).toBeVisible();
    await expect(page.locator('text=5551112222')).toBeVisible();
  });

  test('As a user, I want to validate telephone number format, so that only valid phone numbers are accepted', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoNewOwner();
    
    // Try with invalid telephone number
    const invalidTelephoneData = {
      firstName: 'Test',
      lastName: 'User',
      address: '123 Test Street',
      city: 'Test City',
      telephone: 'invalid-phone'
    };

    await ownerPage.createOwner(invalidTelephoneData);
    
    // Should show validation error or reject the input
    const hasValidationError = await ownerPage.formErrors.count() > 0;
    const stayedOnForm = page.url().includes('/owners/new');
    
    expect(hasValidationError || stayedOnForm).toBeTruthy();
  });

  test('As a user, I want to navigate between owner-related pages, so that I can efficiently manage owners', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    // Start from find owners page
    await ownerPage.gotoFindOwners();
    await ownerPage.assertFindOwnersPageLoaded();

    // Navigate to add new owner
    await ownerPage.addOwnerButton.click();
    await expect(page).toHaveURL('/owners/new');
    await ownerPage.assertNewOwnerFormLoaded();

    // Go back to find owners
    await ownerPage.navigateToFindOwners();
    await ownerPage.assertFindOwnersPageLoaded();
  });

  test('As a user, I want to handle special characters in owner data, so that names with accents and symbols work correctly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.gotoNewOwner();
    
    // Test with special characters
    const specialCharData = {
      firstName: 'José',
      lastName: "O'Sullivan-García",
      address: '123 Café Street',
      city: 'São Paulo',
      telephone: '5551234567'
    };

    await ownerPage.createOwner(specialCharData);
    
    // Should successfully create owner
    await expect(page).toHaveURL(/\/owners\/\d+/);
    await expect(page.locator('text=José')).toBeVisible();
    await expect(page.locator("text=O'Sullivan-García")).toBeVisible();
  });

});