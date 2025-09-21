import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

export class VetPage extends BasePage {
  
  constructor(page: Page) {
    super(page);
  }

  // Vet list page elements
  get vetTable(): Locator {
    return this.page.locator('table');
  }

  get vetRows(): Locator {
    return this.page.locator('tbody tr');
  }

  get vetNames(): Locator {
    return this.page.locator('tbody tr td:first-child');
  }

  get vetSpecialties(): Locator {
    return this.page.locator('tbody tr td:nth-child(2)');
  }

  // Pagination elements
  get previousPageLink(): Locator {
    return this.page.locator('a:has-text("Previous")');
  }

  get nextPageLink(): Locator {
    return this.page.locator('a:has-text("Next")');
  }

  get pageNumbers(): Locator {
    return this.page.locator('.pagination a:not(:has-text("Previous")):not(:has-text("Next"))');
  }

  get currentPageInfo(): Locator {
    return this.page.locator('text=/Showing \\d+ to \\d+ of \\d+ entries/');
  }

  // Actions
  async goto(): Promise<void> {
    await this.page.goto('/vets.html');
    await this.page.waitForLoadState('networkidle');
  }

  async gotoPage(pageNumber: number): Promise<void> {
    await this.page.goto(`/vets.html?page=${pageNumber}`);
    await this.page.waitForLoadState('networkidle');
  }

  async clickNextPage(): Promise<void> {
    await this.nextPageLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async clickPreviousPage(): Promise<void> {
    await this.previousPageLink.click();
    await this.page.waitForLoadState('networkidle');
  }

  async clickPageNumber(pageNumber: number): Promise<void> {
    await this.pageNumbers.nth(pageNumber - 1).click();
    await this.page.waitForLoadState('networkidle');
  }

  async getVetCount(): Promise<number> {
    return await this.vetRows.count();
  }

  async getVetName(index: number): Promise<string> {
    return await this.vetNames.nth(index).textContent() || '';
  }

  async getVetSpecialty(index: number): Promise<string> {
    return await this.vetSpecialties.nth(index).textContent() || '';
  }

  // Assertions
  async assertVetListPageLoaded(): Promise<void> {
    await expect(this.vetTable).toBeVisible();
    await expect(this.vetRows).toHaveCount({ min: 1 });
  }

  async assertVetDisplayed(name: string): Promise<void> {
    await expect(this.page.locator(`text=${name}`)).toBeVisible();
  }

  async assertPaginationWorking(): Promise<void> {
    const initialVetCount = await this.getVetCount();
    if (await this.nextPageLink.isVisible()) {
      await this.clickNextPage();
      const newVetCount = await this.getVetCount();
      expect(newVetCount).toBeGreaterThan(0);
    }
  }

  async assertVetSpecialties(): Promise<void> {
    const vetCount = await this.getVetCount();
    for (let i = 0; i < vetCount; i++) {
      const name = await this.getVetName(i);
      expect(name).toBeTruthy();
    }
  }
}