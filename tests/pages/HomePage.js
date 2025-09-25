import { BasePage } from './BasePage.js';

export class HomePage extends BasePage {
  constructor(page) {
    super(page);
    this.welcomeHeading = page.getByRole('heading', { name: 'Welcome', level: 2 });
    this.petImage = page.locator('img').first(); // Main pet image
    this.springLogo = page.locator('img[alt="VMware Tanzu Logo"]');
  }

  async goto() {
    await this.page.goto('/');
    await this.page.waitForLoadState('networkidle');
  }

  async verifyHomePageContent() {
    await this.welcomeHeading.waitFor({ state: 'visible' });
    await this.petImage.waitFor({ state: 'visible' });
    await this.springLogo.waitFor({ state: 'visible' });
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.verifyHomePageContent();
    await this.verifyPageTitle('PetClinic');
  }
}