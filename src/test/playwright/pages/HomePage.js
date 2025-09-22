const { BasePage } = require('./BasePage');

/**
 * HomePage class representing the welcome/home page
 */
class HomePage extends BasePage {
  constructor(page) {
    super(page);
    this.url = '/';
  }

  /**
   * Navigate to home page
   */
  async goto() {
    await super.goto(this.url);
  }

  /**
   * Check if welcome header is visible
   */
  async isWelcomeHeaderVisible() {
    return await this.page.locator('h2').first().isVisible();
  }

  /**
   * Get welcome message text
   */
  async getWelcomeMessage() {
    return await this.page.locator('h2').first().textContent();
  }

  /**
   * Check if Spring logo is visible
   */
  async isSpringLogoVisible() {
    return await this.page.locator('img[alt*="Logo"]').isVisible();
  }

  /**
   * Check if the page has the correct layout
   */
  async hasCorrectLayout() {
    const navbar = await this.isNavigationMenuVisible();
    const welcome = await this.isWelcomeHeaderVisible();
    const logo = await this.isSpringLogoVisible();
    
    return navbar && welcome && logo;
  }
}

module.exports = { HomePage };