const { BasePage } = require('./BasePage');

/**
 * PetFormPage class for adding and editing pets
 */
class PetFormPage extends BasePage {
  constructor(page) {
    super(page);
  }

  /**
   * Navigate to new pet form for owner
   */
  async gotoNew(ownerId) {
    await super.goto(`/owners/${ownerId}/pets/new`);
  }

  /**
   * Navigate to edit pet form
   */
  async gotoEdit(ownerId, petId) {
    await super.goto(`/owners/${ownerId}/pets/${petId}/edit`);
  }

  /**
   * Fill pet form with data
   */
  async fillPetForm(petData) {
    if (petData.name) {
      await this.page.locator('#name').fill(petData.name);
    }
    if (petData.birthDate) {
      await this.page.locator('#birthDate').fill(petData.birthDate);
    }
    if (petData.type) {
      await this.page.locator('#type').selectOption({ label: petData.type });
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
    return await this.page.locator('#pet').isVisible();
  }

  /**
   * Get available pet types
   */
  async getPetTypes() {
    const options = await this.page.locator('#type option').allTextContents();
    return options.filter(option => option.trim().length > 0);
  }

  /**
   * Get form title
   */
  async getFormTitle() {
    return await this.page.locator('h2').textContent();
  }

  /**
   * Check if field has validation error
   */
  async hasFieldError(fieldName) {
    const fieldGroup = this.page.locator(`#${fieldName}`).locator('..').locator('..');
    return await fieldGroup.locator('.has-error').isVisible();
  }
}

module.exports = { PetFormPage };