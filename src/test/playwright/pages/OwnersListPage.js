const { BasePage } = require('./BasePage');

/**
 * OwnersListPage class for displaying search results
 */
class OwnersListPage extends BasePage {
  constructor(page) {
    super(page);
  }

  /**
   * Get number of owners displayed
   */
  async getOwnerCount() {
    const rows = await this.page.locator('tbody tr').count();
    return rows;
  }

  /**
   * Click on an owner by index (0-based)
   */
  async clickOwnerByIndex(index) {
    await this.page.locator(`tbody tr:nth-child(${index + 1}) a`).click();
  }

  /**
   * Click on an owner by name
   */
  async clickOwnerByName(name) {
    await this.page.locator(`tbody tr:has-text("${name}") a`).click();
  }

  /**
   * Get owner names displayed in the list
   */
  async getOwnerNames() {
    const names = await this.page.locator('tbody tr a').allTextContents();
    return names;
  }

  /**
   * Check if pagination is visible
   */
  async isPaginationVisible() {
    // Check for pagination controls if they exist
    return await this.page.locator('.pagination').isVisible().catch(() => false);
  }

  /**
   * Navigate to next page if pagination exists
   */
  async goToNextPage() {
    const nextButton = this.page.locator('.pagination .next');
    if (await nextButton.isVisible()) {
      await nextButton.click();
    }
  }

  /**
   * Navigate to previous page if pagination exists
   */
  async goToPreviousPage() {
    const prevButton = this.page.locator('.pagination .prev');
    if (await prevButton.isVisible()) {
      await prevButton.click();
    }
  }
}

module.exports = { OwnersListPage };