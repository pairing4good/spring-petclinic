/**
 * Base Page class with common functionality for all pages
 */
class BasePage {
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to a specific URL
   */
  async goto(url) {
    await this.page.goto(url);
  }

  /**
   * Get page title
   */
  async getTitle() {
    return await this.page.title();
  }

  /**
   * Check if navigation menu is visible
   */
  async isNavigationMenuVisible() {
    return await this.page.locator('nav.navbar').isVisible();
  }

  /**
   * Click on Home link in navigation
   */
  async clickHome() {
    await this.page.locator('nav.navbar a[href="/"]').click();
  }

  /**
   * Click on Find Owners link in navigation
   */
  async clickFindOwners() {
    await this.page.locator('nav.navbar a[href="/owners/find"]').click();
  }

  /**
   * Click on Veterinarians link in navigation
   */
  async clickVeterinarians() {
    await this.page.locator('nav.navbar a[href="/vets.html"]').click();
  }

  /**
   * Click on Error link in navigation
   */
  async clickError() {
    await this.page.locator('nav.navbar a[href="/oups"]').click();
  }

  /**
   * Wait for page to load completely
   */
  async waitForLoad() {
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Check if success message is visible
   */
  async isSuccessMessageVisible() {
    return await this.page.locator('.alert-success').isVisible();
  }

  /**
   * Check if error message is visible
   */
  async isErrorMessageVisible() {
    return await this.page.locator('.alert-danger').isVisible();
  }

  /**
   * Get success message text
   */
  async getSuccessMessage() {
    return await this.page.locator('.alert-success').textContent();
  }

  /**
   * Get error message text
   */
  async getErrorMessage() {
    return await this.page.locator('.alert-danger').textContent();
  }
}

module.exports = { BasePage };