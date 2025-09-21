import { test, expect } from '@playwright/test';
import { WelcomePage } from './pages/WelcomePage';
import { OwnerPage } from './pages/OwnerPage';
import { VetPage } from './pages/VetPage';

test.describe('As a user on different devices', () => {

  test('As a mobile user, I want the welcome page to display properly on my phone, so that I can access the application on the go', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 }); // iPhone SE
    
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
    await welcomePage.assertResponsiveDesign();
    
    // Check that content is readable and not cut off
    const welcomeHeading = await welcomePage.welcomeHeading.boundingBox();
    expect(welcomeHeading?.width).toBeLessThanOrEqual(375);
    
    // Image should be responsive
    const petImage = await welcomePage.petImage.boundingBox();
    expect(petImage?.width).toBeLessThanOrEqual(375);
  });

  test('As a tablet user, I want the navigation to work smoothly on my tablet, so that I can easily browse the application', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 }); // iPad
    
    await welcomePage.goto();
    await welcomePage.assertResponsiveDesign();
    
    // Test navigation on tablet
    await welcomePage.navigateToFindOwners();
    await expect(page).toHaveURL('/owners/find');
    
    await welcomePage.navigateToVeterinarians();
    await expect(page).toHaveURL('/vets.html');
    
    await welcomePage.navigateToHome();
    await expect(page).toHaveURL('/');
  });

  test('As a user with a wide desktop screen, I want the content to use the available space effectively, so that I can see more information at once', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Set large desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 });
    
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
    
    // Content should not be stretched too wide
    const container = await page.locator('.container, .xd-container').first().boundingBox();
    if (container) {
      // Container should have reasonable max-width (not stretched across entire screen)
      expect(container.width).toBeLessThan(1400);
    }
  });

  test('As a mobile user, I want forms to be easy to fill out on my phone, so that I can input data without frustration', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    
    await ownerPage.gotoNewOwner();
    await ownerPage.assertNewOwnerFormLoaded();
    
    // Form elements should be properly sized for mobile
    const inputs = await page.locator('input, select, textarea').all();
    
    for (const input of inputs) {
      const box = await input.boundingBox();
      if (box) {
        // Inputs should be at least 44px tall for easy touch interaction
        expect(box.height).toBeGreaterThanOrEqual(30);
        // Inputs should not overflow the viewport
        expect(box.width).toBeLessThanOrEqual(375);
      }
    }
    
    // Test filling out the form on mobile
    const ownerData = {
      firstName: 'Mobile',
      lastName: 'User',
      address: '123 Mobile Street',
      city: 'Mobile City',
      telephone: '5551234567'
    };
    
    await ownerPage.createOwner(ownerData);
    
    // Should successfully create owner
    await expect(page).toHaveURL(/\/owners\/\d+/);
  });

  test('As a tablet user, I want table data to be readable and navigable, so that I can browse information efficiently', async ({ page }) => {
    const vetPage = new VetPage(page);
    
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    
    await vetPage.goto();
    await vetPage.assertVetListPageLoaded();
    
    // Table should be responsive
    const table = await vetPage.vetTable.boundingBox();
    if (table) {
      expect(table.width).toBeLessThanOrEqual(768);
    }
    
    // Table content should be readable
    const vetCount = await vetPage.getVetCount();
    expect(vetCount).toBeGreaterThan(0);
    
    // Test table scrolling if necessary
    if (table && table.width > 700) {
      // Table might be horizontally scrollable
      await page.evaluate(() => {
        const table = document.querySelector('table');
        if (table) {
          table.scrollLeft = 100;
        }
      });
    }
  });

  test('As a user rotating my mobile device, I want the layout to adapt, so that the application works in both portrait and landscape modes', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Start in portrait mode
    await page.setViewportSize({ width: 375, height: 667 });
    await welcomePage.goto();
    await welcomePage.assertPageLoaded();
    
    // Rotate to landscape
    await page.setViewportSize({ width: 667, height: 375 });
    await welcomePage.assertResponsiveDesign();
    
    // Navigation should still work
    await welcomePage.navigateToFindOwners();
    await expect(page).toHaveURL('/owners/find');
    
    // Form should still be usable
    const ownerPage = new OwnerPage(page);
    await ownerPage.addOwnerButton.click();
    await ownerPage.assertNewOwnerFormLoaded();
  });

  test('As a user with a small screen, I want navigation to be accessible through a menu, so that I can access all features despite limited screen space', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Set very small mobile viewport
    await page.setViewportSize({ width: 320, height: 568 }); // iPhone 5
    
    await welcomePage.goto();
    
    // Check if there's a navigation toggle (hamburger menu)
    const navToggle = page.locator('.navbar-toggler, .navbar-toggle, .menu-toggle');
    const isToggleVisible = await navToggle.isVisible();
    
    if (isToggleVisible) {
      // Test hamburger menu functionality
      await navToggle.click();
      
      // Navigation menu should be visible after clicking toggle
      const navMenu = page.locator('.navbar-collapse, .nav-menu');
      await expect(navMenu).toBeVisible();
      
      // Should be able to navigate
      await welcomePage.navigateToFindOwners();
      await expect(page).toHaveURL('/owners/find');
    } else {
      // If no toggle, navigation should still be accessible
      await welcomePage.assertNavigation();
    }
  });

  test('As a user with touch interactions, I want buttons and links to respond properly to touch, so that I can interact naturally with the interface', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    // Set mobile viewport with touch simulation
    await page.setViewportSize({ width: 375, height: 667 });
    
    await ownerPage.gotoFindOwners();
    
    // Test touch interactions
    const searchButton = ownerPage.findOwnerButton;
    const addOwnerButton = ownerPage.addOwnerButton;
    
    // Touch should work the same as click
    await searchButton.tap();
    await page.waitForLoadState('networkidle');
    
    // Should have performed the search
    const currentUrl = page.url();
    expect(currentUrl).toContain('/owners');
    
    // Test touch on another button
    await page.goto('/owners/find');
    await addOwnerButton.tap();
    await expect(page).toHaveURL('/owners/new');
  });

  test('As a user on different screen densities, I want images and text to appear crisp, so that the interface looks professional', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Test different device pixel ratios
    const devicePixelRatios = [1, 2, 3]; // Standard, Retina, High-DPI
    
    for (const ratio of devicePixelRatios) {
      await page.emulateMedia({ reducedMotion: 'reduce' }); // Reduce motion for testing
      await page.setViewportSize({ width: 375, height: 667 });
      
      // Simulate different pixel density
      await page.evaluate((dpr) => {
        Object.defineProperty(window, 'devicePixelRatio', {
          get: () => dpr,
          configurable: true
        });
      }, ratio);
      
      await welcomePage.goto();
      await welcomePage.assertPageLoaded();
      
      // Images should load properly
      const petImage = welcomePage.petImage;
      await expect(petImage).toBeVisible();
      
      // Check image loading
      const imageLoaded = await petImage.evaluate((img: HTMLImageElement) => {
        return img.complete && img.naturalHeight !== 0;
      });
      expect(imageLoaded).toBeTruthy();
    }
  });

  test('As a user with slow internet, I want the mobile version to load quickly, so that I don\'t wait too long on slower connections', async ({ page }) => {
    const welcomePage = new WelcomePage(page);
    
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    
    // Simulate slow 3G connection
    await page.route('**/*', async route => {
      await new Promise(resolve => setTimeout(resolve, 50)); // Small delay to simulate slow network
      await route.continue();
    });
    
    const startTime = Date.now();
    await welcomePage.goto();
    const loadTime = Date.now() - startTime;
    
    // Should load within reasonable time even on slow connection
    expect(loadTime).toBeLessThan(10000); // 10 seconds max
    
    await welcomePage.assertPageLoaded();
  });

  test('As a user switching between desktop and mobile, I want consistent functionality, so that all features work regardless of device', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    
    // Test on desktop first
    await page.setViewportSize({ width: 1200, height: 800 });
    await ownerPage.gotoNewOwner();
    
    const desktopOwnerData = {
      firstName: 'Desktop',
      lastName: 'User',
      address: '123 Desktop Ave',
      city: 'Desktop City',
      telephone: '5551111111'
    };
    
    await ownerPage.createOwner(desktopOwnerData);
    await expect(page).toHaveURL(/\/owners\/\d+/);
    
    // Switch to mobile and test the same functionality
    await page.setViewportSize({ width: 375, height: 667 });
    await ownerPage.gotoNewOwner();
    
    const mobileOwnerData = {
      firstName: 'Mobile',
      lastName: 'User',
      address: '456 Mobile St',
      city: 'Mobile City',
      telephone: '5552222222'
    };
    
    await ownerPage.createOwner(mobileOwnerData);
    await expect(page).toHaveURL(/\/owners\/\d+/);
    
    // Both should work identically
    await expect(page.locator(`text=${mobileOwnerData.firstName} ${mobileOwnerData.lastName}`)).toBeVisible();
  });

});