const { BasePage } = require('./BasePage');

/**
 * FindOwnersPage class for searching owners
 */
class FindOwnersPage extends BasePage {
  constructor(page) {
    super(page);
    this.url = '/owners/find';
  }

  /**
   * Navigate to find owners page
   */
  async goto() {
    await super.goto(this.url);
  }

  /**
   * Search for owners by last name
   */
  async searchByLastName(lastName) {
    await this.page.locator('#lastName').fill(lastName);
    await this.page.locator('button[type="submit"]').click();
  }

  /**
   * Click Find Owner button without entering a last name
   */
  async searchWithoutLastName() {
    await this.page.locator('button[type="submit"]').click();
  }

  /**
   * Check if search form is visible
   */
  async isSearchFormVisible() {
    return await this.page.locator('#lastName').isVisible();
  }

  /**
   * Check if "Add Owner" link is visible
   */
  async isAddOwnerLinkVisible() {
    return await this.page.locator('a[href="/owners/new"]').isVisible();
  }

  /**
   * Click "Add Owner" link
   */
  async clickAddOwner() {
    await this.page.locator('a[href="/owners/new"]').click();
  }

  /**
   * Get validation error message
   */
  async getValidationError() {
    const errorElement = await this.page.locator('.help-inline');
    return await errorElement.textContent();
  }

  /**
   * Check if validation error is visible
   */
  async isValidationErrorVisible() {
    return await this.page.locator('.help-inline').isVisible();
  }
}

module.exports = { FindOwnersPage };