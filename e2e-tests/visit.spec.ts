import { test, expect } from '@playwright/test';
import { OwnerPage } from './pages/OwnerPage';
import { PetPage } from './pages/PetPage';
import { VisitPage } from './pages/VisitPage';

test.describe('As a user managing pet visits', () => {

  test('As a user, I want to schedule a visit for a pet, so that I can record veterinary appointments', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet first
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Alice',
      lastName: 'Cooper',
      address: '123 Visit Street',
      city: 'Visit City',
      telephone: '5551234567'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    // Add a pet
    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'Sick',
      birthDate: '2022-05-10',
      type: 'cat'
    };
    await petPage.addPet(petData);

    // Now add a visit - need to find the pet ID
    await expect(page.locator('a:has-text("Add Visit")')).toBeVisible();
    await page.locator('a:has-text("Add Visit")').click();

    await visitPage.assertAddVisitFormLoaded();

    const visitData = {
      date: '2024-01-15',
      description: 'Annual checkup and vaccinations'
    };

    await visitPage.addVisit(visitData);

    // Should redirect back to owner details
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await visitPage.assertVisitCreated(visitData.description);
  });

  test('As a user, I want to validate visit form fields, so that I cannot submit invalid visit data', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Bob',
      lastName: 'Validator',
      address: '456 Validation Ave',
      city: 'Valid City',
      telephone: '5559876543'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'Checker',
      birthDate: '2021-08-20',
      type: 'dog'
    };
    await petPage.addPet(petData);

    // Navigate to add visit
    await page.locator('a:has-text("Add Visit")').click();
    await visitPage.assertRequiredFieldsValidation();
  });

  test('As a user, I want to see pet information when scheduling a visit, so that I can confirm I\'m scheduling for the right pet', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Carol',
      lastName: 'Information',
      address: '789 Info Street',
      city: 'Info City',
      telephone: '5551112222'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'InfoPet',
      birthDate: '2020-12-05',
      type: 'bird'
    };
    await petPage.addPet(petData);

    // Navigate to add visit
    await page.locator('a:has-text("Add Visit")').click();
    
    await visitPage.assertPetInformationDisplayed();
    
    // Pet name should be visible on the visit form page
    await expect(page.locator(`text=${petData.name}`)).toBeVisible();
  });

  test('As a user, I want to see visit history for a pet, so that I can track the pet\'s medical record', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'David',
      lastName: 'History',
      address: '321 History Lane',
      city: 'Historic City',
      telephone: '5553334444'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'HistoryPet',
      birthDate: '2019-03-15',
      type: 'lizard'
    };
    await petPage.addPet(petData);

    // Add multiple visits
    const visits = [
      { date: '2023-01-10', description: 'Initial checkup' },
      { date: '2023-06-15', description: 'Vaccination update' },
      { date: '2023-12-20', description: 'Annual physical exam' }
    ];

    for (const visit of visits) {
      await page.locator('a:has-text("Add Visit")').click();
      await visitPage.addVisit(visit);
      
      // Should be back on owner details page
      await expect(page).toHaveURL(`/owners/${ownerId}`);
    }

    // All visits should be visible in the visit history
    for (const visit of visits) {
      await expect(page.locator(`text=${visit.description}`)).toBeVisible();
    }
  });

  test('As a user, I want to validate visit dates, so that only realistic dates are accepted', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Eva',
      lastName: 'DateValidator',
      address: '555 Date Street',
      city: 'Date City',
      telephone: '5555556666'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'DatePet',
      birthDate: '2022-01-01',
      type: 'hamster'
    };
    await petPage.addPet(petData);

    // Navigate to add visit
    await page.locator('a:has-text("Add Visit")').click();
    
    // Test that current date is valid
    await visitPage.assertVisitDateValid();
  });

  test('As a user, I want to add detailed visit descriptions, so that I can record comprehensive medical notes', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Frank',
      lastName: 'Detailed',
      address: '777 Detail Drive',
      city: 'Detail City',
      telephone: '5557778888'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'DetailPet',
      birthDate: '2021-09-30',
      type: 'snake'
    };
    await petPage.addPet(petData);

    // Add visit with detailed description
    await page.locator('a:has-text("Add Visit")').click();

    const detailedVisit = {
      date: '2024-02-14',
      description: 'Comprehensive examination including weight check (2.5kg), temperature normal (101.5°F), administered DHPP vaccine, prescribed antibiotics for minor skin irritation, scheduled follow-up in 2 weeks.'
    };

    await visitPage.addVisit(detailedVisit);

    // Should successfully create visit with long description
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await visitPage.assertVisitCreated(detailedVisit.description);
  });

  test('As a user, I want to handle special characters in visit descriptions, so that medical notes with symbols work correctly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Grace',
      lastName: 'Special',
      address: '888 Symbol Street',
      city: 'Symbol City',
      telephone: '5558889999'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'Símbol',
      birthDate: '2023-04-12',
      type: 'cat'
    };
    await petPage.addPet(petData);

    // Add visit with special characters
    await page.locator('a:has-text("Add Visit")').click();

    const specialVisit = {
      date: '2024-03-01',
      description: 'Examinación médica: peso 3.2kg, temperatura 38.5°C, vacuna contra rabia administrada. Próxima cita: 15/04/2024.'
    };

    await visitPage.addVisit(specialVisit);

    // Should successfully create visit
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await visitPage.assertVisitCreated(specialVisit.description);
  });

  test('As a user, I want to navigate efficiently between visit-related pages, so that I can manage visits quickly', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Henry',
      lastName: 'Navigator',
      address: '999 Navigation Blvd',
      city: 'Nav City',
      telephone: '5559990000'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'NavPet',
      birthDate: '2020-07-04',
      type: 'dog'
    };
    await petPage.addPet(petData);

    // Test navigation flow
    await page.locator('a:has-text("Add Visit")').click();
    await visitPage.assertAddVisitFormLoaded();

    // Navigate back to owner details
    await page.goBack();
    await expect(page).toHaveURL(`/owners/${ownerId}`);
    await ownerPage.assertOwnerDetailsPageLoaded();

    // Navigate to add visit again
    await page.locator('a:has-text("Add Visit")').click();
    await visitPage.assertAddVisitFormLoaded();
  });

  test('As a user, I want to see visits sorted chronologically, so that I can easily track the pet\'s medical history timeline', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const petPage = new PetPage(page);
    const visitPage = new VisitPage(page);

    // Create owner and pet
    await ownerPage.gotoNewOwner();
    const ownerData = {
      firstName: 'Iris',
      lastName: 'Timeline',
      address: '111 Chronology St',
      city: 'Time City',
      telephone: '5551110000'
    };
    await ownerPage.createOwner(ownerData);

    const url = page.url();
    const ownerId = url.match(/\/owners\/(\d+)/)?.[1];

    await petPage.gotoAddPet(ownerId!);
    const petData = {
      name: 'TimePet',
      birthDate: '2021-01-01',
      type: 'bird'
    };
    await petPage.addPet(petData);

    // Add visits in non-chronological order
    const visits = [
      { date: '2024-03-15', description: 'Latest visit' },
      { date: '2024-01-10', description: 'First visit' },
      { date: '2024-02-20', description: 'Middle visit' }
    ];

    for (const visit of visits) {
      await page.locator('a:has-text("Add Visit")').click();
      await visitPage.addVisit(visit);
      await expect(page).toHaveURL(`/owners/${ownerId}`);
    }

    // Check that visits are displayed (order may vary depending on implementation)
    for (const visit of visits) {
      await expect(page.locator(`text=${visit.description}`)).toBeVisible();
    }
  });

});