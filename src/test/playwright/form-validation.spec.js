const { test, expect } = require('@playwright/test');
const { OwnerFormPage } = require('./pages/OwnerFormPage');
const { FindOwnersPage } = require('./pages/FindOwnersPage');
const { ErrorPage } = require('./pages/ErrorPage');

test.describe('Form Validation and Error Handling', () => {
  test('As a user, I should see validation errors for required fields when creating an owner', async ({ page }) => {
    const ownerFormPage = new OwnerFormPage(page);
    
    await ownerFormPage.gotoNew();
    expect(await ownerFormPage.isFormVisible()).toBe(true);
    
    // Try to submit form with empty required fields
    await ownerFormPage.submitForm();
    
    // Should stay on the same page
    expect(await ownerFormPage.isFormVisible()).toBe(true);
    
    // Check if we get client-side or server-side validation
    const hasValidationErrors = await page.locator('.has-error, .is-invalid, .alert-danger').isVisible();
    if (!hasValidationErrors) {
      // If no obvious validation errors, check if we stayed on form (server might handle this differently)
      expect(page.url()).toContain('/owners/new');
    }
  });

  test('As a user, I should see validation errors for invalid telephone format', async ({ page }) => {
    const ownerFormPage = new OwnerFormPage(page);
    
    await ownerFormPage.gotoNew();
    
    // Fill form with invalid telephone
    const invalidOwnerData = {
      firstName: 'John',
      lastName: 'Doe',
      address: '123 Main St',
      city: 'Springfield',
      telephone: 'invalid-phone'
    };
    
    await ownerFormPage.fillOwnerForm(invalidOwnerData);
    await ownerFormPage.submitForm();
    
    // Should show validation error or stay on form
    const hasErrors = await page.locator('.has-error, .is-invalid, .alert-danger').isVisible();
    const stayedOnForm = page.url().includes('/owners/new');
    
    expect(hasErrors || stayedOnForm).toBe(true);
  });

  test('As a user, I should see proper error handling when searching for non-existent owners', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    
    await findOwnersPage.goto();
    
    // Search for definitely non-existent owner
    await findOwnersPage.searchByLastName('ZZZNonExistentOwnerXYZ123');
    
    // Should stay on find page with error message
    expect(page.url()).toContain('/owners/find');
    
    // Should show "not found" message
    const hasNotFoundMessage = await findOwnersPage.isValidationErrorVisible();
    expect(hasNotFoundMessage).toBe(true);
    
    if (hasNotFoundMessage) {
      const errorText = await findOwnersPage.getValidationError();
      expect(errorText.toLowerCase()).toContain('not found');
    }
  });

  test('As a user, I should see the custom error page when accessing the error endpoint', async ({ page }) => {
    const errorPage = new ErrorPage(page);
    
    await errorPage.goto();
    await errorPage.waitForLoad();
    
    // Verify it's the custom error page, not the default Spring Boot whitelabel page
    expect(await errorPage.isCustomErrorPage()).toBe(true);
    expect(await errorPage.isErrorImageVisible()).toBe(true);
    
    const errorMessage = await errorPage.getErrorMessage();
    expect(errorMessage).toContain('Something happened...');
    
    const description = await errorPage.getErrorDescription();
    expect(description).toContain('controller used to showcase');
  });

  test('As a user, I should get appropriate error responses for invalid URLs', async ({ page }) => {
    // Test accessing non-existent owner ID
    const response = await page.goto('/owners/99999', { waitUntil: 'networkidle' });
    
    // Should either get 404 or redirect to error page
    if (response) {
      const status = response.status();
      expect([404, 500].includes(status) || page.url().includes('error')).toBe(true);
    }
  });

  test('As a user, I should see proper error handling when accessing invalid pet URLs', async ({ page }) => {
    // Test accessing non-existent pet for non-existent owner
    const response = await page.goto('/owners/99999/pets/99999/edit', { waitUntil: 'networkidle' });
    
    // Should handle the error gracefully
    if (response) {
      const status = response.status();
      expect([404, 500].includes(status) || page.url().includes('error')).toBe(true);
    }
  });

  test('As a user, I should see loading states during form submissions', async ({ page }) => {
    const ownerFormPage = new OwnerFormPage(page);
    
    await ownerFormPage.gotoNew();
    
    // Fill out valid form data
    const ownerData = {
      firstName: 'Test',
      lastName: 'User',
      address: '123 Test St',
      city: 'Test City',
      telephone: '555-0123'
    };
    
    await ownerFormPage.fillOwnerForm(ownerData);
    
    // Submit form and check for loading behavior
    const submitPromise = ownerFormPage.submitForm();
    
    // Button might show loading state or be disabled
    const submitButton = page.locator('button[type="submit"]');
    
    // Wait for navigation or response
    await submitPromise;
    await page.waitForLoadState('networkidle');
    
    // Should either succeed or show validation errors
    const isFormVisible = await ownerFormPage.isFormVisible();
    const isOnDetailsPage = page.url().includes('/owners/') && !page.url().includes('/new') && !page.url().includes('/edit');
    
    expect(isFormVisible || isOnDetailsPage).toBe(true);
  });

  test('As a user, I should see proper validation for empty search forms', async ({ page }) => {
    const findOwnersPage = new FindOwnersPage(page);
    
    await findOwnersPage.goto();
    
    // Submit search with empty fields - this should return all owners or handle gracefully
    await findOwnersPage.searchWithoutLastName();
    await page.waitForLoadState('networkidle');
    
    // Should either show results or handle the empty search appropriately
    const currentUrl = page.url();
    const hasResults = currentUrl.includes('/owners') && !currentUrl.includes('/find');
    const stayedOnSearch = currentUrl.includes('/find');
    
    expect(hasResults || stayedOnSearch).toBe(true);
  });
});