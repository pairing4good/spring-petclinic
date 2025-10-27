import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class OwnerPage extends BasePage {
  
  constructor(page: Page) {
    super(page);
  }

  // Find Owners page elements
  get lastNameInput(): Locator {
    return this.page.locator('input[name="lastName"]');
  }

  get findOwnerButton(): Locator {
    return this.page.locator('button[type="submit"]');
  }

  get addOwnerButton(): Locator {
    return this.page.locator('a[href="/owners/new"]');
  }

  // Create/Update Owner form elements
  get firstNameInput(): Locator {
    return this.page.locator('input[id="firstName"]');
  }

  get lastNameFormInput(): Locator {
    return this.page.locator('input[id="lastName"]');
  }

  get addressInput(): Locator {
    return this.page.locator('input[id="address"]');
  }

  get cityInput(): Locator {
    return this.page.locator('input[id="city"]');
  }

  get telephoneInput(): Locator {
    return this.page.locator('input[id="telephone"]');
  }

  get submitButton(): Locator {
    return this.page.locator('button[type="submit"]');
  }

  get updateButton(): Locator {
    return this.page.locator('button:has-text("Update Owner")');
  }

  // Owner details page elements
  get editOwnerButton(): Locator {
    return this.page.locator('a:has-text("Edit Owner")');
  }

  get addNewPetButton(): Locator {
    return this.page.locator('a:has-text("Add New Pet")');
  }

  get ownerInformation(): Locator {
    return this.page.locator('table.table-striped');
  }

  // Owner list elements
  get ownersTable(): Locator {
    return this.page.locator('table');
  }

  get ownerLinks(): Locator {
    return this.page.locator('table a[href*="/owners/"]');
  }

  // Error elements
  get errorMessages(): Locator {
    return this.page.locator('.help-inline');
  }

  get formErrors(): Locator {
    return this.page.locator('.has-error');
  }

  // Actions
  async gotoFindOwners(): Promise<void> {
    await this.page.goto('/owners/find');
    await this.page.waitForLoadState('networkidle');
  }

  async gotoNewOwner(): Promise<void> {
    await this.page.goto('/owners/new');
    await this.page.waitForLoadState('networkidle');
  }

  async searchOwnerByLastName(lastName: string): Promise<void> {
    await this.lastNameInput.fill(lastName);
    await this.findOwnerButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async searchAllOwners(): Promise<void> {
    await this.findOwnerButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async createOwner(ownerData: {
    firstName: string;
    lastName: string;
    address: string;
    city: string;
    telephone: string;
  }): Promise<void> {
    await this.firstNameInput.fill(ownerData.firstName);
    await this.lastNameFormInput.fill(ownerData.lastName);
    await this.addressInput.fill(ownerData.address);
    await this.cityInput.fill(ownerData.city);
    await this.telephoneInput.fill(ownerData.telephone);
    await this.submitButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async updateOwner(ownerData: Partial<{
    firstName: string;
    lastName: string;
    address: string;
    city: string;
    telephone: string;
  }>): Promise<void> {
    if (ownerData.firstName) await this.firstNameInput.fill(ownerData.firstName);
    if (ownerData.lastName) await this.lastNameFormInput.fill(ownerData.lastName);
    if (ownerData.address) await this.addressInput.fill(ownerData.address);
    if (ownerData.city) await this.cityInput.fill(ownerData.city);
    if (ownerData.telephone) await this.telephoneInput.fill(ownerData.telephone);
    await this.updateButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async gotoOwnerDetails(ownerId: string): Promise<void> {
    await this.page.goto(`/owners/${ownerId}`);
    await this.page.waitForLoadState('networkidle');
  }

  async gotoEditOwner(ownerId: string): Promise<void> {
    await this.page.goto(`/owners/${ownerId}/edit`);
    await this.page.waitForLoadState('networkidle');
  }

  // Assertions
  async assertFindOwnersPageLoaded(): Promise<void> {
    await expect(this.lastNameInput).toBeVisible();
    await expect(this.findOwnerButton).toBeVisible();
    await expect(this.addOwnerButton).toBeVisible();
  }

  async assertNewOwnerFormLoaded(): Promise<void> {
    await expect(this.firstNameInput).toBeVisible();
    await expect(this.lastNameFormInput).toBeVisible();
    await expect(this.addressInput).toBeVisible();
    await expect(this.cityInput).toBeVisible();
    await expect(this.telephoneInput).toBeVisible();
    await expect(this.submitButton).toBeVisible();
  }

  async assertOwnerDetailsPageLoaded(): Promise<void> {
    await expect(this.ownerInformation).toBeVisible();
    await expect(this.editOwnerButton).toBeVisible();
    await expect(this.addNewPetButton).toBeVisible();
  }

  async assertOwnerListDisplayed(): Promise<void> {
    await expect(this.ownersTable).toBeVisible();
  }

  async assertFormValidationErrors(): Promise<void> {
    await expect(this.formErrors).toHaveCount({ min: 1 });
  }

  async assertOwnerCreated(firstName: string, lastName: string): Promise<void> {
    await expect(this.page.locator(`text=${firstName} ${lastName}`)).toBeVisible();
  }

  async assertNoOwnersFound(): Promise<void> {
    await expect(this.page.locator('text=has not been found')).toBeVisible();
  }
}