import { BasePage } from './BasePage.js';

export class OwnerDetailsPage extends BasePage {
  constructor(page) {
    super(page);
    this.ownerInfoHeading = page.getByRole('heading', { name: 'Owner Information', level: 2 });
    this.petsAndVisitsHeading = page.getByRole('heading', { name: 'Pets and Visits', level: 2 });
    this.editOwnerLink = page.getByRole('link', { name: 'Edit Owner' });
    this.addNewPetLink = page.getByRole('link', { name: 'Add New Pet' });
    this.ownerInfoTable = page.locator('table').first();
    this.petsTable = page.locator('table').nth(1);
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.ownerInfoHeading.waitFor({ state: 'visible' });
    await this.petsAndVisitsHeading.waitFor({ state: 'visible' });
    await this.editOwnerLink.waitFor({ state: 'visible' });
    await this.addNewPetLink.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async verifyOwnerInformation(expectedInfo) {
    await this.ownerInfoTable.waitFor({ state: 'visible' });
    
    if (expectedInfo.name) {
      await this.page.getByText(expectedInfo.name).waitFor({ state: 'visible' });
    }
    if (expectedInfo.address) {
      await this.page.getByText(expectedInfo.address).waitFor({ state: 'visible' });
    }
    if (expectedInfo.city) {
      await this.page.getByText(expectedInfo.city).waitFor({ state: 'visible' });
    }
    if (expectedInfo.telephone) {
      await this.page.getByText(expectedInfo.telephone).waitFor({ state: 'visible' });
    }
  }

  async clickEditOwner() {
    await this.editOwnerLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async clickAddNewPet() {
    await this.addNewPetLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async verifyPetPresent(petName) {
    await this.page.getByText(petName).waitFor({ state: 'visible' });
  }

  async clickEditPetByName(petName) {
    // Find the edit pet link in the same row as the pet name
    const petRow = this.page.locator('tr').filter({ hasText: petName });
    const editPetLink = petRow.getByRole('link', { name: 'Edit Pet' });
    await editPetLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async clickAddVisitByPetName(petName) {
    // Find the add visit link in the same row as the pet name
    const petRow = this.page.locator('tr').filter({ hasText: petName });
    const addVisitLink = petRow.getByRole('link', { name: 'Add Visit' });
    await addVisitLink.click();
    await this.page.waitForLoadState('networkidle');
  }
}