import { BasePage } from './BasePage.js';

export class OwnerFormPage extends BasePage {
  constructor(page) {
    super(page);
    this.pageHeading = page.getByRole('heading', { name: 'Owner', level: 2 });
    this.firstNameInput = page.getByRole('textbox', { name: 'First Name' });
    this.lastNameInput = page.getByRole('textbox', { name: 'Last Name' });
    this.addressInput = page.getByRole('textbox', { name: 'Address' });
    this.cityInput = page.getByRole('textbox', { name: 'City' });
    this.telephoneInput = page.getByRole('textbox', { name: 'Telephone' });
    this.submitButton = page.getByRole('button', { name: /Add Owner|Update Owner/ });
    
    // Error message selectors for validation
    this.firstNameError = page.locator('.help-block').filter({ hasText: /First Name|firstName/ });
    this.lastNameError = page.locator('.help-block').filter({ hasText: /Last Name|lastName/ });
    this.addressError = page.locator('.help-block').filter({ hasText: /Address|address/ });
    this.cityError = page.locator('.help-block').filter({ hasText: /City|city/ });
    this.telephoneError = page.locator('.help-block').filter({ hasText: /Telephone|telephone/ });
  }

  async gotoNew() {
    await this.page.goto('/owners/new');
    await this.page.waitForLoadState('networkidle');
  }

  async gotoEdit(ownerId) {
    await this.page.goto(`/owners/${ownerId}/edit`);
    await this.page.waitForLoadState('networkidle');
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.pageHeading.waitFor({ state: 'visible' });
    await this.firstNameInput.waitFor({ state: 'visible' });
    await this.lastNameInput.waitFor({ state: 'visible' });
    await this.addressInput.waitFor({ state: 'visible' });
    await this.cityInput.waitFor({ state: 'visible' });
    await this.telephoneInput.waitFor({ state: 'visible' });
    await this.submitButton.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async fillOwnerForm(ownerData) {
    if (ownerData.firstName !== undefined) {
      await this.firstNameInput.fill(ownerData.firstName);
    }
    if (ownerData.lastName !== undefined) {
      await this.lastNameInput.fill(ownerData.lastName);
    }
    if (ownerData.address !== undefined) {
      await this.addressInput.fill(ownerData.address);
    }
    if (ownerData.city !== undefined) {
      await this.cityInput.fill(ownerData.city);
    }
    if (ownerData.telephone !== undefined) {
      await this.telephoneInput.fill(ownerData.telephone);
    }
  }

  async submitForm() {
    await this.submitButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async verifyFormValidationErrors(expectedErrors) {
    if (expectedErrors.firstName) {
      await this.firstNameError.waitFor({ state: 'visible' });
    }
    if (expectedErrors.lastName) {
      await this.lastNameError.waitFor({ state: 'visible' });
    }
    if (expectedErrors.address) {
      await this.addressError.waitFor({ state: 'visible' });
    }
    if (expectedErrors.city) {
      await this.cityError.waitFor({ state: 'visible' });
    }
    if (expectedErrors.telephone) {
      await this.telephoneError.waitFor({ state: 'visible' });
    }
  }

  async clearAllFields() {
    await this.firstNameInput.fill('');
    await this.lastNameInput.fill('');
    await this.addressInput.fill('');
    await this.cityInput.fill('');
    await this.telephoneInput.fill('');
  }

  async verifyFormValues(expectedValues) {
    if (expectedValues.firstName !== undefined) {
      await expect(this.firstNameInput).toHaveValue(expectedValues.firstName);
    }
    if (expectedValues.lastName !== undefined) {
      await expect(this.lastNameInput).toHaveValue(expectedValues.lastName);
    }
    if (expectedValues.address !== undefined) {
      await expect(this.addressInput).toHaveValue(expectedValues.address);
    }
    if (expectedValues.city !== undefined) {
      await expect(this.cityInput).toHaveValue(expectedValues.city);
    }
    if (expectedValues.telephone !== undefined) {
      await expect(this.telephoneInput).toHaveValue(expectedValues.telephone);
    }
  }
}