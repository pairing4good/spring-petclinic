import { BasePage } from './BasePage.js';

export class VeterinariansPage extends BasePage {
  constructor(page) {
    super(page);
    this.pageHeading = page.getByRole('heading', { name: 'Veterinarians', level: 2 });
    this.vetsTable = page.locator('table').first();
    this.tableHeaders = page.locator('table th');
    this.vetRows = page.locator('table tbody tr');
    
    // Pagination elements
    this.paginationSection = page.locator('div').filter({ hasText: /pages/ });
    this.currentPageIndicator = page.locator('span').filter({ hasText: /^\d+$/ });
    this.nextPageLink = this.page.getByRole('link', { name: '2' });
    this.previousButton = page.locator('span').filter({ hasText: 'Previous' });
    this.nextButton = page.locator('span').filter({ hasText: 'Next' });
  }

  async goto() {
    await this.page.goto('/vets.html');
    await this.page.waitForLoadState('networkidle');
  }

  async gotoPage(pageNumber) {
    await this.page.goto(`/vets.html?page=${pageNumber}`);
    await this.page.waitForLoadState('networkidle');
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.pageHeading.waitFor({ state: 'visible' });
    await this.vetsTable.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async verifyTableHeaders() {
    const expectedHeaders = ['Name', 'Specialties'];
    const headers = await this.tableHeaders.allTextContents();
    
    for (const expectedHeader of expectedHeaders) {
      if (!headers.some(header => header.includes(expectedHeader))) {
        throw new Error(`Expected header "${expectedHeader}" not found in table headers: ${headers.join(', ')}`);
      }
    }
  }

  async getVetCount() {
    await this.vetRows.first().waitFor({ state: 'visible' });
    return await this.vetRows.count();
  }

  async verifyVetInList(vetName) {
    await this.page.getByText(vetName).waitFor({ state: 'visible' });
  }

  async verifySpecialtyInList(specialty) {
    await this.page.getByText(specialty).waitFor({ state: 'visible' });
  }

  async verifyPaginationPresent() {
    await this.paginationSection.waitFor({ state: 'visible' });
  }

  async clickNextPage() {
    const nextPageLink = this.page.getByRole('link', { name: '2' });
    if (await nextPageLink.isVisible()) {
      await nextPageLink.click();
      await this.page.waitForLoadState('networkidle');
      return true;
    }
    return false;
  }

  async verifyCurrentPage(expectedPage) {
    const currentPageText = await this.currentPageIndicator.textContent();
    if (currentPageText !== expectedPage.toString()) {
      throw new Error(`Expected current page to be ${expectedPage}, but got ${currentPageText}`);
    }
  }

  async getAllVetNames() {
    await this.vetRows.first().waitFor({ state: 'visible' });
    const vetCells = this.page.locator('table tbody tr td:first-child');
    return await vetCells.allTextContents();
  }

  async verifyVetSpecialties(vetName, expectedSpecialties) {
    const vetRow = this.page.locator('tr').filter({ hasText: vetName });
    const specialtiesCell = vetRow.locator('td:nth-child(2)');
    const specialtiesText = await specialtiesCell.textContent();
    
    for (const specialty of expectedSpecialties) {
      if (!specialtiesText.includes(specialty)) {
        throw new Error(`Expected specialty "${specialty}" not found for vet "${vetName}"`);
      }
    }
  }
}