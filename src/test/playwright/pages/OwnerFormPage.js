const { BasePage } = require('./BasePage');

/**
 * OwnerFormPage class for creating and updating owners
 */
class OwnerFormPage extends BasePage {
  constructor(page) {
    super(page);
  }

  /**
   * Navigate to new owner form
   */
  async gotoNew() {
    await super.goto('/owners/new');
  }

  /**
   * Navigate to edit owner form
   */
  async gotoEdit(ownerId) {
    await super.goto(`/owners/${ownerId}/edit`);
  }

  /**
   * Fill owner form with data
   */
  async fillOwnerForm(ownerData) {
    if (ownerData.firstName) {
      await this.page.locator('#firstName').fill(ownerData.firstName);
    }
    if (ownerData.lastName) {
      await this.page.locator('#lastName').fill(ownerData.lastName);
    }
    if (ownerData.address) {
      await this.page.locator('#address').fill(ownerData.address);
    }
    if (ownerData.city) {
      await this.page.locator('#city').fill(ownerData.city);
    }
    if (ownerData.telephone) {
      await this.page.locator('#telephone').fill(ownerData.telephone);
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
    return await this.page.locator('#add-owner-form').isVisible();
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

  /**
   * Get validation error for a specific field
   */
  async getFieldError(fieldName) {
    const fieldGroup = this.page.locator(`#${fieldName}`).locator('..').locator('..');
    const errorElement = fieldGroup.locator('.help-inline');
    return await errorElement.textContent();
  }

  /**
   * Clear all form fields
   */
  async clearForm() {
    await this.page.locator('#firstName').fill('');
    await this.page.locator('#lastName').fill('');
    await this.page.locator('#address').fill('');
    await this.page.locator('#city').fill('');
    await this.page.locator('#telephone').fill('');
  }

  /**
   * Check if submit button text matches expected value
   */
  async getSubmitButtonText() {
    return await this.page.locator('button[type="submit"]').textContent();
  }
}

module.exports = { OwnerFormPage };