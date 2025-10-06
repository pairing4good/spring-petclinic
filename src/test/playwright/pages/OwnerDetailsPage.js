const { BasePage } = require('./BasePage');

/**
 * OwnerDetailsPage class for viewing owner details
 */
class OwnerDetailsPage extends BasePage {
  constructor(page) {
    super(page);
  }

  /**
   * Navigate to owner details page
   */
  async goto(ownerId) {
    await super.goto(`/owners/${ownerId}`);
  }

  /**
   * Get owner information
   */
  async getOwnerInfo() {
    const table = this.page.locator('table.table-striped').first();
    
    const name = await table.locator('tr:has-text("Name") td').textContent();
    const address = await table.locator('tr:has-text("Address") td').textContent();
    const city = await table.locator('tr:has-text("City") td').textContent();
    const telephone = await table.locator('tr:has-text("Telephone") td').textContent();

    return {
      name: name?.trim(),
      address: address?.trim(),
      city: city?.trim(),
      telephone: telephone?.trim()
    };
  }

  /**
   * Click Edit Owner button
   */
  async clickEditOwner() {
    await this.page.locator('a.btn:has-text("Edit")').click();
  }

  /**
   * Click Add New Pet button
   */
  async clickAddNewPet() {
    await this.page.locator('a.btn:has-text("Add New Pet")').click();
  }

  /**
   * Get list of pets
   */
  async getPets() {
    const petsTable = this.page.locator('table.table-striped').nth(1);
    const petRows = await petsTable.locator('tbody tr').count();
    
    const pets = [];
    for (let i = 0; i < petRows; i++) {
      const row = petsTable.locator(`tbody tr:nth-child(${i + 1})`);
      const nameElement = row.locator('dd').first();
      const name = await nameElement.textContent();
      pets.push({ name: name?.trim() });
    }
    
    return pets;
  }

  /**
   * Click on a pet by name
   */
  async clickPetByName(petName) {
    await this.page.locator(`dt:has-text("Name") + dd:has-text("${petName}")`).click();
  }

  /**
   * Click Add Visit for a specific pet
   */
  async clickAddVisitForPet(petName) {
    const petRow = this.page.locator(`tr:has-text("${petName}")`);
    await petRow.locator('a:has-text("Add Visit")').click();
  }

  /**
   * Check if owner information is displayed correctly
   */
  async isOwnerInfoVisible() {
    const table = this.page.locator('table.table-striped').first();
    return await table.isVisible();
  }

  /**
   * Check if pets section is visible
   */
  async isPetsSectionVisible() {
    return await this.page.locator('h2:has-text("Pets and Visits")').isVisible();
  }

  /**
   * Get number of pets for this owner
   */
  async getPetCount() {
    const pets = await this.getPets();
    return pets.length;
  }

  /**
   * Check if success/error messages are displayed and then hidden
   */
  async waitForMessageToDisappear() {
    // Wait for success/error messages to be hidden by JavaScript
    await this.page.waitForTimeout(3500); // Messages should disappear after 3 seconds
  }
}

module.exports = { OwnerDetailsPage };