import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class WelcomePage extends BasePage {
  
  constructor(page: Page) {
    super(page);
  }

  // Page-specific elements
  get welcomeHeading(): Locator {
    return this.page.locator('h2');
  }

  get petImage(): Locator {
    return this.page.locator('img[src*="pets.png"]');
  }

  // Page actions
  async goto(): Promise<void> {
    await this.page.goto('/');
    await this.page.waitForLoadState('networkidle');
  }

  // Page assertions
  async assertWelcomePageElements(): Promise<void> {
    await expect(this.welcomeHeading).toBeVisible();
    await expect(this.welcomeHeading).toContainText('Welcome');
    await expect(this.petImage).toBeVisible();
  }

  async assertPageLoaded(): Promise<void> {
    await this.assertPageTitle(/PetClinic/);
    await this.assertWelcomePageElements();
    await this.assertNavigation();
  }
}