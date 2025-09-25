import { BasePage } from './BasePage.js';

export class OwnersListPage extends BasePage {
  constructor(page) {
    super(page);
    this.pageHeading = page.getByRole('heading', { name: 'Owners', level: 2 });
    this.ownersTable = page.locator('table').first();
    this.tableHeaders = page.locator('table th');
    this.ownerRows = page.locator('table tbody tr');
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.pageHeading.waitFor({ state: 'visible' });
    await this.ownersTable.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async verifyTableHeaders() {
    const expectedHeaders = ['Name', 'Address', 'City', 'Telephone', 'Pets'];
    const headers = await this.tableHeaders.allTextContents();
    
    for (const expectedHeader of expectedHeaders) {
      if (!headers.some(header => header.includes(expectedHeader))) {
        throw new Error(`Expected header "${expectedHeader}" not found in table headers: ${headers.join(', ')}`);
      }
    }
  }

  async getOwnerCount() {
    await this.ownerRows.first().waitFor({ state: 'visible' });
    return await this.ownerRows.count();
  }

  async clickOwnerByName(ownerName) {
    // Use more specific locator to find owner link by name
    const ownerLink = this.page.getByRole('link', { name: ownerName });
    await ownerLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async verifyOwnerInList(ownerName) {
    const ownerLink = this.page.getByRole('link', { name: ownerName });
    await ownerLink.waitFor({ state: 'visible' });
  }
}