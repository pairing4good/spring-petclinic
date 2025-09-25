import { BasePage } from './BasePage.js';

export class FindOwnersPage extends BasePage {
  constructor(page) {
    super(page);
    this.pageHeading = page.getByRole('heading', { name: 'Find Owners', level: 2 });
    this.lastNameInput = page.locator('#lastName');
    this.findOwnerButton = page.getByRole('button', { name: 'Find Owner' });
    this.addOwnerLink = page.getByRole('link', { name: 'Add Owner' });
  }

  async goto() {
    await this.page.goto('/owners/find');
    await this.page.waitForLoadState('networkidle');
  }

  async searchByLastName(lastName) {
    await this.lastNameInput.fill(lastName);
    await this.findOwnerButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async clickAddOwner() {
    await this.addOwnerLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.pageHeading.waitFor({ state: 'visible' });
    await this.lastNameInput.waitFor({ state: 'visible' });
    await this.findOwnerButton.waitFor({ state: 'visible' });
    await this.addOwnerLink.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async verifySearchFormValidation() {
    // Test empty search
    await this.lastNameInput.fill('');
    await this.findOwnerButton.click();
    await this.page.waitForLoadState('networkidle');
  }
}