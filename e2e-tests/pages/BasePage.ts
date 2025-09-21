import { Page, Locator, expect } from '@playwright/test';

export class BasePage {
  protected page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  // Common navigation elements
  get homeLink(): Locator {
    return this.page.locator('a[href="/"]');
  }

  get findOwnersLink(): Locator {
    return this.page.locator('a[href="/owners/find"]');
  }

  get veterinariansLink(): Locator {
    return this.page.locator('a[href="/vets.html"]');
  }

  get errorLink(): Locator {
    return this.page.locator('a[href="/oups"]');
  }

  // Common actions
  async navigateToHome(): Promise<void> {
    await this.homeLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async navigateToFindOwners(): Promise<void> {
    await this.findOwnersLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async navigateToVeterinarians(): Promise<void> {
    await this.veterinariansLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async navigateToError(): Promise<void> {
    await this.errorLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  // Common assertions
  async assertPageTitle(expectedTitle: string): Promise<void> {
    await expect(this.page).toHaveTitle(expectedTitle);
  }

  async assertNavigation(): Promise<void> {
    await expect(this.homeLink).toBeVisible();
    await expect(this.findOwnersLink).toBeVisible();
    await expect(this.veterinariansLink).toBeVisible();
    await expect(this.errorLink).toBeVisible();
  }

  async assertResponsiveDesign(): Promise<void> {
    // Check that navigation is present and responsive
    await expect(this.page.locator('.navbar')).toBeVisible();
    
    // For mobile, check if navbar toggle is visible
    const viewport = this.page.viewportSize();
    if (viewport && viewport.width <= 768) {
      await expect(this.page.locator('.navbar-toggler')).toBeVisible();
    }
  }
}