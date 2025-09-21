import { test, expect } from '@playwright/test';
import { OwnerPage } from './pages/OwnerPage';
import { PetPage } from './pages/PetPage';

test.describe('As a user managing pets', () => {

  test('As a user, I want to add a new pet to an owner, so that I can register their pets in the system', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // First create an owner
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Alice',
      lastName: 'Johnson',
      address: '123 Pet Street',
      city: 'Pet City',
      telephone: '5551234567'
    };
    await ownerPage.createOwner(ownerData);

    // Get owner ID from URL
    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];
    expect(ownerId).toBeTruthy();

    // Add a pet
    await petPage.gotoAddPet(ownerId!);
    await petPage.assertAddPetFormLoaded();

    const petData = {
      name: 'Fluffy',
      birthDate: '2023-01-15',
      type: 'cat'
    };

    await petPage.addPet(petData);

    // Should redirect to owner details page
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await petPage.assertPetCreated(petData.name);
  });

  test('As a user, I want to see available pet types, so that I can choose the appropriate type for the pet', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create an owner first
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Bob',
      lastName: 'Smith',
      address: '456 Animal Ave',
      city: 'Animal City',
      telephone: '5559876543'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Go to add pet form
    await petPage.gotoAddPet(ownerId!);
    await petPage.assertPetTypesAvailable();
  });

  test('As a user, I want to validate pet form fields, so that I cannot submit invalid pet data', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create an owner first
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Charlie',
      lastName: 'Brown',
      address: '789 Dog Street',
      city: 'Dog City',
      telephone: '5551112222'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Try to add pet with missing data
    await petPage.gotoAddPet(ownerId!);
    
    const incompletePetData = {
      name: '', // Missing name
      birthDate: '2023-01-15',
      type: 'dog'
    };

    await petPage.addPet(incompletePetData);
    
    // Should show validation errors
    await petPage.assertFormValidationErrors();
  });

  test('As a user, I want to edit pet information, so that I can update pet details when needed', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Diana',
      lastName: 'Prince',
      address: '321 Hero Street',
      city: 'Hero City',
      telephone: '5553334444'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Add a pet
    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'Wonder',
      birthDate: '2022-06-01',
      type: 'dog'
    };
    await petPage.addPet(petData);

    // Find the pet ID and edit it
    // Note: In a real scenario, we'd need to extract the pet ID from the page
    // For this test, we'll simulate it
    await expect(page.locator('a:has-text("Edit Pet")')).toBeVisible();
    await page.locator('a:has-text("Edit Pet")').click();

    await petPage.assertEditPetFormLoaded();

    // Update pet information
    const updatedPetData = {
      name: 'Wonder Woman',
      birthDate: '2022-06-01',
      type: 'dog'
    };

    await petPage.updatePet(updatedPetData);

    // Should redirect back to owner details
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await petPage.assertPetUpdated(updatedPetData.name);
  });

  test('As a user, I want to validate pet birth dates, so that only realistic dates are accepted', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create an owner first
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Eva',
      lastName: 'Green',
      address: '555 Time Street',
      city: 'Time City',
      telephone: '5555556666'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Test with future date
    await petPage.gotoAddPet(ownerId!);
    
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);
    const futureDateString = futureDate.toISOString().split('T')[0];

    const invalidPetData = {
      name: 'TimeTraveler',
      birthDate: futureDateString,
      type: 'cat'
    };

    await petPage.addPet(invalidPetData);
    
    // Should either show validation error or reject the date
    const hasError = await petPage.formErrors.count() > 0;
    const stayedOnForm = page.url().includes('/pets/new');
    
    expect(hasError || stayedOnForm).toBeTruthy();
  });

  test('As a user, I want to see pets listed on owner details page, so that I can view all pets belonging to an owner', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create owner
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Frank',
      lastName: 'Miller',
      address: '777 Pet Paradise',
      city: 'Pet Paradise',
      telephone: '5557778888'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Add multiple pets
    const pets = [
      { name: 'Max', birthDate: '2020-03-15', type: 'dog' },
      { name: 'Luna', birthDate: '2021-07-22', type: 'cat' },
      { name: 'Buddy', birthDate: '2019-11-10', type: 'hamster' }
    ];

    for (const pet of pets) {
      await petPage.gotoAddPet(ownerId!);
      await petPage.addPet(pet);
      
      // Should be back on owner details page
      await expect(page).toHaveURL(`/owners/${ownerId}`);
    }

    // All pets should be visible on owner details page
    for (const pet of pets) {
      await expect(page.locator(`text=${pet.name}`)).toBeVisible();
    }
  });

  test('As a user, I want to handle special characters in pet names, so that names with accents and symbols work correctly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create owner
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Gabriel',
      lastName: 'García',
      address: '888 International St',
      city: 'Global City',
      telephone: '5558889999'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Add pet with special characters
    await petPage.gotoAddPet(ownerId!);
    const specialPetData = {
      name: 'Piñata-Ñoño',
      birthDate: '2022-12-25',
      type: 'bird'
    };

    await petPage.addPet(specialPetData);

    // Should successfully create pet
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await petPage.assertPetCreated(specialPetData.name);
  });

  test('As a user, I want to navigate efficiently between pet-related pages, so that I can manage pets quickly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);

    // Create owner
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Helen',
      lastName: 'Troy',
      address: '999 Navigation Blvd',
      city: 'Nav City',
      telephone: '5559990000'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Test navigation flow
    await ownerPage.addNewPetButton.click();
    await expect(page).toHaveURL(`/owners/${ownerId}/pets/new`);
    await petPage.assertAddPetFormLoaded();

    // Go back to owner details (via navigation or back button)
    await page.goBack();
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await ownerPage.assertOwnerDetailsPageLoaded();
  });

});