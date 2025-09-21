import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class PetPage extends BasePage {
  
  constructor(page: Page) {
    super(page);
  }

  // Add Pet form elements
  get petNameInput(): Locator {
    return this.page.locator('input[id="name"]');
  }

  get birthDateInput(): Locator {
    return this.page.locator('input[id="birthDate"]');
  }

  get petTypeSelect(): Locator {
    return this.page.locator('select[id="type"]');
  }

  get addPetButton(): Locator {
    return this.page.locator('button[type="submit"]');
  }

  get updatePetButton(): Locator {
    return this.page.locator('button:has-text("Update Pet")');
  }

  // Pet details elements
  get petInformation(): Locator {
    return this.page.locator('table.table-striped');
  }

  get editPetButton(): Locator {
    return this.page.locator('a:has-text("Edit Pet")');
  }

  get addVisitButton(): Locator {
    return this.page.locator('a:has-text("Add Visit")');
  }

  // Error elements
  get errorMessages(): Locator {
    return this.page.locator('.help-inline');
  }

  get formErrors(): Locator {
    return this.page.locator('.has-error');
  }

  // Actions
  async gotoAddPet(ownerId: string): Promise<void> {
    await this.page.goto(`/owners/${ownerId}/pets/new`);
    await this.page.waitForLoadState('networkidle');
  }

  async gotoEditPet(ownerId: string, petId: string): Promise<void> {
    await this.page.goto(`/owners/${ownerId}/pets/${petId}/edit`);
    await this.page.waitForLoadState('networkidle');
  }

  async addPet(petData: {
    name: string;
    birthDate: string;
    type: string;
  }): Promise<void> {
    await this.petNameInput.fill(petData.name);
    await this.birthDateInput.fill(petData.birthDate);
    await this.petTypeSelect.selectOption(petData.type);
    await this.addPetButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async updatePet(petData: Partial<{
    name: string;
    birthDate: string;
    type: string;
  }>): Promise<void> {
    if (petData.name) await this.petNameInput.fill(petData.name);
    if (petData.birthDate) await this.birthDateInput.fill(petData.birthDate);
    if (petData.type) await this.petTypeSelect.selectOption(petData.type);
    await this.updatePetButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async getPetTypes(): Promise<string[]> {
    const options = await this.petTypeSelect.locator('option').allTextContents();
    return options.filter(option => option.trim() !== '');
  }

  // Assertions
  async assertAddPetFormLoaded(): Promise<void> {
    await expect(this.petNameInput).toBeVisible();
    await expect(this.birthDateInput).toBeVisible();
    await expect(this.petTypeSelect).toBeVisible();
    await expect(this.addPetButton).toBeVisible();
  }

  async assertEditPetFormLoaded(): Promise<void> {
    await expect(this.petNameInput).toBeVisible();
    await expect(this.birthDateInput).toBeVisible();
    await expect(this.petTypeSelect).toBeVisible();
    await expect(this.updatePetButton).toBeVisible();
  }

  async assertPetTypesAvailable(): Promise<void> {
    const petTypes = await this.getPetTypes();
    expect(petTypes.length).toBeGreaterThan(0);
    
    // Common pet types should be available
    const expectedTypes = ['cat', 'dog', 'bird', 'hamster', 'snake', 'lizard'];
    for (const type of expectedTypes) {
      expect(petTypes.some(t => t.toLowerCase().includes(type))).toBeTruthy();
    }
  }

  async assertFormValidationErrors(): Promise<void> {
    await expect(this.formErrors).toHaveCount({ min: 1 });
  }

  async assertPetCreated(petName: string): Promise<void> {
    await expect(this.page.locator(`text=${petName}`)).toBeVisible();
  }

  async assertPetUpdated(petName: string): Promise<void> {
    await expect(this.page.locator(`text=${petName}`)).toBeVisible();
  }
}