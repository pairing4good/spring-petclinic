import { test, expect } from '@playwright/test';

test.describe('PetClinic User Acceptance Tests', () => {
  
  test('As a user, I want to navigate to the home page, so that I can access the PetClinic application', async ({ page }) => {
    // Navigate to the home page
    await page.goto('/');
    
    // Verify the page loads correctly
    await expect(page).toHaveTitle(/PetClinic :: a Spring Framework demonstration/);
    
    // Verify the welcome message is displayed
    await expect(page.getByRole('heading', { name: 'Welcome' })).toBeVisible();
    
    // Verify the main navigation is present
    await expect(page.getByRole('link', { name: 'Home' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Find Owners' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Veterinarians' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Error' })).toBeVisible();
    
    // Verify the pets image is displayed
    await expect(page.locator('img[src*="pets.png"]')).toBeVisible();
  });

  test('As a user, I want to find existing owners, so that I can search the database', async ({ page }) => {
    // Navigate to the find owners page
    await page.goto('/');
    await page.getByRole('link', { name: 'Find Owners' }).click();
    
    // Verify we're on the find owners page
    await expect(page.getByRole('heading', { name: 'Find Owners' })).toBeVisible();
    await expect(page.locator('#lastName')).toBeVisible();
    
    // Search for owners with last name "Davis"
    await page.locator('#lastName').fill('Davis');
    await page.getByRole('button', { name: 'Find Owner' }).click();
    
    // Verify search results are displayed
    await expect(page.getByRole('heading', { name: 'Owners' })).toBeVisible();
    await expect(page.getByRole('table')).toBeVisible();
    
    // Verify specific owners are in the results
    await expect(page.getByRole('link', { name: 'Betty Davis' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Harold Davis' })).toBeVisible();
    
    // Verify table headers are correct
    await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Address' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'City' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Telephone' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Pets' })).toBeVisible();
  });

  test('As a user, I want to view owner details, so that I can see pet information', async ({ page }) => {
    // Search for and select an owner
    await page.goto('/owners/find');
    await page.locator('#lastName').fill('Davis');
    await page.getByRole('button', { name: 'Find Owner' }).click();
    
    // Click on Betty Davis to view details
    await page.getByRole('link', { name: 'Betty Davis' }).click();
    
    // Verify owner details page
    await expect(page.getByRole('heading', { name: 'Owner Information' })).toBeVisible();
    
    // Verify owner information is displayed
    await expect(page.getByText('Betty Davis')).toBeVisible();
    await expect(page.getByText('638 Cardinal Ave.')).toBeVisible();
    await expect(page.getByText('Sun Prairie')).toBeVisible();
    await expect(page.getByText('6085551749')).toBeVisible();
    
    // Verify pet information section
    await expect(page.getByRole('heading', { name: 'Pets and Visits' })).toBeVisible();
    await expect(page.getByText('Basil')).toBeVisible();
    await expect(page.getByText('hamster')).toBeVisible();
    
    // Verify action buttons are present
    await expect(page.getByRole('link', { name: 'Edit Owner' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Add New Pet' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Edit Pet' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Add Visit' })).toBeVisible();
  });

  test('As a new customer, I want to add a new owner, so that I can register myself', async ({ page }) => {
    // Navigate to add new owner form
    await page.goto('/owners/find');
    await page.getByRole('link', { name: 'Add Owner' }).click();
    
    // Verify we're on the new owner form
    await expect(page.getByRole('heading', { name: 'Owner' })).toBeVisible();
    
    // Fill in owner information
    const timestamp = Date.now();
    await page.locator('#firstName').fill('John');
    await page.locator('#lastName').fill(`TestOwner${timestamp}`);
    await page.locator('#address').fill('123 Test Street');
    await page.locator('#city').fill('Test City');
    await page.locator('#telephone').fill('1234567890');
    
    // Submit the form
    await page.getByRole('button', { name: 'Add Owner' }).click();
    
    // Verify we're redirected to the owner details page
    await expect(page.getByRole('heading', { name: 'Owner Information' })).toBeVisible();
    await expect(page.getByText('John')).toBeVisible();
    await expect(page.getByText(`TestOwner${timestamp}`)).toBeVisible();
    await expect(page.getByText('123 Test Street')).toBeVisible();
    await expect(page.getByText('Test City')).toBeVisible();
    await expect(page.getByText('1234567890')).toBeVisible();
  });

  test('As a pet owner, I want to add a new pet, so that my pet can be tracked', async ({ page }) => {
    // First find an owner by doing an empty search to see all owners  
    await page.goto('/owners/find');
    await page.getByRole('button', { name: 'Find Owner' }).click();
    
    // Select the first owner in the list (this will be more reliable)
    await page.locator('table tbody tr:first-child a').click();
    
    // Click Add New Pet
    await page.getByRole('link', { name: 'Add New Pet' }).click();
    
    // Verify we're on the pet form
    await expect(page.getByRole('heading', { name: 'Pet' })).toBeVisible();
    
    // Fill in pet information
    const timestamp = Date.now();
    await page.locator('#name').fill(`TestPet${timestamp}`);
    await page.locator('#birthDate').fill('2023-01-15');
    await page.selectOption('#type', { label: 'dog' });
    
    // Submit the form
    await page.getByRole('button', { name: 'Add Pet' }).click();
    
    // Verify we're back on owner details and pet is listed
    await expect(page.getByRole('heading', { name: 'Owner Information' })).toBeVisible();
    await expect(page.getByText(`TestPet${timestamp}`)).toBeVisible();
    // Verify the pet type is visible in relation to the new pet name
    const petRow = page.locator(`text=${timestamp}`).locator('..').locator('..');
    await expect(petRow.getByText('dog')).toBeVisible();
  });

  test('As a pet owner, I want to add visits, so that veterinary care is recorded', async ({ page }) => {
    // Navigate to an owner with pets
    await page.goto('/owners/find');
    await page.locator('#lastName').fill('Davis');
    await page.getByRole('button', { name: 'Find Owner' }).click();
    await page.getByRole('link', { name: 'Betty Davis' }).click();
    
    // Click Add Visit for the pet
    await page.getByRole('link', { name: 'Add Visit' }).click();
    
    // Verify we're on the visit form
    await expect(page.getByRole('heading', { name: 'New Visit' })).toBeVisible();
    
    // Fill in visit information
    await page.locator('#date').fill('2024-01-15');
    await page.locator('#description').fill('Regular checkup and vaccination');
    
    // Submit the form
    await page.getByRole('button', { name: 'Add Visit' }).click();
    
    // Verify we're back on owner details and visit is recorded
    await expect(page.getByRole('heading', { name: 'Owner Information' })).toBeVisible();
    await expect(page.getByText('2024-01-15')).toBeVisible();
    await expect(page.getByText('Regular checkup and vaccination')).toBeVisible();
  });

  test('As a user, I want to view veterinarians, so that I can see available vets', async ({ page }) => {
    // Navigate to veterinarians page
    await page.goto('/');
    await page.getByRole('link', { name: 'Veterinarians' }).click();
    
    // Verify we're on the veterinarians page
    await expect(page.getByRole('heading', { name: 'Veterinarians' })).toBeVisible();
    
    // Verify the table structure
    await expect(page.getByRole('table')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Specialties' })).toBeVisible();
    
    // Verify some specific veterinarians are listed
    await expect(page.getByText('James Carter')).toBeVisible();
    await expect(page.getByText('Helen Leary')).toBeVisible();
    await expect(page.getByText('Linda Douglas')).toBeVisible();
    
    // Verify specialties are shown
    await expect(page.getByText('radiology')).toBeVisible();
    await expect(page.getByText('surgery')).toBeVisible();
    await expect(page.getByText('dentistry')).toBeVisible();
  });

  test('As a user, I want to navigate between vet pages, so that I can browse all vets', async ({ page }) => {
    // Navigate to veterinarians page
    await page.goto('/vets.html');
    
    // Verify pagination controls are present
    await expect(page.getByText('pages')).toBeVisible();
    
    // Check if page 2 link exists
    const page2Link = page.getByRole('link', { name: '2' });
    if (await page2Link.isVisible()) {
      // Click to go to page 2
      await page2Link.click();
      
      // Verify we're on page 2
      await expect(page).toHaveURL(/page=2/);
      await expect(page.getByRole('heading', { name: 'Veterinarians' })).toBeVisible();
      
      // Verify veterinarians table is still displayed
      await expect(page.getByRole('table')).toBeVisible();
    }
  });

  test('As a user, I want to trigger an error, so that I can see error handling', async ({ page }) => {
    // Navigate to the intentional error page
    await page.goto('/');
    await page.getByRole('link', { name: 'Error' }).click();
    
    // Verify we're on the error page
    await expect(page.getByRole('heading', { name: 'Something happened...' })).toBeVisible();
    
    // Verify error content is displayed
    await expect(page.locator('img[src*="pets.png"]')).toBeVisible();
    await expect(page.getByText('Expected: controller used to showcase what happens when an exception is thrown')).toBeVisible();
  });

  // Test to verify search with no results
  test('As a user, I want to search for non-existent owners, so that I can see appropriate messaging', async ({ page }) => {
    await page.goto('/owners/find');
    
    // Search for a non-existent owner
    await page.locator('#lastName').fill('NonExistentOwner123');
    await page.getByRole('button', { name: 'Find Owner' }).click();
    
    // Verify appropriate message is shown
    await expect(page.getByText('has not been found')).toBeVisible();
  });

  // Test to verify empty search returns all owners
  test('As a user, I want to search with empty criteria, so that I can see all owners', async ({ page }) => {
    await page.goto('/owners/find');
    
    // Submit search with empty criteria
    await page.getByRole('button', { name: 'Find Owner' }).click();
    
    // Verify we get a list of owners
    await expect(page.getByRole('heading', { name: 'Owners' })).toBeVisible();
    await expect(page.getByRole('table')).toBeVisible();
    
    // Verify multiple owners are shown
    await expect(page.getByRole('link', { name: 'George Franklin' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Betty Davis' })).toBeVisible();
  });
});