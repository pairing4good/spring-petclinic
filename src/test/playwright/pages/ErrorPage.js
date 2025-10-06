const { BasePage } = require('./BasePage');

/**
 * ErrorPage class for testing error handling
 */
class ErrorPage extends BasePage {
  constructor(page) {
    super(page);
    this.url = '/oups';
  }

  /**
   * Navigate to error page
   */
  async goto() {
    await super.goto(this.url);
  }

  /**
   * Check if error message is displayed
   */
  async isErrorMessageVisible() {
    return await this.page.locator('h2:has-text("Something happened...")').isVisible();
  }

  /**
   * Get error message text
   */
  async getErrorMessage() {
    return await this.page.locator('h2').textContent();
  }

  /**
   * Get detailed error description
   */
  async getErrorDescription() {
    return await this.page.locator('p').textContent();
  }

  /**
   * Check if error image is visible
   */
  async isErrorImageVisible() {
    return await this.page.locator('img[src*="pets.png"]').isVisible();
  }

  /**
   * Check if this is the custom error page (not whitelabel)
   */
  async isCustomErrorPage() {
    const hasCustomHeader = await this.isErrorMessageVisible();
    const hasErrorImage = await this.isErrorImageVisible();
    const notWhitelabel = !(await this.page.locator('text=Whitelabel Error Page').isVisible());
    
    return hasCustomHeader && hasErrorImage && notWhitelabel;
  }
}

module.exports = { ErrorPage };