const { BasePage } = require('./BasePage');

/**
 * VeterinariansPage class for viewing veterinarians
 */
class VeterinariansPage extends BasePage {
  constructor(page) {
    super(page);
    this.url = '/vets.html';
  }

  /**
   * Navigate to veterinarians page
   */
  async goto() {
    await super.goto(this.url);
  }

  /**
   * Get list of veterinarians
   */
  async getVeterinarians() {
    const vets = [];
    const rows = await this.page.locator('tbody tr').count();
    
    for (let i = 0; i < rows; i++) {
      const row = this.page.locator(`tbody tr:nth-child(${i + 1})`);
      const name = await row.locator('td:nth-child(1)').textContent();
      const specialties = await row.locator('td:nth-child(2)').textContent();
      
      vets.push({
        name: name?.trim(),
        specialties: specialties?.trim()
      });
    }
    
    return vets;
  }

  /**
   * Check if veterinarians table is visible
   */
  async isVetsTableVisible() {
    return await this.page.locator('table.table-striped').isVisible();
  }

  /**
   * Get number of veterinarians
   */
  async getVetCount() {
    return await this.page.locator('tbody tr').count();
  }

  /**
   * Check if page header is correct
   */
  async getPageHeader() {
    return await this.page.locator('h2').textContent();
  }
}

module.exports = { VeterinariansPage };