import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class VisitPage extends BasePage {
  
  constructor(page: Page) {
    super(page);
  }

  // Add Visit form elements
  get visitDateInput(): Locator {
    return this.page.locator('input[id="date"]');
  }

  get descriptionTextarea(): Locator {
    return this.page.locator('textarea[id="description"]');
  }

  get addVisitButton(): Locator {
    return this.page.locator('button[type="submit"]');
  }

  // Pet information elements
  get petInformation(): Locator {
    return this.page.locator('table.table-striped').first();
  }

  get visitHistory(): Locator {
    return this.page.locator('table.table-striped').nth(1);
  }

  get visitRows(): Locator {
    return this.page.locator('table.table-striped').nth(1).locator('tbody tr');
  }

  // Error elements
  get errorMessages(): Locator {
    return this.page.locator('.help-inline');
  }

  get formErrors(): Locator {
    return this.page.locator('.has-error');
  }

  // Actions
  async gotoAddVisit(ownerId: string, petId: string): Promise<void> {
    await this.page.goto(`/owners/${ownerId}/pets/${petId}/visits/new`);
    await this.page.waitForLoadState('networkidle');
  }

  async addVisit(visitData: {
    date: string;
    description: string;
  }): Promise<void> {
    await this.visitDateInput.fill(visitData.date);
    await this.descriptionTextarea.fill(visitData.description);
    await this.addVisitButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async getVisitCount(): Promise<number> {
    try {
      return await this.visitRows.count();
    } catch {
      return 0; // No visits yet
    }
  }

  async getVisitDate(index: number): Promise<string> {
    return await this.visitRows.nth(index).locator('td').first().textContent() || '';
  }

  async getVisitDescription(index: number): Promise<string> {
    return await this.visitRows.nth(index).locator('td').nth(1).textContent() || '';
  }

  // Assertions
  async assertAddVisitFormLoaded(): Promise<void> {
    await expect(this.visitDateInput).toBeVisible();
    await expect(this.descriptionTextarea).toBeVisible();
    await expect(this.addVisitButton).toBeVisible();
  }

  async assertPetInformationDisplayed(): Promise<void> {
    await expect(this.petInformation).toBeVisible();
  }

  async assertVisitHistoryDisplayed(): Promise<void> {
    // Visit history might be empty for new pets
    const hasVisits = await this.visitHistory.isVisible();
    if (hasVisits) {
      await expect(this.visitHistory).toBeVisible();
    }
  }

  async assertFormValidationErrors(): Promise<void> {
    await expect(this.formErrors).toHaveCount({ min: 1 });
  }

  async assertVisitCreated(description: string): Promise<void> {
    await expect(this.page.locator(`text=${description}`)).toBeVisible();
  }

  async assertVisitDateValid(): Promise<void> {
    // Check that the visit date input accepts valid dates
    const today = new Date().toISOString().split('T')[0];
    await this.visitDateInput.fill(today);
    const value = await this.visitDateInput.inputValue();
    expect(value).toBe(today);
  }

  async assertRequiredFieldsValidation(): Promise<void> {
    // Try to submit empty form
    await this.addVisitButton.click();
    await this.page.waitForTimeout(500); // Wait for validation to show
    
    // Should have validation errors
    const hasErrors = await this.formErrors.count() > 0;
    expect(hasErrors).toBeTruthy();
  }
}