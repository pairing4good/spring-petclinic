export class BasePage {
  constructor(page) {
    this.page = page;
    // Navigation elements using more specific locators to avoid ambiguity
    this.homeLink = page.getByRole('link', { name: /Home/i }).filter({ hasText: 'Home' });
    this.findOwnersLink = page.getByRole('link', { name: /Find Owners/i });
    this.veterinariansLink = page.getByRole('link', { name: /Veterinarians/i });
    this.errorLink = page.getByRole('link', { name: /Error/i });
    this.logo = page.locator('.navbar-brand');
  }

  async navigateToHome() {
    await this.homeLink.click();
  }

  async navigateToFindOwners() {
    await this.findOwnersLink.click();
  }

  async navigateToVeterinarians() {
    await this.veterinariansLink.click();
  }

  async navigateToError() {
    await this.errorLink.click();
  }

  async verifyNavigationPresent() {
    await this.homeLink.waitFor({ state: 'visible' });
    await this.findOwnersLink.waitFor({ state: 'visible' });
    await this.veterinariansLink.waitFor({ state: 'visible' });
    await this.errorLink.waitFor({ state: 'visible' });
  }

  async verifyPageTitle(expectedTitle) {
    await this.page.waitForLoadState('networkidle');
    const title = await this.page.title();
    if (!title.includes(expectedTitle)) {
      throw new Error(`Expected title to contain "${expectedTitle}", but got "${title}"`);
    }
  }
}