const { BasePage } = require('./BasePage');

/**
 * VisitFormPage class for adding visits to pets
 */
class VisitFormPage extends BasePage {
  constructor(page) {
    super(page);
  }

  /**
   * Navigate to new visit form for pet
   */
  async gotoNew(ownerId, petId) {
    await super.goto(`/owners/${ownerId}/pets/${petId}/visits/new`);
  }

  /**
   * Fill visit form with data
   */
  async fillVisitForm(visitData) {
    if (visitData.date) {
      await this.page.locator('#date').fill(visitData.date);
    }
    if (visitData.description) {
      await this.page.locator('#description').fill(visitData.description);
    }
  }

  /**
   * Submit the form
   */
  async submitForm() {
    await this.page.locator('button[type="submit"]').click();
  }

  /**
   * Check if form is visible
   */
  async isFormVisible() {
    return await this.page.locator('#visit').isVisible();
  }

  /**
   * Get form title
   */
  async getFormTitle() {
    return await this.page.locator('h2').textContent();
  }

  /**
   * Get pet name displayed in form
   */
  async getPetName() {
    const petInfo = await this.page.locator('table tr:has-text("Name") td').textContent();
    return petInfo?.trim();
  }

  /**
   * Check if field has validation error
   */
  async hasFieldError(fieldName) {
    const fieldGroup = this.page.locator(`#${fieldName}`).locator('..').locator('..');
    return await fieldGroup.locator('.has-error').isVisible();
  }
}

module.exports = { VisitFormPage };