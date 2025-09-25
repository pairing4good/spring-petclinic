import { BasePage } from './BasePage.js';

export class ErrorPage extends BasePage {
  constructor(page) {
    super(page);
    this.errorHeading = page.getByRole('heading', { name: 'Something happened...', level: 2 });
    this.errorImage = page.locator('img').first(); // Error page image
    this.springLogo = page.locator('img[alt="VMware Tanzu Logo"]');
  }

  async goto() {
    await this.page.goto('/oups');
    await this.page.waitForLoadState('networkidle');
  }

  async verifyPageStructure() {
    await this.verifyNavigationPresent();
    await this.errorHeading.waitFor({ state: 'visible' });
    await this.errorImage.waitFor({ state: 'visible' });
    await this.springLogo.waitFor({ state: 'visible' });
    await this.verifyPageTitle('PetClinic');
  }

  async verifyErrorPageContent() {
    // Verify that we're on an error page with appropriate heading
    await this.errorHeading.waitFor({ state: 'visible' });
    
    // Check that the page shows an error state
    const headingText = await this.errorHeading.textContent();
    if (!headingText.includes('Something happened')) {
      throw new Error(`Expected error heading to contain "Something happened", but got "${headingText}"`);
    }
  }

  async verify500StatusCode() {
    // This method verifies that the error page represents a 500 server error
    // by checking the response status when navigating to /oups
    const response = await this.page.request.get('/oups');
    const status = response.status();
    
    if (status !== 500) {
      throw new Error(`Expected 500 status code for error page, but got ${status}`);
    }
  }

  async verifyNavigationStillWorks() {
    // Verify that navigation still works from the error page
    await this.verifyNavigationPresent();
    
    // Test that we can navigate away from the error page
    await this.navigateToHome();
    await this.page.waitForLoadState('networkidle');
    
    // Verify we're no longer on the error page
    const currentUrl = this.page.url();
    if (currentUrl.includes('/oups')) {
      throw new Error('Expected to navigate away from error page, but still on error page');
    }
  }
}